package com.zhj.tanhua.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @author huanjie.zhuang
 * @date 2021/6/18
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ResponseStatus {
    SUCCESS(200, "success"),

    SERVER_ERROR(4000, "server error"),
    RESOURCE_DUPLICATE(4001, "resource duplicate"),
    RESOURCE_NOT_FOUND(4002, "resource not found"),
    PARAMETER_INVALID(4003, "parameter invalid"),
    TOKEN_EXPIRED(4004, "token expired"),
    CHECK_CODE_EXPIRED(4005, "checkCode expired"),
    REMOTE_SERVER_ERROR(4006, "remote server error"),
    SENT_MESSAGE_ERROR(4007, "sent message error");

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
        return SERVER_ERROR;
    }
}
