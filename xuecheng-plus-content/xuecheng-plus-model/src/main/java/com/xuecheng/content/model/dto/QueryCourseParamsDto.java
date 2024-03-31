package com.xuecheng.content.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * Created by Lee Yian on 2024/3/30 13:52
 * 课程 POST 查询参数
 */
@Data
@ToString
@ApiModel(value = "课程查询参数")
public class QueryCourseParamsDto {

    //审核状态
    @ApiModelProperty(value = "审核状态")
    private String auditStatus;
    //课程名称
    @ApiModelProperty(value = "课程名称")
    private String courseName;
    //发布状态
    @ApiModelProperty(value = "课程发布状态")
    private String publishStatus;

}
