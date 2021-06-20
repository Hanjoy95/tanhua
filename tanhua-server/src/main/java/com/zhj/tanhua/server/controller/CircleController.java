package com.zhj.tanhua.server.controller;

import com.zhj.tanhua.common.result.PageResult;
import com.zhj.tanhua.common.result.ResponseResult;
import com.zhj.tanhua.server.pojo.vo.circle.FeedVo;
import com.zhj.tanhua.server.pojo.vo.circle.PublishFailedVo;
import com.zhj.tanhua.server.pojo.vo.circle.PublishVo;
import com.zhj.tanhua.server.service.CircleService;
import com.zhj.tanhua.server.web.annotation.Auth;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @Auth
    @ApiOperation(value = "保存用户发布动态")
    @PostMapping("/publish/save")
    public ResponseResult<PublishFailedVo> savePublish(@RequestBody PublishVo publishVo) {

        PublishFailedVo result = circleService.savePublish(publishVo);

        return null == result ? ResponseResult.ok() : ResponseResult.fail(result);
    }

    /**
     * 查询好友或推荐动态
     *
     * @param pageNum 当前页
     * @param pageSize 页大小
     * @return ResponseResult<PageResult<Void>>
     */
    @Auth
    @GetMapping("/query/friends")
    public ResponseResult<PageResult<FeedVo>> queryFeeds(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return ResponseResult.ok(circleService.queryFeeds(pageNum, pageSize));
    }
}
