package cn.yiidii.openapi.free.controller;

import cn.yiidii.openapi.common.annotation.FlowLimit;
import cn.yiidii.openapi.common.enums.FlowLimitType;
import cn.yiidii.openapi.free.component.document.DocumentComponent;
import cn.yiidii.pigeon.common.core.base.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Doc相关
 *
 * @author YiiDii Wang
 * @create 2021-11-19 23:26
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/free/doc")
@Api(tags = "文档")
public class DocumentController {

    private final DocumentComponent documentComponent;

    @PostMapping("/2pdf")
    @ApiOperation(value = "office文档转换为pdf")
    @FlowLimit(type = {FlowLimitType.INTERVAL}, interval = 5)
    public R<?> doc2PdfGet(@RequestParam("fileList") @NotEmpty(message = "文件不能为空") List<MultipartFile> fileList,
            @RequestParam(required = false, defaultValue = "") String callbackUrl) {
        return R.ok(documentComponent.toPdf(fileList, callbackUrl));
    }

    @PostMapping("/2pdf/task/list")
    @ApiOperation(value = "根据taskId获取office文档转换为pdf的任务列表")
    public R<?> get2PdfTaskWithTaskId(@RequestBody List<String> taskIds) {
        return R.ok(documentComponent.get2PDFTaskWithTaskIds(taskIds));
    }

}
