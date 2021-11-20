package cn.yiidii.openapi.free.model.vo;

import cn.yiidii.openapi.oss.model.bo.Convert2PdfTaskState;
import cn.yiidii.openapi.oss.model.entity.Attachment;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

/**
 * Convert2PdfTaskVO
 *
 * @author YiiDii Wang
 * @create 2021-11-20 13:42
 */
@Data
@Slf4j
@Accessors(chain = true)
@EqualsAndHashCode
public class Convert2PdfTaskVO {

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 回调地址
     */
    private String callbackUrl;
    /**
     * 附件
     */
    private Attachment attachment;
    /**
     * 备注
     */
    private String remark;
    /**
     * 状态
     */
    private Convert2PdfTaskState state;
}
