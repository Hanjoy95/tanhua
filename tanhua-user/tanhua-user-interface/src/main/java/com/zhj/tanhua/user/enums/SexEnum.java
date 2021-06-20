package com.zhj.tanhua.user.enums;

/**
 * 性别枚举
 *
 * @author huanjie.zhuang
 * @date 2021/6/2
 */
public enum SexEnum {
    man(0),
    women(1),
    unknown(2);

    private final int value;

    SexEnum(int value) {
        this.value = value;
    }

    public Integer getValue() {
        return this.value;
    }

    public static SexEnum getType(int num) {
        for (SexEnum sexEnum : SexEnum.values()) {
            if (sexEnum.getValue() == num) {
                return sexEnum;
            }
        }
        return unknown;
    }
}
