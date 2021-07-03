package com.zhj.tanhua.circle.pojo.to;

import lombok.Data;

import java.io.Serializable;

/**
 * @author huanjie.zhuang
 * @date 2021/6/23
 */
@Data
public class CommentTo implements Serializable {

    private String commentId;
    private String momentId;
    private Long userId;
    private String content;
    private String parentId;
    private Long subCommentNum;
    private Long created;
}
