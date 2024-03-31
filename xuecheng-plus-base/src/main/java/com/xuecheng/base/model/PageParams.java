package com.xuecheng.base.model;

/**
 * Created by Lee Yian on 2024/3/30 13:49
 */

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("分页参数")
public class PageParams {

    //当前页码
    @ApiModelProperty("当前页码，默认值 1")
    private Long pageNo = 1L;

    //每页记录数默认值
    @ApiModelProperty("每页记录数，默认值 10")
    private Long pageSize =10L;

}

