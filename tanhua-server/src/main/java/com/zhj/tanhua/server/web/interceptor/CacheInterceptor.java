package com.zhj.tanhua.server.web.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author huanjie.zhuang
 * @date 2021/6/17
 */
@Component
public class CacheInterceptor implements HandlerInterceptor {

    public static Boolean isCache = false;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Value("${tanhua.cache.enable}")
    private Boolean enable;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 未开启缓存
        if (!enable) {
            return true;
        }

        // 非GET、POST的请求不进行缓存处理
        String method = request.getMethod();
        if (!StringUtils.equalsAnyIgnoreCase(method, "GET", "POST")) {
            return true;
        }

        // 通过缓存做命中，查询redisKey
        String redisKey = createRedisKey(request);
        String data = this.redisTemplate.opsForValue().get(redisKey);
        if (StringUtils.isEmpty(data)) {
            isCache = true;
            return true;
        }

        // 将data数据进行响应
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        response.getWriter().write(data);

        return false;
    }

    public static String createRedisKey(HttpServletRequest request) throws Exception {

        String paramStr = request.getRequestURI();
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (parameterMap.isEmpty()) {
            // 请求体的数据只能读取一次，需要进行包装Request进行解决
            paramStr += IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
        } else {
            paramStr += MAPPER.writeValueAsString(request.getParameterMap());
        }

        String authorization = request.getHeader("authorization");
        if (StringUtils.isNotEmpty((authorization))) {
            paramStr += "_" + authorization;
        }

        return "SERVER_DATA_" + DigestUtils.md5Hex(paramStr);
    }
}
