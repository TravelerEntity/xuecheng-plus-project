package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.service.CourseCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by Lee Yian on 2024/3/31 18:08
 */
@Slf4j
@Api(value = "课程分类接口", tags = "课程分类接口")
@RestController
public class CourseCategoryController {
    @Autowired
    CourseCategoryService courseCategoryService;


    @GetMapping("/course-category/tree-nodes")
    @ApiOperation("按照树形结构获取课程分类")
    public List<CourseCategoryTreeDto> queryTreeNodes() {
        return         courseCategoryService.queryTreeNodes("1") ;
    }
}
