package com.zhj.tanhua.user.api;

import com.zhj.tanhua.user.pojo.po.User;
import com.zhj.tanhua.user.pojo.to.UserTo;

/**
 * 用户dubbo接口
 *
 * @author huanjie.zhuang
 * @date 2021/6/12
 */
public interface UserApi {

    /**
     * 用户登录
     *
     * @param phone 用户手机号
     * @param checkCode  验证码
     * @return UserTo
     */
    UserTo login(String phone, String checkCode);

    /**
     * 发送验证码
     *
     * @param phone 用户手机号
     * @return String
     */
    String sentCheckCode(String phone);

    /**
     * 根据token查询用户数据
     *
     * @param token 用户token
     * @return User
     */
    User getUserByToken(String token);

    /**
     * 修改密码
     *
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    void modifyPassword(Long userId, String oldPassword, String newPassword);
}
