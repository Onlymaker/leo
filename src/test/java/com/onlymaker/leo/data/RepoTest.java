package com.onlymaker.leo.data;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class RepoTest {
    @Autowired
    EntryRepository entryRepository;
    @Autowired
    LogRepository logRepository;
    @Autowired
    OrderInfoRepository orderInfoRepository;

    @Test
    public void test() {
        entryRepository.findAll().forEach(entry -> {
            long count = logRepository.countByEntry(entry);
            if(count == 0) {
                Assert.assertNull(logRepository.findFirstByEntryOrderByIdDesc(entry));
            } else if (count == 1) {
                Assert.assertNotNull(logRepository.findFirstByEntryOrderByIdDesc(entry));
            } else {
                List<Log> logs = logRepository.findFirst2ByEntryOrderByIdDesc(entry);
                Assert.assertTrue(logs.size() == 2);
                System.out.println("log[0] validate: " + logs.get(0).validate());
                System.out.println("log[1] validate: " + logs.get(1).validate());
            }
        });

        long count = orderInfoRepository.countByOrderId("test");
        Assert.assertEquals(count, 0);
        OrderInfo o = orderInfoRepository.save(new OrderInfo("test", "test", "test"));
        count = orderInfoRepository.countByOrderId("test");
        Assert.assertEquals(count, 1);
        orderInfoRepository.delete(o);
        count = orderInfoRepository.countByOrderId("test");
        Assert.assertEquals(count, 0);
    }
}
