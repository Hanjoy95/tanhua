package com.zhj.tanhua.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author huanjie.zhuang
 * @date 2021/6/12
 */
@ApiModel("分页对象")
@Data
@Builder
public class PageResult<T> implements Serializable {

    @ApiModelProperty(value = "总页数")
    private Long total;
    @ApiModelProperty(value = "当前页")
    private Long pageNum;
    @ApiModelProperty(value = "页大小")
    private Long pageSize;
    @ApiModelProperty(value = "是否有下一页")
    private Boolean hasNext;
    @ApiModelProperty(value = "数据")
    private List<T> data;
}
