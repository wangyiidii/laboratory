package cn.yiidii.openapi.free.controller;

import cn.yiidii.openapi.common.annotation.FlowLimit;
import cn.yiidii.openapi.common.enums.FlowLimitType;
import cn.yiidii.openapi.free.component.DocumentComponent;
import cn.yiidii.openapi.free.model.bo.office.Convert2PdfTask;
import cn.yiidii.openapi.free.model.vo.Convert2PdfTaskVO;
import cn.yiidii.pigeon.common.core.base.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
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
    public R<?> doc2PdfGet(@RequestParam("fileList") List<MultipartFile> fileList,
            @RequestParam(required = false, defaultValue = "") String callbackUrl) {
        Convert2PdfTask task = documentComponent.toPdf(fileList, callbackUrl);
        Convert2PdfTaskVO vo = new Convert2PdfTaskVO();
        BeanUtils.copyProperties(task, vo);
        return R.ok(vo);
    }

    @PostMapping("/2pdf/file/list")
    public R<?> getWithTaskId(@RequestBody List<String> taskIds) {
        return R.ok(documentComponent.getWithTaskIds(taskIds));
    }

}
