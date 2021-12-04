package cn.yiidii.openapi.free.component.document;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.yiidii.openapi.common.util.Office2Pdf;
import cn.yiidii.openapi.free.model.bo.office.Convert2PdfTask;
import cn.yiidii.openapi.free.model.vo.Convert2PdfTaskVO;
import cn.yiidii.openapi.oss.model.entity.Attachment;
import cn.yiidii.openapi.oss.service.IAttachmentService;
import cn.yiidii.pigeon.common.core.constant.StringPool;
import cn.yiidii.pigeon.common.core.exception.BizException;
import com.google.common.collect.Maps;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

/**
 * Doc
 *
 * @author YiiDii Wang
 * @create 2021-11-19 23:32
 */
@Slf4j
@Component
public class DocumentComponent {

    private static final String TEMP_DIR = "./temp";
    private static final Map<String, Convert2PdfTask> CONVERT_TASK_MAP = Maps.newConcurrentMap();

    private final IAttachmentService attachmentService;
    private final ThreadPoolTaskExecutor executor;

    public DocumentComponent(IAttachmentService attachmentService, @Qualifier("asyncExecutor") ThreadPoolTaskExecutor executor) {
        this.attachmentService = attachmentService;
        this.executor = executor;
    }

    /**
     * 转为pdf并上传oss
     *
     * @param multipartFile attachment
     * @return attachment
     */
    public Attachment toPdf(MultipartFile multipartFile) {
        File resultFile;
        // 临时文件
        File temp = getTempFile(multipartFile);
        try {
            FileUtil.writeBytes(multipartFile.getBytes(), temp);
            // 转换
            resultFile = Office2Pdf.convert(temp);
        } catch (IOException e) {
            throw new BizException("转换异常");
        } finally {
            temp.delete();
        }
        // 上传oss
        Attachment attachment = attachmentService.upload(resultFile);
        return attachment;
    }

    /**
     * 带回调的转换pdf
     *
     * @param multipartFileList 带转换的文件
     * @param callbackUrl       回调地址
     * @return 附件
     */
    public Convert2PdfTaskVO toPdf(List<MultipartFile> multipartFileList, String callbackUrl) {
        Assert.isTrue(CollUtil.isNotEmpty(multipartFileList), "文件不能为空");

        // 临时文件
        List<File> tempFileList = multipartFileList.stream().map(mf -> {
            File temp = getTempFile(mf);
            try {
                FileUtil.writeBytes(mf.getBytes(), temp);
            } catch (IOException e) {
                temp.delete();
            }
            return temp;
        }).collect(Collectors.toList());

        // 异步任务
        Convert2PdfTask convert2PdfTask = new Convert2PdfTask(tempFileList, callbackUrl)
                .setDocumentComponent(this)
                .setAttachmentService(attachmentService);
        CONVERT_TASK_MAP.put(convert2PdfTask.getTaskId(), convert2PdfTask);
        executor.submit(convert2PdfTask);

        return BeanUtil.toBean(convert2PdfTask, Convert2PdfTaskVO.class);
    }

    /**
     * 根据taskId获取office文档转换为pdf的任务列表
     *
     * @param taskIds 任务id
     * @return 任务列表
     */
    public List<Convert2PdfTaskVO> get2PDFTaskWithTaskIds(List<String> taskIds) {
        return taskIds.stream()
                .filter(taskId -> CONVERT_TASK_MAP.containsKey(taskId))
                .map(taskId -> BeanUtil.toBean(CONVERT_TASK_MAP.get(taskId), Convert2PdfTaskVO.class))
                .sorted(Comparator.comparing(Convert2PdfTaskVO::getStartTime).reversed())
                .collect(Collectors.toList());
    }

    /**
     * multipartFile转为临时file文件
     * <p/>
     * 如无必要, 用完需要删除
     *
     * @param multipartFile multipartFile
     * @return 临时file文件
     */
    private File getTempFile(MultipartFile multipartFile) {
        String originalFilename = new String(multipartFile.getOriginalFilename().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        String suffix = FileUtil.getSuffix(originalFilename);
        String filePath = TEMP_DIR.concat(File.separator)
                .concat(FileUtil.mainName(originalFilename)).concat(StringPool.DOT).concat(suffix);
        return FileUtil.file(filePath);
    }

}
