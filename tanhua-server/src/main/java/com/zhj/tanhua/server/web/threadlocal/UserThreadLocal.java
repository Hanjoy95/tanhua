package com.zhj.tanhua.server.web.threadlocal;

import com.zhj.tanhua.user.pojo.po.User;

/**
 * 存储用户基本信息的线程变量
 *
 * @author huanjie.zhuang
 * @date 2021/6/19
 */
public class UserThreadLocal {

    private static final ThreadLocal<User> LOCAL = new ThreadLocal<>();

    UserThreadLocal() {}

    public static void set(User user) {
        LOCAL.set(user);
    }

    public static User get() {
        return LOCAL.get();
    }
}
