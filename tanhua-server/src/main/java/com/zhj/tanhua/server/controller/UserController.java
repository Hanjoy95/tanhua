package com.zhj.tanhua.server.controller;

import com.zhj.tanhua.common.exception.BaseException;
import com.zhj.tanhua.common.result.ResponseResult;
import com.zhj.tanhua.server.service.UserService;
import com.zhj.tanhua.server.web.annotation.Auth;
import com.zhj.tanhua.server.web.threadlocal.UserThreadLocal;
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
 * 用户模块的控制层
 *
 * @author huanjie.zhuang
 * @date 2021/6/13
 */
@Api(tags = "用户")
@RequestMapping("tanhua/user")
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 验证码登录
     *
     * @param phone 用户手机号
     * @param checkCode 验证码
     * @return 返回用户信息
     */
    @ApiOperation("验证码登录")
    @GetMapping("/login/checkCode")
    public ResponseResult<UserTo> loginWithCheckCode(@RequestParam("phone") String phone,
                                                     @RequestParam("checkCode") String checkCode){
        try {
            return ResponseResult.ok(userService.loginWithCheckCode(phone, checkCode));
        } catch (BaseException e) {
            return ResponseResult.fail(e);
        }
    }

    /**
     * 密码登录
     *
     * @param phone 用户手机号
     * @param password 密码
     * @return 返回用户信息
     */
    @ApiOperation("密码登录")
    @GetMapping("/login/password")
    public ResponseResult<UserTo> loginWithPassword(@RequestParam("phone") String phone,
                                                    @RequestParam("password") String password){
        try {
            return ResponseResult.ok(userService.loginWithPassword(phone, password));
        } catch (BaseException e) {
            return ResponseResult.fail(e);
        }
    }

    /**
     * 发送验证码
     *
     * @param phone 用户手机号
     * @return 返回验证码
     */
    @ApiOperation("发送验证码")
    @GetMapping("/sentCheckCode")
    public ResponseResult<String> sentCheckCode(@RequestParam("phone") String phone) {

        try {
            return ResponseResult.ok(userService.sentCheckCode(phone));
        } catch (BaseException e) {
            return ResponseResult.fail(e);
        }
    }

    /**
     * 根据token查询用户数据
     *
     * @param  token 用户token
     * @return 返回用户信息
     */
    @ApiOperation("根据token查询用户")
    @GetMapping("/token/{token}")
    public ResponseResult<User> getUserByToken(@PathVariable("token") String token) {

        try {
            return ResponseResult.ok(userService.getUserByToken(token));
        } catch (BaseException e) {
            return ResponseResult.fail(e);
        }
    }

    /**
     * 完善个人信息
     *
     * @param userInfoDto 用户信息
     * @return 返回响应
     */
    @ApiOperation("保存信息")
    @PostMapping("/saveInfo")
    @Auth
    public ResponseResult<Void> saveUserInfo(@RequestBody UserInfoDto userInfoDto) {
        try {
            userService.saveUserInfo(userInfoDto);
        } catch (BaseException e) {
            return ResponseResult.fail(e);
        }

        return ResponseResult.ok();
    }

    /**
     * 修改密码
     *
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    @ApiOperation("修改密码")
    @GetMapping("/password/modify")
    @Auth
    public ResponseResult<Void> modifyPassword(String oldPassword, String newPassword) {
        try {
            userService.modifyPassword(oldPassword, newPassword);
        } catch (BaseException e) {
            return ResponseResult.fail(e);
        }

        return ResponseResult.ok();
    }

    /**
     * 上传头像
     *
     * @param file 用户头像图片文件
     * @return 返回响应
     */
    @ApiOperation("保存头像")
    @PostMapping("/saveAvatar")
    @Auth
    public ResponseResult<Void> saveAvatar(@RequestParam("avatar") MultipartFile file) {
        try {
            userService.saveAvatar(file);
        } catch (BaseException e) {
            return ResponseResult.fail(e);
        }

        return ResponseResult.ok();
    }

    /**
     * 获取用户详细信息
     *
     * @return 返回用户详细信息
     */
    @ApiOperation("获取详细信息")
    @GetMapping("/info")
    @Auth
    public ResponseResult<UserInfoTo> getUserInfo() {

        try {
            return ResponseResult.ok(userService.getUserInfo(UserThreadLocal.get().getId()));
        } catch (BaseException e) {
            return ResponseResult.fail(e);
        }
    }
}
