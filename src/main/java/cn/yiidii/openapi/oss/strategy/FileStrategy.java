package cn.yiidii.openapi.oss.strategy;

import cn.yiidii.openapi.oss.model.entity.Attachment;
import java.io.File;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件策略接口
 *
 * @author YiiDii Wang
 * @date 2021/3/9 21:52:13
 */
public interface FileStrategy {

    /**
     * 文件上传
     *
     * @param file 文件
     * @return 文件对象
     */
    Attachment upload(MultipartFile file);

    /**
     * 文件上传
     *
     * @param file 文件
     * @return 文件对象
     */
    Attachment upload(File file);

    /**
     * 文件删除
     *
     * @param bucketName bucketName
     * @param objectName objectName
     * @return boolean
     */
    boolean delete(String bucketName, String objectName);

}
