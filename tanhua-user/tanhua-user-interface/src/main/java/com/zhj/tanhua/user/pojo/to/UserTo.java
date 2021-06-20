package com.zhj.tanhua.user.pojo.to;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author huanjie.zhuang
 * @date 2021/6/18
 */
@Data
@Builder
public class UserTo implements Serializable {

    private Boolean isNew;
    private Long userId;
    private String token;
}
