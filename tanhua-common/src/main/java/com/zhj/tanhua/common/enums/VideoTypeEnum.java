package com.zhj.tanhua.common.enums;

/**
 * 视频文件类型枚举
 *
 * @author huanjie.zhuang
 * @date 2021/6/19
 */
public enum VideoTypeEnum {

    AVI(".avi"),
    MP4(".mp4"),
    RMVB(".rmvb"),
    MPEG(".mpeg"),
    UNKNOWN("unknown");

    private String type;

    VideoTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static VideoTypeEnum getType(String type) {
        for (VideoTypeEnum videoTypeEnum : VideoTypeEnum.values()) {
            if (videoTypeEnum.getType().equals(type)) {
                return videoTypeEnum;
            }
        }
        return UNKNOWN;
    }
}
