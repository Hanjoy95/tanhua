package com.zhj.tanhua.common.exception;

import com.zhj.tanhua.common.vo.ResponseResult;

/**
 * @author huanjie.zhuang
 * @date 2021/6/14
 */
public class BaseRunTimeException extends RuntimeException {

    private String errorCode;

    public BaseRunTimeException(String message) {
        super(message);
        this.errorCode = ResponseResult.INTERNAL_ERROR;
    }

    public BaseRunTimeException(Throwable cause) {
        super(cause);
        this.errorCode = ResponseResult.INTERNAL_ERROR;
    }

    public BaseRunTimeException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = ResponseResult.INTERNAL_ERROR;
    }

    public BaseRunTimeException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BaseRunTimeException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
