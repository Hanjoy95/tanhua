package com.zhj.tanhua.circle.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.zhj.tanhua.common.exception.EnumConvertException;

/**
 * 动态可看类型枚举
 *
 * @author huanjie.zhuang
 * @date 2021/6/26
 */
public enum SeeTypeEnum {
    PUBLIC(0, "公开"),
    PRIVATE(1, "私人"),
    WHO_CAN_SEE(2, "谁可看"),
    WHO_CANNOT_SEE(3, "谁不可看");

    private final int val;
    private final String desc;

    SeeTypeEnum(int val, String desc) {
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

    public static SeeTypeEnum getType(int num) {
        for (SeeTypeEnum seeTypeEnum : SeeTypeEnum.values()) {
            if (seeTypeEnum.getVal() == num) {
                return seeTypeEnum;
            }
        }
        throw new EnumConvertException("动态可看类型枚举转换错误");
    }
}
