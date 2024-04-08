package com.xuecheng.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 课程计划 Mapper 接口
 * </p>
 *
 * @author Lin Yun
 */
public interface TeachplanMapper extends BaseMapper<Teachplan> {
    List<TeachplanDto> selectTreeNodes(long courseId);

    @Select("SELECT MAX(orderby) FROM teachplan WHERE course_id = #{courseId} AND parentid = #{parentId}")
    Integer selectMaxOrderBy(@Param("courseId") long courseId, @Param("parentId") long parentId);
}
