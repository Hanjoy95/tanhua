package com.zhj.tanhua.common.enums;

/**
 * 文件类型枚举
 *
 * @author huanjie.zhuang
 * @date 2021/6/19
 */
public enum FileTypeEnum {
    IMAGE("image"),
    VIDEO("video"),
    UNKNOWN("unknown");

    private final String type;

    FileTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static FileTypeEnum getType(String type) {
        for (FileTypeEnum fileTypeEnum : FileTypeEnum.values()) {
            if (fileTypeEnum.getType() == type) {
                return fileTypeEnum;
            }
        }
        return UNKNOWN;
    }
}