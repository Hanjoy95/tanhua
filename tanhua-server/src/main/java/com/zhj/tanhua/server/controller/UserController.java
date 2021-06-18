package com.zhj.tanhua.server.controller;

import com.zhj.tanhua.common.exception.BaseException;
import com.zhj.tanhua.common.result.ResponseResult;
import com.zhj.tanhua.server.service.UserService;
import com.zhj.tanhua.user.pojo.dto.UserInfoDto;
import com.zhj.tanhua.user.pojo.po.User;
import com.zhj.tanhua.user.pojo.to.UserInfoTo;
import com.zhj.tanhua.user.pojo.to.UserTo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author huanjie.zhuang
 * @date 2021/6/13
 */
@Api(tags = "用户")
@RequestMapping("tanhua/user")
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    private static final String AUTHORIZATION = "authorization";

    /**
     * 用户登录
     *
     * @param phone 用户手机号
     * @param checkCode  验证码
     * @return ResponseResult<UserTo>
     */
    @ApiOperation("用户登录")
    @GetMapping("login")
    public ResponseResult<UserTo> login(@RequestParam("phone") String phone,
                                        @RequestParam("checkCode") String checkCode){
        try {
            return ResponseResult.ok(userService.login(phone, checkCode));
        } catch (BaseException e) {
            return ResponseResult.fail(e.getStatus(), e.getMessage());
        }
    }

    /**
     * 发送验证码
     *
     * @param phone 用户手机号
     * @return ResponseResult<String>
     */
    @ApiOperation("发送验证码")
    @GetMapping("sentCheckCode")
    public ResponseResult<String> sentCheckCode(@RequestParam("phone") String phone) {

        try {
            return ResponseResult.ok(userService.sentCheckCode(phone));
        } catch (BaseException e) {
            return ResponseResult.fail(e.getStatus(), e.getMessage());
        }
    }

    /**
     * 根据token查询用户数据
     *
     * @param  token 用户token
     * @return ResponseResult<User>
     */
    @ApiOperation("根据token查询用户")
    @GetMapping("token/{token}")
    public ResponseResult<User> getUserByToken(@PathVariable("token") String token) {

        try {
            return ResponseResult.ok(userService.getUserByToken(token));
        } catch (BaseException e) {
            return ResponseResult.fail(e.getStatus(), e.getMessage());
        }
    }

    /**
     * 完善个人信息
     *
     * @param token 用户token
     * @param userInfoDto 用户信息
     * @return ResponseResult<Object>
     */
    @ApiOperation("保存用户信息")
    @PostMapping("saveInfo")
    public ResponseResult<Void> saveUserInfo(@RequestHeader(AUTHORIZATION) String token,
                                             @RequestBody UserInfoDto userInfoDto) {
        try {
            userService.saveUserInfo(token, userInfoDto);
        } catch (BaseException e) {
            return ResponseResult.fail(e.getStatus(), e.getMessage());
        }

        return ResponseResult.ok();
    }

    /**
     * 上传头像
     *
     * @param token 用户token
     * @param file 用户头像图片文件
     * @return ResponseResult<Object>
     */
    @ApiOperation("保存用户头像")
    @PostMapping("saveAvatar")
    public ResponseResult<Void> saveAvatar(@RequestHeader(AUTHORIZATION) String token,
                                           @RequestParam("avatar") MultipartFile file) {
        try {
            userService.saveAvatar(token, file);
        } catch (BaseException e) {
            return ResponseResult.fail(e.getStatus(), e.getMessage());
        }

        return ResponseResult.ok();
    }

    /**
     * 获取用户详细信息
     *
     * @param token 用户token
     * @return ResponseResult<UserInfoDto>
     */
    @ApiOperation("获取用户详细信息")
    @GetMapping("info")
    public ResponseResult<UserInfoTo> getUserInfo(@RequestHeader(AUTHORIZATION) String token) {

        try {
            return ResponseResult.ok(userService.getUserInfo(userService.getUserByToken(token).getId()));
        } catch (BaseException e) {
            return ResponseResult.fail(e.getStatus(), e.getMessage());
        }
    }
}
