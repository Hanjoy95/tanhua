package com.zhj.tanhua.user.api.impl;

import com.aliyun.oss.OSS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zhj.tanhua.common.constant.ThConstant;
import com.zhj.tanhua.common.enums.ImageTypeEnum;
import com.zhj.tanhua.common.exception.BaseException;
import com.zhj.tanhua.user.api.UserInfoApi;
import com.zhj.tanhua.user.dao.UserDao;
import com.zhj.tanhua.user.dao.UserInfoDao;
import com.zhj.tanhua.user.enums.EduEnum;
import com.zhj.tanhua.user.enums.SexEnum;
import com.zhj.tanhua.user.enums.StatusEnum;
import com.zhj.tanhua.user.pojo.dto.UserInfoDto;
import com.zhj.tanhua.user.pojo.po.User;
import com.zhj.tanhua.user.pojo.po.UserInfo;
import com.zhj.tanhua.user.pojo.to.UserInfoTo;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户信息dubbo接口实现
 *
 * @author huanjie.zhuang
 * @date 2021/7/1
 */
@DubboService(version = "1.0")
public class UserInfoApiImpl implements UserInfoApi {

    @Autowired
    private UserDao userDao;
    @Autowired
    private UserInfoDao userInfoDao;
    @Autowired
    private OSS oss;

    @Value("${aliyun.bucketName}")
    private String bucketName;
    @Value("${aliyun.urlPrefix}")
    private String urlPrefix;

    /**
     * 上传用户头像
     *
     * @param userId 用户ID
     * @param file 用户头像图片文件
     */
    @Override
    @SneakyThrows
    public void saveAvatar(Long userId, MultipartFile file) {

        // 校验图片文件后缀名
        if (ImageTypeEnum.UNKNOWN.equals(ImageTypeEnum.getType(StringUtils
                .substringAfterLast(file.getOriginalFilename(), ThConstant.SPOT)))) {
            throw new BaseException("image type error, only support jpg, jpeg, gif, png");
        }

        // 文件路径, avatar/{userId}/{currentTimeMillis}.{imageType}
        String fileUrl = "avatar/" + userId + "/" + System.currentTimeMillis() + ThConstant.SPOT +
                StringUtils.substringAfterLast(file.getOriginalFilename(), ThConstant.SPOT);

        // 上传阿里云OSS
        try {
            oss.putObject(bucketName, fileUrl, new ByteArrayInputStream(file.getBytes()));
        } catch (Exception e) {
            throw new BaseException(e.getMessage());
        }

        userInfoDao.update(null, Wrappers.<UserInfo>lambdaUpdate()
                .set(UserInfo::getAvatar, urlPrefix + fileUrl)
                .eq(UserInfo::getUserId, userId));
    }

    /**
     * 完善个人信息
     *
     * @param userInfoDto 用户信息
     */
    @Override
    public void saveUserInfo(UserInfoDto userInfoDto) {

        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(userInfoDto, userInfo);
        userInfo.setTags(StringUtils.join(userInfoDto.getTags().toArray(), ThConstant.SPOT));
        userInfo.setSex(userInfoDto.getSex().getValue());
        userInfo.setEdu(userInfoDto.getEdu().getValue());
        userInfo.setStatus(userInfoDto.getStatus().getValue());

        try {
            userInfoDao.insert(userInfo);
        } catch (DuplicateKeyException e) {
            userInfoDao.update(userInfo, Wrappers.<UserInfo>lambdaQuery()
                    .eq(UserInfo::getUserId, userInfoDto.getUserId()));
        }
    }

    /**
     * 获取用户详细信息
     *
     * @param userId 用户ID
     * @return UserInfoTo
     */
    @Override
    public UserInfoTo getUserInfo(Long userId) {

        UserInfo userInfo = userInfoDao.selectOne(Wrappers.<UserInfo>lambdaQuery().eq(UserInfo::getUserId, userId));
        UserInfoTo userInfoTo = new UserInfoTo();
        BeanUtils.copyProperties(userInfo, userInfoTo);
        userInfoTo.setPhone(userDao.selectById(userId).getPhone());
        userInfoTo.setTags(Arrays.asList(userInfo.getTags().split(ThConstant.COMMA)));
        userInfoTo.setSex(SexEnum.getType(userInfo.getSex()));
        userInfoTo.setEdu(EduEnum.getType(userInfo.getEdu()));
        userInfoTo.setStatus(StatusEnum.getType(userInfo.getStatus()));

        return userInfoTo;
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
    @Override
    public List<UserInfoTo> getUserInfos(List<Long> userIds, Integer sex, Integer age, String city) {

        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("user_id", userIds);
        if (null != sex) {
            queryWrapper.eq("sex", sex);
        }
        if (null != age) {
            queryWrapper.lt("age", age);
        }
        if (StringUtils.isNotEmpty(city)) {
            queryWrapper.eq("city", city);
        }

        List<UserInfo> userInfos = userInfoDao.selectList(queryWrapper);
        Map<Long, String> userMap = userDao.selectBatchIds(userIds)
                .stream().collect(Collectors.toMap(User::getId, User::getPhone));
        List<UserInfoTo> userInfoToList = new ArrayList<>();
        for (UserInfo userInfo : userInfos) {
            UserInfoTo userInfoTo = new UserInfoTo();
            BeanUtils.copyProperties(userInfo, userInfoTo);
            userInfoTo.setPhone(userMap.get(userInfo.getUserId()));
            userInfoTo.setTags(Arrays.asList(userInfo.getTags().split(ThConstant.COMMA)));
            userInfoTo.setSex(SexEnum.getType(userInfo.getSex()));
            userInfoTo.setEdu(EduEnum.getType(userInfo.getEdu()));
            userInfoTo.setStatus(StatusEnum.getType(userInfo.getStatus()));
            userInfoToList.add(userInfoTo);
        }

        return userInfoToList;
    }
}
