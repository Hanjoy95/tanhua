package com.zhj.tanhua.circle.api;

import com.zhj.tanhua.circle.pojo.dto.PublishDto;
import com.zhj.tanhua.circle.pojo.po.Publish;
import com.zhj.tanhua.common.result.PageResult;

/**
 * @author huanjie.zhuang
 * @date 2021/6/17
 */
public interface CircleApi {

    /**
     * 保存用户发布动态
     *
     * @param publishDto 发布内容
     */
    void savePublish(PublishDto publishDto);

    /**
     * 查询好友动态
     *
     * @param userId 用户ID
     * @param pageNum 当前页
     * @param pageSize 页大小
     * @return PageResult<PublishDto>
     */
    PageResult<Publish> queryPublishList(Long userId, Integer pageNum, Integer pageSize);
}
