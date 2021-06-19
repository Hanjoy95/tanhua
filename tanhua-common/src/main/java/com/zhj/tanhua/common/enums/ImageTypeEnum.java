package com.zhj.tanhua.common.enums;

/**
 * 图片文件类型
 *
 * @author huanjie.zhuang
 * @date 2021/6/19
 */
public enum ImageTypeEnum {

    JPG(".jpg"),
    JPEG(".jpeg"),
    PNG(".png"),
    GIF(".gif");

    private String type;

    ImageTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
