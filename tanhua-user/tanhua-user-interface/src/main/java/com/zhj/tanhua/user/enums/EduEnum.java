package com.zhj.tanhua.user.enums;

/**
 * 学历枚举
 *
 * @author huanjie.zhuang
 * @date 2021/6/19
 */
public enum EduEnum {
    illiteracy(0),
    primary_school(1),
    middle_school(2),
    technical_secondary_school(3),
    high_school(4),
    junior_college(5),
    undergraduate(6),
    master(7),
    doctor(8),
    unknown(9);

    private final int value;

    EduEnum(int value) {
        this.value = value;
    }

    public Integer getValue() {
        return this.value;
    }

    public static EduEnum getType(int num) {
        for (EduEnum eduEnum : EduEnum.values()) {
            if (eduEnum.getValue() == num) {
                return eduEnum;
            }
        }
        return unknown;
    }
}
