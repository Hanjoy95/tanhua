package com.zhj.tanhua.common.exception;

import com.zhj.tanhua.common.vo.ResponseStatus;

/**
 * @author huanjie.zhuang
 * @date 2021/6/14
 */
public class NotFoundException extends BaseException {

    public NotFoundException(String message) {
        super(message);
        this.setStatus(ResponseStatus.RESOURCE_NOT_FOUND);
    }
}
