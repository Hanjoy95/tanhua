package com.zhj.tanhua.circle.api;

import com.zhj.tanhua.circle.dto.PublishDto;
import com.zhj.tanhua.common.exception.BaseRunTimeException;
import com.zhj.tanhua.common.vo.PageResult;

/**
 * @author huanjie.zhuang
 * @date 2021/6/17
 */
public interface circleApi {

    /**
     * 保存用户发布动态
     *
     * @param publishDto 发布内容
     * @throws BaseRunTimeException 运行时异常
     */
    void savePublish(PublishDto publishDto) throws BaseRunTimeException;

    /**
     * 查询好友动态
     *
     * @param userId 用户ID
     * @param pageNum 当前页
     * @param pageSize 页大小
     * @return PageResult<PublishDto>
     */
    PageResult<PublishDto> queryPublishList(Long userId, Integer pageNum, Integer pageSize);
}
