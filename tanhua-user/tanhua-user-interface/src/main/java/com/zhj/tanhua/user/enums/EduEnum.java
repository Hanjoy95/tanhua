package com.zhj.tanhua.user.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.zhj.tanhua.common.exception.EnumConvertException;

/**
 * 学历枚举
 *
 * @author huanjie.zhuang
 * @date 2021/6/19
 */
public enum EduEnum {
    ILLITERACY(0, "文盲"),
    PRIMARY_SCHOOL(1, "小学"),
    MIDDLE_SCHOOL(2, "中学"),
    TECHNICAL_SECONDARY_SCHOOL(3, "中专"),
    HIGH_SCHOOL(4, "高中"),
    JUNIOR_COLLEGE(5, "高中"),
    UNDERGRADUATE(6, "本科"),
    MASTER(7, "硕士"),
    DOCTOR(8, "博士"),
    UNKNOWN(9, "未知");

    private final int val;
    private final String desc;

    EduEnum(int val, String desc) {
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

    public static EduEnum getType(int num) {
        for (EduEnum eduEnum : EduEnum.values()) {
            if (eduEnum.getVal() == num) {
                return eduEnum;
            }
        }
        throw new EnumConvertException("学历枚举转换错误");
    }
}
