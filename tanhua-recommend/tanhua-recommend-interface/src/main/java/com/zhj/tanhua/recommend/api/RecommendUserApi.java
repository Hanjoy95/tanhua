package com.zhj.tanhua.recommend.api;

import com.zhj.tanhua.common.vo.PageResult;
import com.zhj.tanhua.recommend.dto.RecommendUserDto;

/**
 * @author huanjie.zhuang
 * @date 2021/6/10
 */
public interface RecommendUserApi {

    /**
     * 获取最佳推荐用户
     *
     * @param userId 用户ID
     * @return RecommendUserDto
     */
    RecommendUserDto getBestRecommendUser(Long userId);

    /**
     * 获取推荐用户列表
     *
     * @param userId 用户ID
     * @param pageNum 当前页
     * @param pageSize 页大小
     * @return PageResult<RecommendUserDto>
     */
    PageResult<RecommendUserDto> getRecommendUsers(Long userId, Integer pageNum, Integer pageSize);
}
