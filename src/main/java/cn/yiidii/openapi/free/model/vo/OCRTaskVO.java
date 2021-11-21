package cn.yiidii.openapi.free.model.vo;

import cn.yiidii.openapi.free.model.bo.office.Convert2PdfTaskState;
import cn.yiidii.openapi.oss.model.entity.Attachment;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

/**
 * OCR Task VO
 *
 * @author YiiDii Wang
 * @create 2021-11-20 13:42
 */
@Data
@Slf4j
@Accessors(chain = true)
@EqualsAndHashCode
public class OCRTaskVO {

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 回调地址
     */
    private String callbackUrl;

    /**
     * 状态
     */
    private Convert2PdfTaskState state;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 耗时 ms
     */
    private Long consumingTime;
    /**
     * 文件
     */
    private List<FileInfoVO> fileInfos;

    @Data
    @Slf4j
    @Accessors(chain = true)
    @EqualsAndHashCode
    protected static class FileInfoVO {

        private String fileName;
        private String text;
        private String remark;

        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Long consumingTime;
    }
}
