package com.zhj.tanhua.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zhj.tanhua.common.exception.EnumConvertException;

/**
 * @author huanjie.zhuang
 * @date 2021/6/18
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ResponseStatus {
    SUCCESS(200, "success"),

    INTERNAL_SERVER_ERROR(4000, "internal server error"),
    RESOURCE_HAS_EXIST(4001, "resource has exist"),
    RESOURCE_NOT_FOUND(4002, "resource not found"),
    PARAMETER_INVALID(4003, "parameter invalid"),
    TOKEN_EXPIRED(4004, "token expired"),
    CHECK_CODE_EXPIRED(4005, "checkCode expired"),
    REMOTE_SERVER_ERROR(4006, "remote server error"),
    SENT_MESSAGE_ERROR(4007, "sent message error"),
    ENUM_CONVERT_ERROR(4008, "enum convert error");

    private final Integer code;
    private final String message;

    ResponseStatus(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    public static ResponseStatus of(Integer code) {
        for (ResponseStatus responseStatus : ResponseStatus.values()) {
            if (responseStatus.getCode().equals(code)) {
                return responseStatus;
            }
        }
        throw new EnumConvertException("响应状态枚举转换错误");
    }
}
