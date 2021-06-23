package com.zhj.tanhua.circle.pojo.to;

import lombok.Data;

/**
 * @author huanjie.zhuang
 * @date 2021/6/23
 */
@Data
public class CommentTo {

    private String commentId;
    private String momentId;
    private Long userId;
    private String content;
    private String parentId;
    private Long created;
}
