package com.zhj.tanhua.common.exception;

import com.zhj.tanhua.common.enums.ResponseStatus;

/**
 * @author huanjie.zhuang
 * @date 2021/6/18
 */
public class ResourceHasExistException extends BaseException {

    public ResourceHasExistException(String message) {
        super(message);
        this.setStatus(ResponseStatus.RESOURCE_HAS_EXIST);
    }
}
