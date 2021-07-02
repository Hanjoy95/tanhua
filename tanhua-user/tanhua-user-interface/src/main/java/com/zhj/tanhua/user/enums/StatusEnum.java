package com.zhj.tanhua.user.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.zhj.tanhua.common.exception.EnumConvertException;

/**
 * 状态枚举
 *
 * @author huanjie.zhuang
 * @date 2021/6/19
 */
public enum StatusEnum {
    SINGLE(0, "单身"),
    IN_LOVE(1, "恋爱中"),
    MARRIED(2, "已婚"),
    UNKNOWN(3, "未知");

    private final int val;
    private final String desc;

    StatusEnum(int val, String desc) {
        this.val = val;
        this.desc = desc;
    }

    public Integer getVal() {
        return this.val;
    }

    @JsonValue
    public String getDesc() {
        return this.desc;
    }

    public static StatusEnum getType(int num) {
        for (StatusEnum statusEnum : StatusEnum.values()) {
            if (statusEnum.getVal() == num) {
                return statusEnum;
            }
        }
        throw new EnumConvertException("状态枚举转换错误");
    }
}
