package com.zhj.tanhua.user.pojo.to;

import com.zhj.tanhua.user.enums.SexEnum;
import com.zhj.tanhua.user.pojo.po.UserInfo;
import lombok.Data;

/**
 * @author huanjie.zhuang
 * @date 2021/6/18
 */
@Data
public class UserInfoTo extends UserInfo {

    private String phone;
    private SexEnum gender;
}
