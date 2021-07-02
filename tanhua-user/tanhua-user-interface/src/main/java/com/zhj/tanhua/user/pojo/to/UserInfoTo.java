package com.zhj.tanhua.user.pojo.to;

import com.zhj.tanhua.user.enums.EduEnum;
import com.zhj.tanhua.user.enums.SexEnum;
import com.zhj.tanhua.user.enums.StatusEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author huanjie.zhuang
 * @date 2021/6/18
 */
@Data
public class UserInfoTo implements Serializable {

    private Long userId;
    private String phone;
    private String nickName;
    private String avatar;
    private List<String> tags;
    private SexEnum sex;
    private Integer age;
    private EduEnum edu;
    private String school;
    private String city;
    private String birthday;
    private String industry;
    private String income;
    private StatusEnum status;
}
