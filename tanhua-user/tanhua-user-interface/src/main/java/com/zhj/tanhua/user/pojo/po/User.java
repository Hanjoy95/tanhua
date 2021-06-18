package com.zhj.tanhua.user.pojo.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;

/**
 * @author huanjie.zhuang
 * @date 2021/6/2
 */
@Data
public class User implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String phone;
    @JsonIgnore
    private String password;
}
