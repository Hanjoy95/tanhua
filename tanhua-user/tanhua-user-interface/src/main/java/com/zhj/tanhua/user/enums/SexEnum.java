package com.zhj.tanhua.user.enums;

/**
 * @author huanjie.zhuang
 * @date 2021/6/2
 */
public enum SexEnum {
    man(0),
    women(1),
    unknow(2);

    private final int value;

    SexEnum(int value) {
        this.value = value;
    }

    public Integer getValue() {
        return this.value;
    }
}
