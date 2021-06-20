package com.zhj.tanhua.user.enums;

/**
 * 状态枚举
 *
 * @author huanjie.zhuang
 * @date 2021/6/19
 */
public enum StatusEnum {
    single(0),
    in_love(1),
    married(2),
    unknown(3);

    private final int value;

    StatusEnum(int value) {
        this.value = value;
    }

    public Integer getValue() {
        return this.value;
    }

    public static StatusEnum getType(int num) {
        for (StatusEnum statusEnum : StatusEnum.values()) {
            if (statusEnum.getValue() == num) {
                return statusEnum;
            }
        }
        return unknown;
    }
}
