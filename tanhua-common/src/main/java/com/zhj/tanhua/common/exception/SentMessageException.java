package com.zhj.tanhua.common.exception;

import com.zhj.tanhua.common.result.ResponseStatus;

/**
 * @author huanjie.zhuang
 * @date 2021/6/18
 */
public class SentMessageException extends BaseException {

    public SentMessageException(String message) {
        super(message);
        this.setStatus(ResponseStatus.SENT_MESSAGE_ERROR);
    }

    public SentMessageException(Throwable cause) {
        super(cause);
        this.setStatus(ResponseStatus.SENT_MESSAGE_ERROR);
    }

    public SentMessageException(String message, Throwable cause) {
        super(message, cause);
        this.setStatus(ResponseStatus.SENT_MESSAGE_ERROR);
    }
}
