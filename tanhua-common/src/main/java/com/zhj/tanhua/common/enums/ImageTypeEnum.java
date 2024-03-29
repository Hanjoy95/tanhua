package com.zhj.tanhua.common.enums;

/**
 * 图片文件类型枚举
 *
 * @author huanjie.zhuang
 * @date 2021/6/19
 */
public enum ImageTypeEnum {

    JPG(".jpg"),
    JPEG(".jpeg"),
    PNG(".png"),
    GIF(".gif"),
    UNKNOWN("unknown");

    private String type;

    ImageTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static ImageTypeEnum getType(String type) {
        for (ImageTypeEnum imageTypeEnum : ImageTypeEnum.values()) {
            if (imageTypeEnum.getType().equals(type)) {
                return imageTypeEnum;
            }
        }
        return UNKNOWN;
    }
}
