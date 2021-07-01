package com.zhj.tanhua.user.pojo.to;

import com.fasterxml.jackson.annotation.JsonInclude;
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

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Boolean isNew;
    private Long userId;
    private String token;
}
