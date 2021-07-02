package com.zhj.tanhua.server.controller;

import com.zhj.tanhua.common.exception.BaseException;
import com.zhj.tanhua.common.result.PageResult;
import com.zhj.tanhua.common.result.ResponseResult;
import com.zhj.tanhua.server.service.RecommendService;
import com.zhj.tanhua.server.pojo.vo.recommend.RecommendUserVo;
import com.zhj.tanhua.server.pojo.bo.recommend.RecommendUserBo;
import com.zhj.tanhua.server.web.annotation.Auth;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 推荐模块的控制层
 *
 * @author huanjie.zhuang
 * @date 2021/6/13
 */
@Api(tags = "推荐")
@RequestMapping("tanhua/recommend")
@RestController
public class RecommendController {

    @Autowired
    private RecommendService recommendService;

    /**
     * 推荐今日佳人
     *
     * @return 返回今日佳人信息
     */
    @ApiOperation("推荐今日佳人")
    @GetMapping("todayBest")
    @Auth
    public ResponseResult<RecommendUserBo> getTodayBest() {

        try {
            return ResponseResult.ok(recommendService.getTodayBest());
        }catch (BaseException e) {
            return ResponseResult.fail(e);
        }
    }

    /**
     * 推荐用户
     *
     * @param recommendUserVo  推荐用户参数
     * @return 返回推荐用户分页结果
     */
    @ApiOperation("推荐用户")
    @PostMapping("users")
    @Auth
    public ResponseResult<PageResult<RecommendUserBo>> getRecommendUsers(@RequestBody RecommendUserVo recommendUserVo) {
        try {
            return ResponseResult.ok(recommendService.getRecommendUsers(recommendUserVo));
        } catch (BaseException e) {
            return ResponseResult.fail(e);
        }
    }
}
