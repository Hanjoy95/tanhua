package com.zhj.tanhua.server.service;

import com.zhj.tanhua.common.constant.ThConstant;
import com.zhj.tanhua.server.web.threadlocal.UserThreadLocal;
import com.zhj.tanhua.user.api.UserInfoApi;
import com.zhj.tanhua.user.pojo.dto.UserInfoDto;
import com.zhj.tanhua.user.pojo.po.User;
import com.zhj.tanhua.user.pojo.to.UserInfoTo;
import com.zhj.tanhua.user.api.UserApi;
import com.zhj.tanhua.user.pojo.to.UserTo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 用户模块的服务层
 *
 * @author huanjie.zhuang
 * @date 2021/6/13
 */
@Service
public class UserService {

    @DubboReference(version = "1.0", url = ThConstant.USER_URL)
    UserApi userApi;
    @DubboReference(version = "1.0", url = ThConstant.USER_URL)
    UserInfoApi userInfoApi;

    /**
     * 验证码登录
     *
     * @param phone 用户手机号
     * @param checkCode 验证码
     * @return 返回用户信息
     */
    public UserTo loginWithCheckCode(String phone, String checkCode) {
        return userApi.loginWithCheckCode(phone, checkCode);
    }

    /**
     * 密码登录
     *
     * @param phone 用户手机号
     * @param password 密码
     * @return 返回用户信息
     */
    public UserTo loginWithPassword(String phone, String password) {
        return userApi.loginWithPassword(phone, password);
    }

    /**
     * 发送验证码
     *
     * @param phone 用户手机号
     * @return 返回验证码
     */
    public String sentCheckCode(String phone) {
        return userApi.sentCheckCode(phone);
    }

    /**
     * 根据token查询用户数据
     *
     * @param token 用户token
     * @return 返回用户信息
     */
    public User getUserByToken(String token) {
        return userApi.getUserByToken(token);
    }

    /**
     * 修改密码
     *
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    public void modifyPassword(String oldPassword, String newPassword) {
        userApi.modifyPassword(UserThreadLocal.get().getId(), oldPassword, newPassword);
    }

    /**
     * 上传头像
     *
     * @param file 用户头像图片文件
     */
    public void saveAvatar(MultipartFile file) {

        User user = UserThreadLocal.get();
        userInfoApi.saveAvatar(user.getId(), file);
    }

    /**
     * 完善个人信息
     *
     * @param userInfoDto 用户信息
     */
    public void saveUserInfo(UserInfoDto userInfoDto) {

        User user = UserThreadLocal.get();
        userInfoDto.setUserId(user.getId());
        userInfoApi.saveUserInfo(userInfoDto);
    }

    /**
     * 获取用户详细信息
     *
     * @param userId 用户ID
     * @return 返回用户详细
     */
    public UserInfoTo getUserInfo(Long userId) {
        return userInfoApi.getUserInfo(userId);
    }

    /**
     * 获取用户详细信息列表
     *
     * @param userIds 用户ID列表
     * @param sex 性别
     * @param age 年龄
     * @param city 城市
     * @return 返回用户详细列表
     */
    List<UserInfoTo> getUserInfos(List<Long> userIds, Integer sex, Integer age, String city) {
        return userInfoApi.getUserInfos(userIds, sex, age, city);
    }
}
