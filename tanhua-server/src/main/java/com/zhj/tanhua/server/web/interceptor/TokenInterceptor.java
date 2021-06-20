package com.zhj.tanhua.server.web.interceptor;

import com.zhj.tanhua.common.constant.ThConstant;
import com.zhj.tanhua.common.exception.TokenExpiredException;
import com.zhj.tanhua.server.service.UserService;
import com.zhj.tanhua.server.web.annotation.Auth;
import com.zhj.tanhua.server.web.threadlocal.UserThreadLocal;
import com.zhj.tanhua.user.pojo.po.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截请求中携带的token，根据token获取用户基本信息
 *
 * @author huanjie.zhuang
 * @date 2021/6/19
 */
@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Auth annotation = handlerMethod.getMethod().getAnnotation(Auth.class);
            if (null == annotation) {
                return true;
            }
        }

        String token = request.getHeader(ThConstant.AUTHORIZATION);
        User user;
        try {
            user = userService.getUserByToken(token);
        } catch (TokenExpiredException e) {
            response.setStatus(401);
            return false;
        }

        // 存储到当前线程中
        UserThreadLocal.set(user);

        return true;
    }
}
