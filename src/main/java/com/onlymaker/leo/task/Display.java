package com.onlymaker.leo.task;

import com.onlymaker.leo.data.EntryRepository;
import com.onlymaker.leo.data.Log;
import com.onlymaker.leo.data.LogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class Display {
    private static final Logger logger = LoggerFactory.getLogger(Display.class);
    @Autowired
    EntryRepository entryRepository;
    @Autowired
    LogRepository logRepository;

    @Scheduled(cron = "${task.display}")
    public void cron() {
        logger.info("cron display: {}", new Date().toString());
        entryRepository.findAll().forEach(entry -> {
            if(entry.getStatus() == 0) {
                long count = logRepository.countByEntry(entry);
                if(count > 1) {
                    List<Log> logs = logRepository.findFirst2ByEntryOrderByIdDesc(entry);
                    logger.debug("{}:{}, {}:{}",
                            logs.get(0).getId(), logs.get(0).getTotalReview(),
                            logs.get(1).getId(), logs.get(1).getTotalReview());
                    if(logs.get(0).getTotalReview().intValue() != logs.get(1).getTotalReview().intValue()) {
                        logger.info("{} totalReview changed", entry.getUrl());
                        entry.setStatus(1);
                        entry.setUpdateTime(logs.get(1).getCreateTime());
                        entryRepository.save(entry);
                    }
                } else {
                    // do nothing
                }
            }
        });
    }
}