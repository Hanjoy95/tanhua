package com.zhj.tanhua.circle.api;

import com.zhj.tanhua.circle.enums.QueryTypeEnum;
import com.zhj.tanhua.circle.pojo.po.Love;
import com.zhj.tanhua.common.result.PageResult;

/**
 * 喜欢dubbo接口
 *
 * @author huanjie.zhuang
 * @date 2021/7/3
 */
public interface LoveApi {

    /**
     * 喜欢某个用户
     *
     * @param lover 用户ID
     * @param beLoved 被喜欢的用户ID
     * @return 返回是否匹配成功
     */
    boolean addLove(Long lover, Long beLoved);

    /**
     * 取消喜欢某个用户
     *
     * @param lover 用户ID
     * @param beLoved 被喜欢的用户ID
     */
    void deleteLove(Long lover, Long beLoved);

    /**
     * 查询我的喜欢消息
     *
     * @param type 查询类型
     * @param userId 用户ID
     * @param pageNum 当前页
     * @param pageSize 页大小
     * @return 返回喜欢信息分页结果
     */
    PageResult<Love> queryLoveWithType(QueryTypeEnum type, Long userId, Integer pageNum, Integer pageSize);
}
