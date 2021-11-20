package cn.yiidii.openapi.oss.service.impl;

import cn.yiidii.openapi.oss.mapper.AttachmentMapper;
import cn.yiidii.openapi.oss.model.entity.Attachment;
import cn.yiidii.openapi.oss.properties.OssProperties;
import cn.yiidii.openapi.oss.service.IAttachmentService;
import cn.yiidii.openapi.oss.strategy.FileStrategy;
import cn.yiidii.pigeon.common.core.base.BaseSearchParam;
import cn.yiidii.pigeon.common.core.base.enumeration.Status;
import cn.yiidii.pigeon.common.core.exception.BizException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.io.File;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author YiiDii Wang
 * @date 2021/3/9 23:22:01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl extends ServiceImpl<AttachmentMapper, Attachment> implements IAttachmentService {

    private final FileStrategy fileStrategy;
    private final OssProperties ossProperties;
    private final AttachmentMapper attachmentMapper;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Attachment upload(MultipartFile multipartFile) {
        Attachment attachment = fileStrategy.upload(multipartFile);
        attachmentMapper.insert(attachment);
        return attachment;
    }

    @Override
    public Attachment upload(File file) {
        Attachment attachment = fileStrategy.upload(file);
        attachmentMapper.insert(attachment);
        return attachment;
    }

    @Override
    public IPage<Attachment> list(BaseSearchParam searchParam) {
        LambdaQueryWrapper<Attachment> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.between(StringUtils.isNotBlank(searchParam.getStartTime()), Attachment::getCreateTime, searchParam.getStartTime(), searchParam.getEndTime());
        boolean isKeyword = StringUtils.isNotBlank(searchParam.getKeyword());
        queryWrapper.like(isKeyword, Attachment::getFilename, searchParam.getKeyword()).or(isKeyword)
                .like(isKeyword, Attachment::getId, searchParam.getKeyword());
        queryWrapper.eq(Attachment::getStatus, 0);

        // 根据排序字段进行排序
        if (StringUtils.isNotBlank(searchParam.getOrderBy())) {
        }

        // 分页查询
        Page<Attachment> page = new Page<>(searchParam.getCurrent(), searchParam.getSize());
        return this.baseMapper.selectPage(page, queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id, boolean isDeleteInBucket) {
        Attachment attachment = Attachment.builder().id(id).status(Status.DELETED).updateTime(LocalDateTime.now()).build();
        int row = this.baseMapper.updateById(attachment);
        if (1 != row) {
            throw new BizException("文件不存在");
        }
        if (isDeleteInBucket) {
            boolean delete = fileStrategy.delete(ossProperties.getBucketName(), attachment.getUrl());
            if(!delete){
                throw new BizException("删除桶文件失败");
            }
        }
    }

}
