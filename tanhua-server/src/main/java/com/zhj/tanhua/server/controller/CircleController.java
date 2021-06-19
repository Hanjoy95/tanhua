package com.zhj.tanhua.server.controller;

import com.zhj.tanhua.common.result.ResponseResult;
import com.zhj.tanhua.server.pojo.vo.circle.PublishFailedVo;
import com.zhj.tanhua.server.pojo.vo.circle.PublishVo;
import com.zhj.tanhua.server.service.CircleService;
import com.zhj.tanhua.server.web.annotation.Auth;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 好友圈模块的控制层
 *
 * @author huanjie.zhuang
 * @date 2021/6/18
 */
@Api(tags = "好友圈")
@RequestMapping("tanhua/circle")
@RestController
public class CircleController {

    @Autowired
    private CircleService circleService;

    /**
     * 保存用户发布动态
     *
     * @param publishVo 发布动态参数
     * @return ResponseResult<PublishFailedVo>
     */
    @ApiOperation(value = "保存用户发布动态")
    @PostMapping("/publish/save")
    @Auth
    public ResponseResult<PublishFailedVo> savePublish(@RequestBody PublishVo publishVo) {

        PublishFailedVo result = circleService.savePublish(publishVo);

        return null == result ? ResponseResult.ok() : ResponseResult.fail(result);
    }
}
