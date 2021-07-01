package com.zhj.tanhua.user.api;

import com.zhj.tanhua.user.pojo.dto.UserInfoDto;
import com.zhj.tanhua.user.pojo.to.UserInfoTo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 用户信息dubbo接口
 *
 * @author huanjie.zhuang
 * @date 2021/7/1
 */
public interface UserInfoApi {

    /**
     * 上传头像
     *
     * @param userId 用户ID
     * @param file 用户头像图片文件
     */
    void saveAvatar(Long userId, MultipartFile file);

    /**
     * 完善个人信息
     *
     * @param userInfoDto 用户信息
     */
    void saveUserInfo(UserInfoDto userInfoDto);

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
