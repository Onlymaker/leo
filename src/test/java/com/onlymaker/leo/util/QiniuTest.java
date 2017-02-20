package com.onlymaker.leo.util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class QiniuTest {
    @Autowired
    Qiniu qiniu;

    @Test
    public void upload() throws IOException {
        ClassPathResource image = new ClassPathResource("smoke.jpg");
        Assert.assertNotNull(qiniu.upload(image.getFile().getAbsolutePath(), "smoke.jpg"));
    }
}
