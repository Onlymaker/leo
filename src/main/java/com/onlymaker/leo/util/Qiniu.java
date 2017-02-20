package com.onlymaker.leo.util;

import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Qiniu {
    private final static Logger logger = LoggerFactory.getLogger(Qiniu.class);
    @Value("${qiniu.access_key}")
    String ACCESS_KEY;
    @Value("${qiniu.secret_key}")
    String SECRET_KEY;
    @Value("${qiniu.domain}")
    String DOMAIN;
    @Value("${qiniu.bucket}")
    String BUCKET;

    //简单上传，使用默认策略，只需要设置上传的空间名就可以了
    public String getUpToken() {
        Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
        return auth.uploadToken(BUCKET);
    }

    public String upload(String file, String key) {
        String url = null;
        try {
            Zone z = Zone.autoZone();
            Configuration c = new Configuration(z);
            UploadManager uploadManager = new UploadManager(c);
            uploadManager.put(file, key, getUpToken());
            url = DOMAIN + key;
            logger.debug("qiniu upload: {}", url);
        } catch (QiniuException e) {
            logger.error("Upload {} failure", file, e);
        } finally {
            return url;
        }
    }
}
