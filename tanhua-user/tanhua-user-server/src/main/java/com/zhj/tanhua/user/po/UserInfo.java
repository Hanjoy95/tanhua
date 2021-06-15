package com.zhj.tanhua.user.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author huanjie.zhuang
 * @date 2021/6/2
 */
@Data
public class UserInfo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String nickName;
    private String avatar;
    private String tags;
    private Integer sex;
    private Integer age;
    private String edu;
    private String city;
    private String birthday;
    private String coverPic;
    private String industry;
    private String income;
    private String marriage;
}
