package com.zhj.tanhua.user.pojo.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @author huanjie.zhuang
 * @date 2021/6/2
 */
@Data
public class UserInfo implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String nickName;
    private String avatar;
    private String tags;
    private Integer sex;
    private Integer age;
    private Integer edu;
    private String school;
    private String city;
    private String birthday;
    private String industry;
    private String income;
    private Integer status;
}
