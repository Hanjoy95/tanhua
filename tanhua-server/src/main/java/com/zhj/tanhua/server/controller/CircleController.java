package com.zhj.tanhua.server.controller;

import com.zhj.tanhua.common.result.ResponseResult;
import com.zhj.tanhua.server.vo.circle.PublishVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author huanjie.zhuang
 * @date 2021/6/18
 */
@Api(tags = "好友圈")
@RequestMapping("tanhua/circle")
@RestController
public class CircleController {

    /**
     * @param publishVo 发布动态参数
     * @return ResponseResult<Void>
     */
    @ApiOperation(value = "保存用户发布动态")
    @PostMapping("/publish/save")
    public ResponseResult<Void> savePublish(@RequestBody PublishVo publishVo) {

        return null;
    }
}
