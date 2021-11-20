package cn.yiidii.openapi.oss.model.bo;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.yiidii.openapi.common.util.Office2Pdf;
import cn.yiidii.openapi.free.component.DocumentComponent;
import cn.yiidii.openapi.oss.model.entity.Attachment;
import cn.yiidii.openapi.oss.service.IAttachmentService;
import cn.yiidii.pigeon.common.core.constant.StringPool;
import com.alibaba.fastjson.JSON;
import java.io.File;
import java.util.Date;
import javax.activation.UnsupportedDataTypeException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

/**
 * Convert2PdfTask
 *
 * @author YiiDii Wang
 * @create 2021-11-20 11:46
 */
@Data
@Slf4j
@Accessors(chain = true)
@EqualsAndHashCode
public class Convert2PdfTask implements Runnable {

    private String taskId;
    private File file;
    private String fileName;
    private String callbackUrl;
    private Attachment attachment;
    private String remark;
    private Convert2PdfTaskState state;

    private DocumentComponent documentComponent;
    private IAttachmentService attachmentService;

    public Convert2PdfTask() {
        super();
        this.taskId = DateUtil.formatDate(new Date()).concat(StringPool.DASH).concat(IdUtil.randomUUID());
        this.state = Convert2PdfTaskState.INIT;
    }

    public Convert2PdfTask(File file) {
        this();
        this.setFile(file);
        this.fileName = FileUtil.mainName(file);
    }

    public Convert2PdfTask(File file, String callbackUrl) {
        this(file);
        this.callbackUrl = callbackUrl;
    }

    @Override
    public void run() {
        this.state = Convert2PdfTaskState.RUNNING;
        // 转换
        File resultFile;
        try {
            resultFile = Office2Pdf.convert(this.file);
        } catch (Exception e) {
            this.state = Convert2PdfTaskState.FAIL;
            log.error("文件[{}]转换失败, e: {}", this.fileName, e.getMessage());

            String message = e.getMessage();
            if (e instanceof UnsupportedDataTypeException) {
                message = StrUtil.format("不支持的文件类型[{}]", FileUtil.getSuffix(this.file));
            }
            this.setRemark(StrUtil.format("转换失败, 文件名称: {}, 失败原因: {}", file.getName(), message));
            return;
        } finally {
            // 删除临时文件
            this.file.delete();
        }
        // 上传oss
        Attachment attachment;
        try {
            attachment = this.attachmentService.upload(resultFile);
        } catch (Exception e) {
            this.state = Convert2PdfTaskState.FAIL;
            log.error("文件[{}]转换失败, e: {}", this.fileName, e.getMessage());

            this.setRemark(StrUtil.format("转换失败, 文件名称: {}, 失败原因: 上传至oss失败", file.getName()));
            return;
        } finally {
            // 删除源文件
            resultFile.delete();
        }

        this.setAttachment(attachment);
        this.state = Convert2PdfTaskState.SUCCESS;
        this.setRemark("转换成功");
        log.info("文件[{}]转换成功", this.fileName);

        // callback
        if (StrUtil.isNotBlank(callbackUrl)) {
            HttpRequest.post(callbackUrl).body(JSON.toJSONString(attachment)).execute();
            this.state = Convert2PdfTaskState.CALLBACKED;
            log.info("文件[{}]转换成功, 回调: {}", this.fileName, callbackUrl);
        }
    }
}
