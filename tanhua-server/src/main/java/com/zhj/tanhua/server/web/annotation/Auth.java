package com.zhj.tanhua.server.web.annotation;

/**
 * 验证请求携带的token是否合法
 *
 * @author huanjie.zhuang
 * @date 2021/6/19
 */

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Auth {
}
