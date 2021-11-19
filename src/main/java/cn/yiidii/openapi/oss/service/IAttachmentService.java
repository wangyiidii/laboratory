package cn.yiidii.openapi.oss.service;

import cn.yiidii.openapi.oss.model.entity.Attachment;
import cn.yiidii.pigeon.common.core.base.BaseSearchParam;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 业务接口 附件
 * </p>
 *
 * @author zuihou
 * @date 2019-06-24
 */
public interface IAttachmentService extends IService<Attachment> {

    /**
     * 上传附件
     *
     * @param file 文件
     * @return 附件
     */
    Attachment upload(MultipartFile file);

    /**
     * 文件列表
     *
     * @param searchParam searchParam
     * @return Attachment
     */
    IPage<Attachment> list(BaseSearchParam searchParam);

    /**
     * 删除附件
     *
     * @param id               附件ID
     * @param isDeleteInBucket 是否删除桶的图片
     */
    void delete(Long id, boolean isDeleteInBucket);

}
