package com.zhj.tanhua.common.enums;

/**
 * 文件类型
 *
 * @author huanjie.zhuang
 * @date 2021/6/19
 */
public enum FileTypeEnum {

    IMAGE("image"),
    VIDEO("video");

    private String type;

    FileTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
