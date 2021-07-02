package com.zhj.tanhua.common.exception;

import com.zhj.tanhua.common.enums.ResponseStatus;

/**
 * @author huanjie.zhuang
 * @date 2021/6/18
 */
public class TokenExpiredException extends BaseException {

    public TokenExpiredException(String message) {
        super(message);
        this.setStatus(ResponseStatus.TOKEN_EXPIRED);
    }
}
