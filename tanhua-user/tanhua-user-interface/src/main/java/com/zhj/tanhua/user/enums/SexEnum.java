package com.zhj.tanhua.user.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.zhj.tanhua.common.exception.EnumConvertException;

/**
 * 性别枚举
 *
 * @author huanjie.zhuang
 * @date 2021/6/2
 */
public enum SexEnum {
    MAN(0, "男"),
    WOMAN(1, "女"),
    UNKNOWN(2, "未知");

    private final int val;
    private final String desc;

    SexEnum(int val, String msg) {
        this.val = val;
        this.desc = msg;
    }

    public Integer getVal() {
        return this.val;
    }

    @JsonValue
    public String getDesc() {
        return this.desc;
    }

    public static SexEnum getType(int num) {
        for (SexEnum sexEnum : SexEnum.values()) {
            if (sexEnum.getVal() == num) {
                return sexEnum;
            }
        }
        throw new EnumConvertException("性别枚举转换错误");
    }
}
