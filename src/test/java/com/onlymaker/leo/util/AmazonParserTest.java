package com.onlymaker.leo.util;

import com.onlymaker.leo.data.Entry;
import com.onlymaker.leo.data.EntryRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by jibo on 2016/10/18.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class AmazonParserTest {
    @Autowired
    EntryRepository entryRepository;
    @Autowired
    AmazonParser parser;

    @Test
    public void parse() {
        Entry entry = new Entry("B01CCQOES8", "AOUS");
        entryRepository.save(entry);
        parser.parse(entry);
    }
}
