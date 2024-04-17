package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.mapper.TeachplanMediaMapper;
import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import com.xuecheng.content.service.TeachplanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    @Transactional
    @Override
    public void associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto) {
        Long teachplanId = bindTeachplanMediaDto.getTeachplanId();
        // 先根据请求参数查询出对应的教学计划teachplan
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        if (teachplan == null) {
            XueChengPlusException.cast("教学计划不存在");
        }
        // 获取教学计划的层级，只有第二层级允许绑定媒资信息（第二层级为小节，第一层级为章节）
        Integer grade = teachplan.getGrade();
        if (grade != 2) {
            XueChengPlusException.cast("只有小节允许绑定媒资信息");
        }
        // 绑定媒资，如果之前已经绑定过了媒资，再次绑定时为更新（例如该小节已经绑定了 星际牛仔.avi，现在改绑为 胶水.avi，其实现方式为先删再增）
        LambdaQueryWrapper<TeachplanMedia> queryWrapper = new LambdaQueryWrapper<TeachplanMedia>().eq(TeachplanMedia::getTeachplanId, teachplanId);
        // 先删
        teachplanMediaMapper.delete(queryWrapper);
        // 再增
        TeachplanMedia teachplanMedia = new TeachplanMedia();
        teachplanMedia.setTeachplanId(bindTeachplanMediaDto.getTeachplanId());
        teachplanMedia.setMediaFilename(bindTeachplanMediaDto.getFileName());
        teachplanMedia.setMediaId(bindTeachplanMediaDto.getMediaId());
        teachplanMedia.setCourseId(teachplan.getCourseId());
        teachplanMedia.setCreateDate(LocalDateTime.now());
        teachplanMediaMapper.insert(teachplanMedia);
    }

    @Override
    public void unassociationMedia(Long teachPlanId, String mediaId) {
        LambdaQueryWrapper<TeachplanMedia> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TeachplanMedia::getTeachplanId, teachPlanId)
                .eq(TeachplanMedia::getMediaId, mediaId);
        teachplanMediaMapper.delete(queryWrapper);
    }



}
