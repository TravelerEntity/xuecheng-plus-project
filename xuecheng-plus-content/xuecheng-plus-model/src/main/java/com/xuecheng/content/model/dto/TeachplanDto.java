package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * 课程计划 DTO
 */

@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class TeachplanDto extends Teachplan {

  // 课程计划关联的媒资信息
  private TeachplanMedia teachplanMedia;

  // 子结点
  private List<TeachplanDto> teachPlanTreeNodes;

}
