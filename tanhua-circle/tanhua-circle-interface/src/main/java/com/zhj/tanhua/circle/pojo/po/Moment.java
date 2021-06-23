package com.zhj.tanhua.circle.pojo.po;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;

/**
 * 动态表，用于存储自己发布的动态
 *
 * @author huanjie.zhuang
 * @date 2021/6/17
 */
@Data
@Document(collection = "circle_moment")
public class Moment implements Serializable {

    /**
     * 主键ID
     */
    @Id
    private ObjectId id;
    /**
     * 用户ID
     */
    @Indexed
    private Long userId;
    /**
     * 发布内容
     */
    private String text;
    /**
     * 图片或小视频（url）
     */
    private List<String> medias;
    /**
     * 谁可以看，0-公开，1-私密，2-部分可见，3-不给谁看
     */
    private Integer seeType;
    /**
     * 部分可见的用户ID列表
     */
    private List<Long> seeList;
    /**
     * 不给谁看的用户ID列表
     */
    private List<Long> notSeeList;
    /**
     * 点赞数
     */
    private Long likeNum;
    /**
     * 点赞用户
     */
    private List<Long> likeUsers;
    /**
     * 经度
     */
    private String longitude;
    /**
     * 纬度
     */
    private String latitude;
    /**
     * 位置
     */
    private String location;
    /**
     * 发布时间
     */
    @Indexed
    private Long created;
}
