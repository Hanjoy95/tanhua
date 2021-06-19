package com.zhj.tanhua.server.controller;

import com.zhj.tanhua.common.constant.ThConstant;
import com.zhj.tanhua.common.exception.BaseException;
import com.zhj.tanhua.common.result.PageResult;
import com.zhj.tanhua.common.result.ResponseResult;
import com.zhj.tanhua.server.service.RecommendService;
import com.zhj.tanhua.server.pojo.vo.recommend.RecommendUserVo;
import com.zhj.tanhua.server.pojo.vo.recommend.TodayBestVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author huanjie.zhuang
 * @date 2021/6/13
 */
@Api(tags = "推荐用户")
@RequestMapping("tanhua/recommend")
@RestController
public class RecommendController {

    @Autowired
    private RecommendService recommendService;

    /**
     * 获取今日佳人
     *
     * @param token 用户token
     * @return ResponseResult<TodayBestVo>
     */
    @ApiOperation("获取今日佳人")
    @GetMapping("todayBest")
    public ResponseResult<TodayBestVo> getTodayBest(@RequestHeader(ThConstant.AUTHORIZATION) String token) {

        try {
            return ResponseResult.ok(recommendService.getTodayBest(token));
        }catch (BaseException e) {
            return ResponseResult.fail(e.getStatus(), e.getMessage());
        }
    }

    /**
     * 获取推荐用户列表
     *
     * @param token 用户token
     * @param recommendUserVo  获取推荐用户列表参数
     * @return ResponseResult<PageResult<TodayBestVo>>
     */
    @ApiOperation("获取推荐用户列表")
    @PostMapping("users")
    public ResponseResult<PageResult<TodayBestVo>> getRecommendUsers(@RequestHeader(ThConstant.AUTHORIZATION) String token,
                                                                     @RequestBody RecommendUserVo recommendUserVo) {
        try {
            return ResponseResult.ok(recommendService.getRecommendUsers(token, recommendUserVo));
        } catch (BaseException e) {
            return ResponseResult.fail(e.getStatus(), e.getMessage());
        }
    }
}
