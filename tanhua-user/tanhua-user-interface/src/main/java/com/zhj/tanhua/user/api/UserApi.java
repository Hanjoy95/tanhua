package com.zhj.tanhua.user.api;

import com.zhj.tanhua.user.pojo.dto.UserInfoDto;
import com.zhj.tanhua.user.pojo.po.User;
import com.zhj.tanhua.user.pojo.to.UserInfoTo;
import com.zhj.tanhua.user.pojo.to.UserTo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
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
     * 完善个人信息
     *
     * @param userInfoDto 用户信息
     */
    void saveUserInfo(UserInfoDto userInfoDto);

    /**
     * 上传头像
     *
     * @param userId 用户ID
     * @param file 用户头像图片文件
     */
    void saveAvatar(Long userId, MultipartFile file);

    /**
     * 获取用户详细信息
     *
     * @param userId 用户ID
     * @return UserInfoTo
     */
    UserInfoTo getUserInfo(Long userId);

    /**
     * 获取用户详细信息列表
     *
     * @param userIds 用户ID列表
     * @param sex 性别
     * @param age 年龄
     * @param city 城市
     * @return List<UserInfoTo>
     */
    List<UserInfoTo> getUserInfos(List<Long> userIds, Integer sex, Integer age, String city);
}
