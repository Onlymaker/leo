package com.onlymaker.leo.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.mail.internet.MimeMessage;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class MailSender {
    private static final Logger logger = LoggerFactory.getLogger(MailSender.class);

    @Autowired
    JavaMailSenderImpl mailSender;

    @Test
    public void send() throws Exception {
        MimeMessage mimeMailMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMailMessage, true);
        mimeMessageHelper.setSubject("Leo Notification");
        mimeMessageHelper.setFrom(mailSender.getUsername());
        mimeMessageHelper.setTo("jibo@outlook.com");
        String content = "<h3>Dear all:</h3>测试邮件<br/><br/><strong>注意:</strong>&nbsp这是一封测试邮件<br/><br/>";
        mimeMessageHelper.setText(content, true);
        mailSender.send(mimeMailMessage);
    }
}
