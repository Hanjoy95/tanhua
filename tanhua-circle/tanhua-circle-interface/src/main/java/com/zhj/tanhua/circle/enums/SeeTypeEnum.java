package com.zhj.tanhua.circle.enums;

/**
 * 可看类型枚举
 *
 * @author huanjie.zhuang
 * @date 2021/6/26
 */
public enum SeeTypeEnum {
    PUBLIC(0),
    PRIVATE(1),
    WHO_CAN_SEE(2),
    WHO_CANNOT_SEE(3),
    UNKNOWN(4);

    private final int value;

    SeeTypeEnum(int value) {
        this.value = value;
    }

    public Integer getValue() {
        return this.value;
    }

    public static SeeTypeEnum getType(int num) {
        for (SeeTypeEnum seeTypeEnum : SeeTypeEnum.values()) {
            if (seeTypeEnum.getValue() == num) {
                return seeTypeEnum;
            }
        }
        return UNKNOWN;
    }
}
