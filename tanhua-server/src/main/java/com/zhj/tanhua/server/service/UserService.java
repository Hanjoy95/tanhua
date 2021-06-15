package com.zhj.tanhua.server.service;

import com.zhj.tanhua.common.exception.BaseRunTimeException;
import com.zhj.tanhua.user.api.UserApi;
import com.zhj.tanhua.user.dto.UserInfoDto;
import com.zhj.tanhua.user.dto.UserDto;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author huanjie.zhuang
 * @date 2021/6/13
 */
@Service
public class UserService {

    @DubboReference(version = "1.0.0", url = "dubbo://127.0.0.1:20880")
    UserApi userApi;

    /**
     * 用户登录
     *
     * @param phone 用户手机号
     * @param checkCode  验证码
     * @return UserDto
     */
    public UserDto login(String phone, String checkCode) {
        return userApi.login(phone, checkCode);
    }

    /**
     * 发送验证码
     *
     * @param phone 用户手机号
     * @return UserDto
     */
    public UserDto sentCheckCode(String phone) {
        return userApi.sentCheckCode(phone);
    }

    /**
     * 根据token查询用户数据
     *
     * @param token 用户token
     * @return UserDto
     */
    public UserDto getUserByToken(String token) {
        return userApi.getUserByToken(token);
    }

    /**
     * 完善个人信息
     *
     * @param token 用户token
     * @param userInfoDto 用户信息
     */
    public void saveUserInfo(String token, UserInfoDto userInfoDto) {

        UserDto user = getUserByToken(token);
        if (null == user) {
            throw new BaseRunTimeException("当前登录用户token已失效，请重新登录");
        }
        userInfoDto.setUserId(user.getId());
        userApi.saveUserInfo(userInfoDto);
    }

    /**
     * 上传头像
     *
     * @param token 用户token
     * @param file 用户头像图片文件
     */
    public void saveAvatar(String token, MultipartFile file) {

        UserDto user = getUserByToken(token);
        if (null == user) {
            throw new BaseRunTimeException("当前登录用户token已失效，请重新登录");
        }
        userApi.saveAvatar(user.getId(), file);
    }

    /**
     * 获取用户详细信息
     *
     * @param token 用户token
     * @param userId 用户ID
     * @return UserInfoDto
     */
    public UserInfoDto getUserInfo(String token, Long userId) {

        UserDto user = getUserByToken(token);
        if (null == user) {
            throw new BaseRunTimeException("当前登录用户token已失效，请重新登录");
        }

        return userApi.getUserInfo(userId);
    }

    /**
     * 获取用户详细信息列表
     *
     * @param userIds 用户ID列表
     * @param sex 性别
     * @param age 年龄
     * @param city 城市
     * @return List<UserInfoDto>
     */
    List<UserInfoDto> getUserInfos(List<Long> userIds, String sex, Integer age, String city) {
        return userApi.getUserInfos(userIds, sex, age, city);
    }
}
