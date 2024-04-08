package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.mapper.TeachplanMediaMapper;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import com.xuecheng.content.service.TeachplanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 课程计划service接口实现类
 */
@Service
public class TeachplanServiceImpl implements TeachplanService {

    @Autowired
    TeachplanMapper teachplanMapper;

    @Autowired
    TeachplanMediaMapper teachplanMediaMapper;

    @Override
    public List<TeachplanDto> findTeachplanTree(long courseId) {
        return teachplanMapper.selectTreeNodes(courseId);
    }

    @Transactional
    @Override
    public void saveTeachplan(SaveTeachplanDto teachplanDto) {

        // 课程计划id
        Long id = teachplanDto.getId();
        // 修改课程计划
        if (id != null) {
            Teachplan teachplan = teachplanMapper.selectById(id);
            BeanUtils.copyProperties(teachplanDto, teachplan);
            teachplanMapper.updateById(teachplan);
        } else {
            // 取出同父同级别的课程计划数量
            int count = getTeachplanCount(teachplanDto.getCourseId(), teachplanDto.getParentid());
            Teachplan teachplanNew = new Teachplan();
            // 设置排序号
            teachplanNew.setOrderby(count + 1);
            BeanUtils.copyProperties(teachplanDto, teachplanNew);

            teachplanMapper.insert(teachplanNew);

        }

    }

    /**
     * @param courseId 课程id
     * @param parentId 父课程计划id
     * @return int 最新排序号
     * @description 获取最新的排序号
     */
    private int getTeachplanCount(long courseId, long parentId) {
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId, courseId);
        queryWrapper.eq(Teachplan::getParentid, parentId);
        Integer count = teachplanMapper.selectCount(queryWrapper);
        return count;
    }

    @Transactional
    @Override
    public List<TeachplanDto> deleteTeachplan(Long id) {
        Teachplan teachplan = teachplanMapper.selectById(id);
        if (teachplan == null) {
            throw new XueChengPlusException("无法找到该章节");
        }
        Long courseId = teachplan.getCourseId();
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getParentid, id);
        Integer count = teachplanMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new XueChengPlusException("存在子章节，无法删除该章节");
        }

        Long teachplanId = teachplan.getId();
        LambdaQueryWrapper<TeachplanMedia> query = new LambdaQueryWrapper<>();
        query.eq(TeachplanMedia::getTeachplanId, teachplanId);
        Integer mediaCount = teachplanMediaMapper.selectCount(query);
        if (mediaCount > 0) {
            teachplanMediaMapper.delete(query);
        }
        teachplanMapper.deleteById(teachplanId);
        return teachplanMapper.selectTreeNodes(courseId);
    }

    @Override
    public void moveup(Long id) {
        Teachplan teachplan = teachplanMapper.selectById(id);
        Long parentid = teachplan.getParentid();
        Integer targetOrderby = teachplan.getOrderby();
        if (targetOrderby == 1) {
            throw new XueChengPlusException("已经是第一个了，无法继续上移");
        }
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getParentid, parentid);
        queryWrapper.lt(Teachplan::getOrderby, targetOrderby);
        Integer count = teachplanMapper.selectCount(queryWrapper);
        if (count >= 1) {
            queryWrapper.eq(Teachplan::getOrderby, targetOrderby - 1);
            Teachplan preTeachplan = teachplanMapper.selectOne(queryWrapper);
            preTeachplan.setOrderby(preTeachplan.getOrderby() + 1);
            teachplanMapper.updateById(preTeachplan);
            teachplan.setOrderby(targetOrderby - 1);
        }
        teachplanMapper.updateById(teachplan);
    }

    @Override
    public void movedown(Long id) {
        Teachplan teachplan = teachplanMapper.selectById(id);
        Long parentid = teachplan.getParentid();
        Integer targetOrderby = teachplan.getOrderby();
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getParentid, parentid);
        queryWrapper.gt(Teachplan::getOrderby, targetOrderby);
        Integer count = teachplanMapper.selectCount(queryWrapper);
        if (count == 0) {
            throw new XueChengPlusException("已经是最后一个了，无法继续下移");
        } else {
            queryWrapper.eq(Teachplan::getOrderby, targetOrderby + 1);
            Teachplan preTeachplan = teachplanMapper.selectOne(queryWrapper);
            preTeachplan.setOrderby(preTeachplan.getOrderby() - 1);
            teachplanMapper.updateById(preTeachplan);
            teachplan.setOrderby(targetOrderby + 1);
        }
        teachplanMapper.updateById(teachplan);
    }
}
