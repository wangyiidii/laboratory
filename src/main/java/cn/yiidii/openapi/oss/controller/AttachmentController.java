package cn.yiidii.openapi.oss.controller;

import cn.yiidii.openapi.oss.model.entity.Attachment;
import cn.yiidii.openapi.oss.service.IAttachmentService;
import cn.yiidii.pigeon.common.core.base.BaseSearchParam;
import cn.yiidii.pigeon.common.core.base.R;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件接口
 *
 * @author YiiDii Wang
 * @create 2021-03-09 23:15
 */
@Slf4j
@RestController
@RequestMapping("attachment")
@Api(tags = "文件接口")
@RequiredArgsConstructor
public class AttachmentController {

    private final IAttachmentService attachmentService;

    @PostMapping("/upload")
    @ApiOperation(value = "文件上传")
    public R<Attachment> uploadFile(@RequestParam(value = "file") MultipartFile file) {
        Attachment attachment = attachmentService.upload(file);
        return R.ok(attachment, "上传成功");
    }

    @GetMapping("/list")
    @ApiOperation(value = "文件列表")
    public R<?> attachment(BaseSearchParam searchParam) {
        IPage<Attachment> pageData = attachmentService.list(searchParam);
        return R.ok(pageData);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "刪除文件")
    public R<?> attachment(@PathVariable Long id, @RequestParam boolean isDeleteInBucket) {
        attachmentService.delete(id, isDeleteInBucket);
        return R.ok(null, "删除成功");
    }
}
