package com.zhj.tanhua.server.controller;

import com.zhj.tanhua.circle.enums.QueryTypeEnum;
import com.zhj.tanhua.circle.pojo.to.CommentTo;
import com.zhj.tanhua.circle.pojo.to.MomentTo;
import com.zhj.tanhua.common.exception.BaseException;
import com.zhj.tanhua.common.result.PageResult;
import com.zhj.tanhua.common.result.ResponseResult;
import com.zhj.tanhua.server.pojo.bo.circle.*;
import com.zhj.tanhua.server.pojo.vo.circle.CommentVo;
import com.zhj.tanhua.server.pojo.vo.circle.MomentVo;
import com.zhj.tanhua.server.service.CircleService;
import com.zhj.tanhua.server.web.annotation.Auth;
import com.zhj.tanhua.user.pojo.to.UserInfoTo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
     * 发布动态
     *
     * @param momentVo 动态参数
     * @return 返回发布动态结果
     */
    @Auth
    @ApiOperation(value = "发布动态")
    @PostMapping("/moment/publish")
    public ResponseResult<MomentBo> publishMoment(@RequestBody MomentVo momentVo) {
        try {
            MomentBo result = circleService.addMoment(momentVo);
            return null == result.getMomentId() ? ResponseResult.fail(result) : ResponseResult.ok(result);
        } catch (BaseException e) {
            return ResponseResult.fail(e);
        }
    }

    /**
     * 查询某条动态
     *
     * @param momentId 动态ID
     * @return 返回动态
     */
    @Auth
    @ApiOperation(value = "查询某条动态")
    @GetMapping("/moment/query")
    public ResponseResult<MomentTo> queryMoment(String momentId) {
        try {
            return ResponseResult.ok(circleService.queryMoment(momentId));
        } catch (BaseException e) {
            return ResponseResult.fail(e);
        }
    }

    /**
     * 查询相册
     *
     * @param pageNum 当前页
     * @param pageSize 页大小
     * @return 返回相册的分页结果
     */
    @Auth
    @ApiOperation(value = "查询我的相册")
    @PostMapping("/album/query")
    public ResponseResult<PageResult<MomentTo>> queryAlbums(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return ResponseResult.ok(circleService.queryAlbums(pageNum, pageSize));
    }

    /**
     * 查询好友动态
     *
     * @param pageNum 当前页
     * @param pageSize 页大小
     * @return 返回好友动态分页结果
     */
    @Auth
    @ApiOperation(value = "查询好友动态")
    @GetMapping("/feed/query")
    public ResponseResult<PageResult<FeedBo>> queryFeeds(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return ResponseResult.ok(circleService.queryFeeds(pageNum, pageSize));
    }

    /**
     * 点赞
     *
     * @param momentId 动态ID
     * @return 返回成功响应
     */
    @Auth
    @ApiOperation(value = "点赞")
    @GetMapping("/moment/like")
    public ResponseResult<Void> like(@RequestParam("momentId") String momentId) {
        try {
            circleService.likeOrUnlike(momentId, true);
        } catch (BaseException e) {
            return ResponseResult.fail(e);
        }
        return ResponseResult.ok();
    }

    /**
     * 取消点赞
     *
     * @param momentId 动态ID
     * @return 返回成功响应
     */
    @Auth
    @ApiOperation(value = "取消点赞")
    @GetMapping("/moment/unlike")
    public ResponseResult<Void> unlike(@RequestParam("momentId") String momentId) {
        try {
            circleService.likeOrUnlike(momentId, false);
        } catch (BaseException e) {
            return ResponseResult.fail(e);
        }
        return ResponseResult.ok();
    }

    /**
     * 查询谁点赞我的动态
     *
     * @param pageNum 当前页
     * @param pageSize 页大小
     * @return 返回点赞分页结果
     */
    @Auth
    @ApiOperation(value = "查询点赞消息")
    @GetMapping("/like/message")
    public ResponseResult<PageResult<LikeBo>> queryBeLikes(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return ResponseResult.ok(circleService.queryLikeWithType(
                QueryTypeEnum.QUERY_MY_MESSAGE, pageNum, pageSize));
    }

    /**
     * 查询我点赞谁的动态
     *
     * @param pageNum 当前页
     * @param pageSize 页大小
     * @return 返回点赞分页结果
     */
    @Auth
    @ApiOperation(value = "查询我的点赞")
    @GetMapping("/like/my")
    public ResponseResult<PageResult<LikeBo>> queryLikes(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return ResponseResult.ok(circleService.queryLikeWithType(
                QueryTypeEnum.QUERY_MY_ACTION, pageNum, pageSize));
    }

    /**
     * 评论某个动态或评论
     *
     * @param commentVo 评论请求体
     * @return 返回评论ID
     */
    @Auth
    @ApiOperation(value = "发表评论")
    @PostMapping("/comment/add")
    public ResponseResult<String> addComment(@RequestBody CommentVo commentVo) {
        try {
            return ResponseResult.ok(circleService.addComment(commentVo.getMomentId(),
                    commentVo.getCommentId(), commentVo.getContent()));
        } catch (BaseException e) {
            return ResponseResult.fail(e);
        }
    }

    /**
     * 删除评论
     *
     * @param commentId 评论ID
     * @return 返回被删除的评论ID列表
     */
    @Auth
    @ApiOperation(value = "删除评论")
    @GetMapping("/comment/delete")
    public ResponseResult<List<String>> deleteComment(@RequestParam("commentId") String commentId) {
        return ResponseResult.ok(circleService.deleteComment(commentId));
    }

    /**
     * 查询评论
     *
     * @param pageNum 当前页
     * @param pageSize 页大小
     * @param commentVo 评论请求体
     * @return 返回评论分页结果
     */
    @Auth
    @ApiOperation(value = "查询评论")
    @PostMapping("/comment/query")
    public ResponseResult<PageResult<CommentTo>> queryComment(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestBody CommentVo commentVo) {
        return ResponseResult.ok(circleService.queryComment(commentVo.getMomentId(),
                commentVo.getCommentId(), pageNum, pageSize));
    }

    /**
     * 查询谁评论我
     *
     * @param pageNum 当前页
     * @param pageSize 页大小
     * @return 返回评论分页结果
     */
    @Auth
    @ApiOperation(value = "查询评论消息")
    @PostMapping("/comment/message")
    public ResponseResult<PageResult<CommentBo>> queryWhoCommentMe(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return ResponseResult.ok(circleService.queryCommentsWithType(
                QueryTypeEnum.QUERY_MY_MESSAGE, pageNum, pageSize));
    }

    /**
     * 查询我评论谁
     *
     * @param pageNum 当前页
     * @param pageSize 页大小
     * @return 返回评论分页结果
     */
    @Auth
    @ApiOperation(value = "查询我的评论")
    @PostMapping("/comment/my")
    public ResponseResult<PageResult<CommentBo>> queryMeCommentWho(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return ResponseResult.ok(circleService.queryCommentsWithType(
                QueryTypeEnum.QUERY_MY_ACTION, pageNum, pageSize));
    }

    /**
     * 喜欢某个用户
     *
     * @param userId 喜欢的用户ID
     * @return 匹配成功返回对方信息
     */
    @Auth
    @ApiOperation(value = "喜欢某个用户")
    @GetMapping("/user/love")
    public ResponseResult<UserInfoTo> addLove(Long userId) {
        try {
            return ResponseResult.ok(circleService.addLove(userId));
        } catch (BaseException e) {
            return ResponseResult.fail(e);
        }
    }

    /**
     * 取消喜欢某个用户
     *
     * @param lover 用户ID
     * @param beLoved 评论ID
     * @return 返回成功响应
     */
    @Auth
    @ApiOperation("取消喜欢某个用户")
    @GetMapping("/user/unlove")
    public ResponseResult<Void> deleteLove(@RequestParam("lover") Long lover,
                                           @RequestParam("beLoved") Long beLoved) {
        try {
            circleService.deleteLove(lover, beLoved);
        } catch (BaseException e) {
            return ResponseResult.fail(e);
        }
        return ResponseResult.ok();
    }

    /**
     * 查询谁喜欢我
     *
     * @param pageNum 当前页
     * @param pageSize 页大小
     * @return 返回喜欢信息分页结果
     */
    @Auth
    @ApiOperation("查询喜欢消息")
    @GetMapping("/love/message")
    public ResponseResult<PageResult<LoveBo>> queryWhoLoveMe(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return ResponseResult.ok(circleService.queryLove(QueryTypeEnum.QUERY_MY_MESSAGE, pageNum, pageSize));
    }

    /**
     * 查询我喜欢谁
     *
     * @param pageNum 当前页
     * @param pageSize 页大小
     * @return 返回喜欢信息分页结果
     */
    @Auth
    @ApiOperation("查询我的喜欢")
    @GetMapping("/love/my")
    public ResponseResult<PageResult<LoveBo>> queryMeLoveWho(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return ResponseResult.ok(circleService.queryLove(QueryTypeEnum.QUERY_MY_ACTION, pageNum, pageSize));
    }
}
