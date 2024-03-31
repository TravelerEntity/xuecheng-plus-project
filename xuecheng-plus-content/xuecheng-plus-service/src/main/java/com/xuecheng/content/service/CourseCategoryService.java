package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CourseCategoryTreeDto;

import java.util.List;

/**
 * Created by Lee Yian on 2024/3/31 22:06
 */
public interface CourseCategoryService {
    List<CourseCategoryTreeDto> queryTreeNodes(String id);
}
