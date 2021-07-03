package com.zhj.tanhua.circle.enums;

/**
 * 查询类型枚举
 *
 * @author huanjie.zhuang
 * @date 2021/7/3
 */
public enum QueryTypeEnum {
    QUERY_MY_MESSAGE("查询我的消息"),
    QUERY_MY_ACTION("查询我的动作(点赞,评论,喜欢)");

    private final String desc;

    QueryTypeEnum(String desc) {
        this.desc = desc;
    }
}
