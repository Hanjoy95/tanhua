package com.zhj.tanhua.circle.po;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;

/**
 * 发布表，用户发布的动态内容
 *
 * @author huanjie.zhuang
 * @date 2021/6/17
 */
@Data
@Document(collection = "circle_publish")
public class Publish implements Serializable {

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
     * 媒体数据，图片或小视频（url）
     */
    private List<String> medias;
    /**
     * 谁可以看，1-公开，2-私密，3-部分可见，4-不给谁看
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
    private Long created;
}
