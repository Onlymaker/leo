package com.onlymaker.leo.task;

import com.onlymaker.leo.data.EntryRepository;
import com.onlymaker.leo.util.AmazonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class Crawl {
    private static final Logger logger = LoggerFactory.getLogger(Crawl.class);
    private static final int MAX_ERROR = 5;
    @Autowired
    EntryRepository entryRepository;
    @Autowired
    AmazonParser parser;

    @Scheduled(cron = "${task.crawl}")
    public void cron() {
        logger.info("cron crawl: {}", new Date().toString());
        entryRepository.findByErrorLessThanEqual(MAX_ERROR).forEach(entry -> {
            parser.parse(entry);
        });
    }
}