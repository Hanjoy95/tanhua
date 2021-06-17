package com.zhj.tanhua.server.web.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhj.tanhua.server.web.interceptor.CacheInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.time.Duration;

/**
 * @author huanjie.zhuang
 * @date 2021/6/17
 */
@Slf4j
@ControllerAdvice
public class ThResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return CacheInterceptor.isCache;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType, Class aClass,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        try {
            String redisKey = CacheInterceptor.createRedisKey(((ServletServerHttpRequest) request).getServletRequest());
            String redisValue;
            if (body instanceof String) {
                redisValue = (String) body;
            } else {
                redisValue = MAPPER.writeValueAsString(body);
            }
            this.redisTemplate.opsForValue().set(redisKey, redisValue, Duration.ofHours(1));
        } catch (Exception e) {
            log.error("数据存储缓存失败", e);
        }
        CacheInterceptor.isCache = false;
        return body;
    }
}
