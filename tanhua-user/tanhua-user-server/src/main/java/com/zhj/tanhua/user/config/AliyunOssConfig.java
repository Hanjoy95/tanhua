package com.zhj.tanhua.user.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author huanjie.zhuang
 * @date 2021/6/19
 */
@Configuration
public class AliyunOssConfig {

    @Value("${aliyun.endpoint}")
    private String endpoint;
    @Value("${aliyun.accessKeyId}")
    private String accessKeyId;
    @Value("${aliyun.secretAccessKey}")
    private String secretAccessKey;

    @Bean
    public OSS getOssClient() {
        return new OSSClientBuilder().build(endpoint, accessKeyId, secretAccessKey);
    }
}
