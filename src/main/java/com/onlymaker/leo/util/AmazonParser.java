package com.onlymaker.leo.util;

import com.onlymaker.leo.data.Entry;
import com.onlymaker.leo.data.EntryRepository;
import com.onlymaker.leo.data.Log;
import com.onlymaker.leo.data.LogRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by jibo on 2016/10/18.
 */
@Component
public class AmazonParser {
    private static final Logger logger = LoggerFactory.getLogger(AmazonParser.class);
    private static final String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36";

    @Autowired
    EntryRepository entryRepository;
    @Autowired
    LogRepository logRepository;
    @Autowired
    JavaMailSenderImpl mailSender;
    @Value("${logging.path}")
    String path;

    public Long parse(Entry entry, String ... args) {
        Log result = new Log(entry);
        Document doc;
        try {
            if(args.length == 2 && new File(args[0]).exists()) {
                logger.info("handle {}: {}", args[0], args[1]);
                doc = Jsoup.parse(new File(args[0]), args[1]);
            } else {
                logger.info("handle {}: {}", entry.getAsin(), entry.getUrl());
                doc = Jsoup.connect(entry.getUrl()).userAgent(userAgent).get();
            }
            String cache = path + File.separator + UUID.randomUUID();
            Files.write(Paths.get(cache), doc.toString().getBytes());
            result.setCache(cache);
            // parse thumb
            if(entry.getThumb().equals(Entry.DEFAULT_THUMB)) {
                String thumb = doc.select("#altImages img").eq(0).attr("src");
                if(!StringUtils.isEmpty(thumb)) {
                    entry.setThumb(thumb);
                    entryRepository.save(entry);
                }
            }
            // parse rank
            String rankArea = doc.select("#SalesRank").text().trim();
            List<Integer> ranks = getNumbers(rankArea);
            if(ranks.size() != 0) {
                result.setRank(ranks.get(0));
                result.setSubRank(ranks.get(ranks.size() - 1));
            }
            // parse review
            String review = doc.select("#summaryStars").text().trim();
            if(StringUtils.isEmpty(review)) {
                logger.debug("select data by css name");
                review = doc.select(".averageStarRating").text().trim();
                if(!StringUtils.isEmpty(review)) {
                    String averageStars = review.substring(0, review.indexOf(' '));
                    result.setAverageStar(Float.parseFloat(averageStars));
                    String totalReviews = doc.select(".totalReviewCount").text().trim();
                    result.setTotalReview(Integer.parseInt(totalReviews));
                }
            } else {
                logger.debug("select data by element id");
                List<Integer> numbers = getNumbers(review);
                if(numbers.size() != 0) {
                    result.setTotalReview(numbers.get(numbers.size() - 1));
                    String number = numbers.get(0).toString();
                    if(1 < number.length()) number = number.charAt(0) + "." + number.substring(1);
                    result.setAverageStar(Float.parseFloat(number));
                }
            }
            // parse review star
            Elements reviewTable = doc.select("#histogramTable");
            for(int i = 0; i < 5; i++) {
                String selector = String.format("tr:eq(%d) td:eq(2)", i);
                String star = reviewTable.select(selector).text().trim();
                if(StringUtils.isEmpty(star)) setStar(result, i, 0f);
                else if(star.charAt(star.length() - 1) == '%') {
                    star = star.substring(0, star.length() - 1);
                    setStar(result, i, Float.parseFloat(star) / 100);
                } else {
                    if(result.getTotalReview() != 0) {
                        setStar(result, i, Float.parseFloat(star) / result.getTotalReview());
                    } else {
                        setStar(result, i, Float.parseFloat(star));
                    }
                }
            }
        } catch (Exception e) {
            MimeMessage mimeMailMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMailMessage, true);
            mimeMessageHelper.setSubject("Parser Failed");
            mimeMessageHelper.setFrom(mailSender.getUsername());
            mimeMessageHelper.setTo("jibo@outlook.com");
            String content = "<h3>Parser Failed!</h3><br/><br/><strong>" + entry.getAsin() + ":</strong>&nbsp"
                    + entry.getUrl() +  "<br/><br/>" + e.getMessage();
            mimeMessageHelper.setText(content, true);
            mailSender.send(mimeMailMessage);
            entry.setError(entry.getError() + 1);
            entryRepository.save(entry);
        } finally {
            logger.info("result: {}", result.toString());
            if(result.validate()) {
                logRepository.save(result);
                return result.getId();
            } else {
                logger.info("validate failure: {}", result.getCache());
                return 0l;
            }
        }
    }

    private void setStar(Log log, int i, float star) {
        switch (i) {
            case 0:
                log.setStarFive(star);
                break;
            case 1:
                log.setStarFour(star);
                break;
            case 2:
                log.setStarThree(star);
                break;
            case 3:
                log.setStarTwo(star);
                break;
            case 4:
                log.setStarOne(star);
                break;
        }
    }

    private List<Integer> getNumbers(String s) {
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if((48 <= c && c <= 57) || c == ' ') {
                sb.append(c);
            }
        }
        String[] array = sb.toString().trim().split(" ");
        List<Integer> result = new ArrayList<>();
        for(int i = 0; i < array.length; i++) {
            String r = array[i].trim();
            if(!StringUtils.isEmpty(r)) result.add(Integer.valueOf(r));
        }
        return result;
    }
}
