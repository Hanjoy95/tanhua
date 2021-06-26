package com.zhj.tanhua.server.service;

import com.zhj.tanhua.server.web.threadlocal.UserThreadLocal;
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

    @DubboReference(version = "1.0", url = "dubbo://127.0.0.1:19100")
    UserApi userApi;

    /**
     * 用户登录
     *
     * @param phone 用户手机号
     * @param checkCode  验证码
     * @return UserTo
     */
    public UserTo login(String phone, String checkCode) {
        return userApi.login(phone, checkCode);
    }

    /**
     * 发送验证码
     *
     * @param phone 用户手机号
     * @return String
     */
    public String sentCheckCode(String phone) {
        return userApi.sentCheckCode(phone);
    }

    /**
     * 根据token查询用户数据
     *
     * @param token 用户token
     * @return User
     */
    public User getUserByToken(String token) {
        return userApi.getUserByToken(token);
    }

    /**
     * 完善个人信息
     *
     * @param userInfoDto 用户信息
     */
    public void saveUserInfo(UserInfoDto userInfoDto) {

        User user = UserThreadLocal.get();
        userInfoDto.setUserId(user.getId());
        userApi.saveUserInfo(userInfoDto);
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
        userApi.saveAvatar(user.getId(), file);
    }

    /**
     * 获取用户详细信息
     *
     * @param userId 用户ID
     * @return UserInfoTo
     */
    public UserInfoTo getUserInfo(Long userId) {
        return userApi.getUserInfo(userId);
    }

    /**
     * 获取用户详细信息列表
     *
     * @param userIds 用户ID列表
     * @param sex 性别
     * @param age 年龄
     * @param city 城市
     * @return List<UserInfoTo>
     */
    List<UserInfoTo> getUserInfos(List<Long> userIds, Integer sex, Integer age, String city) {
        return userApi.getUserInfos(userIds, sex, age, city);
    }
}
