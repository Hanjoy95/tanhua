package com.zhj.tanhua.common.exception;

import com.zhj.tanhua.common.result.ResponseStatus;

/**
 * @author huanjie.zhuang
 * @date 2021/6/14
 */
public class BaseException extends RuntimeException {

    private ResponseStatus status;

    public BaseException() {
        this.status = ResponseStatus.SERVER_ERROR;
    }

    public BaseException(String message) {
        super(message);
        this.status = ResponseStatus.SERVER_ERROR;
    }

    public BaseException(Throwable cause) {
        super(cause);
        this.status = ResponseStatus.SERVER_ERROR;
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
        this.status = ResponseStatus.SERVER_ERROR;
    }

    public BaseException(ResponseStatus status, String message) {
        super(message);
        this.status = status;
    }

    public BaseException(ResponseStatus status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    public ResponseStatus getStatus() {
        return this.status;
    }
}
