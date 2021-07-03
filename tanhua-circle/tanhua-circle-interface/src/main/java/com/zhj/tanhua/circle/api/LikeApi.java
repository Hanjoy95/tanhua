package com.zhj.tanhua.circle.api;

import com.zhj.tanhua.circle.enums.QueryTypeEnum;
import com.zhj.tanhua.circle.pojo.po.Like;
import com.zhj.tanhua.common.result.PageResult;

/**
 * 点赞dubbo接口
 *
 * @author huanjie.zhuang
 * @date 2021/7/3
 */
public interface LikeApi {

    /**
     * 点赞或取消点赞
     *
     * @param userId 用户ID
     * @param momentId 动态ID
     * @param isLike true为点赞，false为取消点赞
     */
    void likeOrUnlike(Long userId, String momentId, Boolean isLike);

    /**
     * 查询我的点赞消息
     *
     * @param type 查询类型
     * @param userId 用户ID
     * @param pageNum 当前页
     * @param pageSize 页大小
     * @return 返回点赞分页结果
     */
    PageResult<Like> queryLikeWithType(QueryTypeEnum type, Long userId,
                                       Integer pageNum, Integer pageSize);
}
