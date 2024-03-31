package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.CourseCategory;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Lee Yian on 2024/3/31 18:05
 */

@Data
public class CourseCategoryTreeDto extends CourseCategory implements Serializable {
    private static final long serialVersionUID = -1L;

    private List<CourseCategory> childrenTreeNodes;

}