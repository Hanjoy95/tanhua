package com.zhj.tanhua.server.web.filter;

import com.zhj.tanhua.server.web.wrapper.ThHttpServletRequestWrapper;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author huanjie.zhuang
 * @date 2021/6/17
 */
@Component
public class ThOncePerRequestFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (!(request instanceof ThHttpServletRequestWrapper)) {
            request = new ThHttpServletRequestWrapper(request);
        }
        filterChain.doFilter(request, response);
    }
}
