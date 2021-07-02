package com.zhj.tanhua.server.config;

import com.zhj.tanhua.server.web.interceptor.CacheInterceptor;
import com.zhj.tanhua.server.web.interceptor.TokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 注册自定义拦截器
 *
 * @author huanjie.zhuang
 * @date 2021/6/17
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private CacheInterceptor cacheInterceptor;
    @Autowired
    private TokenInterceptor tokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(cacheInterceptor)
                .addPathPatterns("/tanhua/user/info",
                                 "/tanhua/recommend/todayBest",
                                 "/tanhua/recommend/users");
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/doc.html**");
    }
}
