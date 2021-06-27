package com.zhj.tanhua.server.controller;

import com.zhj.tanhua.circle.pojo.po.Comment;
import com.zhj.tanhua.circle.pojo.to.AlbumTo;
import com.zhj.tanhua.common.result.PageResult;
import com.zhj.tanhua.common.result.ResponseResult;
import com.zhj.tanhua.server.pojo.vo.circle.CommentVo;
import com.zhj.tanhua.server.pojo.bo.circle.FeedBo;
import com.zhj.tanhua.server.pojo.bo.circle.MomentBo;
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

        MomentBo result = circleService.addMoment(momentVo);
        return null == result.getMomentId() ? ResponseResult.ok(result) : ResponseResult.fail(result);
    }

    /**
     * 查询相册
     *
     * @param pageNum 当前页
     * @param pageSize 页大小
     * @return 返回相册的分页结果
     */
    @Auth
    @ApiOperation(value = "查询相册")
    @PostMapping("/album/query")
    public ResponseResult<PageResult<AlbumTo>> queryAlbums(
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
    @GetMapping("/friend/query")
    public ResponseResult<PageResult<FeedBo>> queryFeeds(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return ResponseResult.ok(circleService.queryFeeds(pageNum, pageSize));
    }

    /**
     * 点赞或取消点赞
     *
     * @param momentId 动态ID
     * @param isLike true为点赞，false为取消点赞
     * @return 返回成功响应
     */
    @Auth
    @ApiOperation(value = "点赞或取消点赞")
    @GetMapping("/moment/like")
    public ResponseResult<Void> likeOrUnlike(@RequestParam("momentId") String momentId,
                                             @RequestParam("isLike") Boolean isLike) {
        circleService.likeOrUnlike(momentId, isLike);
        return ResponseResult.ok();
    }

    /**
     * 评论某个动态或评论
     *
     * @param commentVo 评论请求体
     * @return 返回评论ID
     */
    @Auth
    @ApiOperation(value = "评论某个动态或评论")
    @PostMapping("/comment/add")
    public ResponseResult<String> addComment(@RequestBody CommentVo commentVo) {
        return ResponseResult.ok(circleService.addComment(commentVo.getMomentId(),
                commentVo.getCommentId(), commentVo.getContent()));
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
    @ApiOperation(value = "查询评论")
    @PostMapping("/comment/query")
    public ResponseResult<PageResult<Comment>> queryComment(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestBody CommentVo commentVo) {
        return ResponseResult.ok(circleService.queryComment(commentVo.getMomentId(), commentVo.getCommentId(),
                                                            pageNum, pageSize));
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
        return ResponseResult.ok(circleService.addLove(userId));
    }

    /**
     * 取消喜欢某个用户
     *
     * @param loveUserId 用户ID
     * @param belovedUserId 评论ID
     * @return 返回成功响应
     */
    @Auth
    @ApiOperation("取消喜欢某个用户")
    @GetMapping("/user/unlove")
    public ResponseResult<Void> deleteLove(@RequestParam("loveUserId") Long loveUserId,
                                           @RequestParam("belovedUserId") Long belovedUserId) {
        circleService.deleteLove(loveUserId, belovedUserId);
        return ResponseResult.ok();
    }

    /**
     * 查询我喜欢或喜欢我的用户
     *
     * @param pageNum 当前页
     * @param pageSize 页大小
     * @param isLove true为查询我喜欢的用户，false为查询喜欢我的用户
     * @return 返回用户信息分页结果
     */
    @Auth
    @ApiOperation("查询我喜欢或喜欢我的用户")
    @GetMapping("/user/love/query")
    public ResponseResult<PageResult<UserInfoTo>> queryLove(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam("isLove") Boolean isLove) {
        return ResponseResult.ok(circleService.queryLove(pageNum, pageSize, isLove));
    }
}
