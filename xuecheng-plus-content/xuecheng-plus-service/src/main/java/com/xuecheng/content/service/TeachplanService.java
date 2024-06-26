package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.TeachplanMedia;

import java.util.List;

public interface TeachplanService {

    /**
     * 查询课程计划树型结构
     */
    List<TeachplanDto> findTeachplanTree(long courseId);

    void saveTeachplan(SaveTeachplanDto teachplanDto);

    List<TeachplanDto> deleteTeachplan(Long id);

    void moveup(Long id);

    void movedown(Long id);

    void associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto);

    void unassociationMedia(Long teachPlanId, String mediaId);



}
