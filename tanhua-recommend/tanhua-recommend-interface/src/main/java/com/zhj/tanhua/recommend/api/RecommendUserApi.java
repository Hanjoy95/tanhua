package com.zhj.tanhua.recommend.api;

import com.zhj.tanhua.common.result.PageResult;
import com.zhj.tanhua.recommend.pojo.po.RecommendUser;

/**
 * @author huanjie.zhuang
 * @date 2021/6/10
 */
public interface RecommendUserApi {

    /**
     * 获取最佳推荐用户
     *
     * @param userId 用户ID
     * @return 返回推荐用户
     */
    RecommendUser getBestRecommendUser(Long userId);

    /**
     * 获取推荐用户列表
     *
     * @param userId 用户ID
     * @param pageNum 当前页
     * @param pageSize 页大小
     * @return 返回推荐用户分页结果
     */
    PageResult<RecommendUser> getRecommendUsers(Long userId, Integer pageNum, Integer pageSize);
}
