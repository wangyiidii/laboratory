package cn.yiidii.openapi.free.model.bo.office;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.yiidii.openapi.common.util.OCRUtil;
import cn.yiidii.openapi.free.component.DocumentComponent;
import cn.yiidii.openapi.free.model.ex.DocumentException;
import cn.yiidii.openapi.oss.service.IAttachmentService;
import cn.yiidii.pigeon.common.core.constant.StringPool;
import com.alibaba.fastjson.JSON;
import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

/**
 * OCRTask
 *
 * @author YiiDii Wang
 * @create 2021-11-20 11:46
 */
@Data
@Slf4j
@Accessors(chain = true)
@EqualsAndHashCode
public class OCRTask implements Runnable {

    private String taskId;
    private String callbackUrl;
    private Convert2PdfTaskState state;
    private List<FileInfo> fileInfos;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long consumingTime;

    private DocumentComponent documentComponent;
    private IAttachmentService attachmentService;

    public OCRTask() {
        super();
        this.taskId = DateUtil.formatDate(new Date()).concat(StringPool.DASH).concat(IdUtil.randomUUID());
        this.state = Convert2PdfTaskState.INIT;
        this.startTime = LocalDateTime.now();
    }

    public OCRTask(List<File> fileList) {
        this();
        List<FileInfo> fileInfos =
                fileList.stream().map(f -> new FileInfo().setFile(f)
                        .setFileName(f.getName()))
                        .collect(Collectors.toList());
        this.setFileInfos(fileInfos);
    }

    public OCRTask(List<File> fileList, String callbackUrl) {
        this(fileList);
        this.callbackUrl = callbackUrl;
    }

    @Override
    public void run() {
        this.state = Convert2PdfTaskState.RUNNING;

        int failCount = 0;
        for (FileInfo fileInfo : this.fileInfos) {
            fileInfo.setStartTime(LocalDateTime.now());
            fileInfo.setRemark("识别中");

            // OCR试别
            String ocrResult;
            try {
                ocrResult = OCRUtil.ocr(fileInfo.getFile());
            } catch (Exception e) {
                log.error("OCR失败, e: {}", fileInfo.getFileName(), e.getMessage());
                failCount++;

                fileInfo.setEndTime(LocalDateTime.now());
                fileInfo.setConsumingTime(Duration.between(fileInfo.startTime, fileInfo.endTime).toMillis());

                String message = e.getMessage();
                if (e instanceof DocumentException) {
                    message = StrUtil.format("不支持的文件类型[{}]", FileUtil.getSuffix(fileInfo.file));
                }
                fileInfo.setRemark(message);
                continue;
            } finally {
                // 删除临时文件
                fileInfo.getFile().delete();
            }

            fileInfo.setRemark("识别成功");
            fileInfo.setText(ocrResult);
            fileInfo.setEndTime(LocalDateTime.now());
            fileInfo.setConsumingTime(Duration.between(fileInfo.startTime, fileInfo.endTime).toMillis());
            log.info("文件[{}]OCR成功", fileInfo.fileName);
        }

        if (failCount == 0) {
            this.setState(Convert2PdfTaskState.SUCCESS);
        } else if (failCount == fileInfos.size()) {
            this.setState(Convert2PdfTaskState.FAIL);
        } else {
            this.setState(Convert2PdfTaskState.PART_SUCCESS);
        }
        this.endTime = LocalDateTime.now();
        this.consumingTime = Duration.between(this.startTime, this.endTime).toMillis();

        // callback
        if (StrUtil.isNotBlank(callbackUrl)) {
            List<String> texts = this.fileInfos.stream().map(FileInfo::getText).collect(Collectors.toList());
            HttpRequest.post(callbackUrl).body(JSON.toJSONString(texts)).execute();
        }

    }

    @Data
    @Slf4j
    @Accessors(chain = true)
    @EqualsAndHashCode
    protected static class FileInfo {

        private File file;
        private String fileName;
        private String remark = "暂无信息";
        private String text = "";

        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Long consumingTime;
    }
}
