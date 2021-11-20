package cn.yiidii.openapi.free.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.yiidii.openapi.common.annotation.FlowLimit;
import cn.yiidii.openapi.common.enums.FlowLimitType;
import cn.yiidii.openapi.free.component.DocumentComponent;
import cn.yiidii.openapi.free.model.vo.Convert2PdfTaskVO;
import cn.yiidii.openapi.oss.model.bo.Convert2PdfTask;
import cn.yiidii.openapi.oss.model.bo.Convert2PdfTaskState;
import cn.yiidii.pigeon.common.core.base.R;
import cn.yiidii.pigeon.common.core.exception.BizException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
    public R<?> doc2PdfGet(@RequestParam("file") MultipartFile file,
            @RequestParam(required = false, defaultValue = "") String callbackUrl) {
        Convert2PdfTask task = documentComponent.toPdf(file, callbackUrl);
        Convert2PdfTaskVO vo = new Convert2PdfTaskVO();
        BeanUtils.copyProperties(task, vo);
        return R.ok(vo);
    }

    @PostMapping("/2pdf/{taskId}")
    public R<?> getWithTaskId(@PathVariable String taskId) {
        Convert2PdfTask taskExist = DocumentComponent.CONVERT_TASK_MAP.get(taskId);
        if (Objects.isNull(taskExist)) {
            throw new BizException("任务不存在");
        }
        Convert2PdfTaskState state = taskExist.getState();
        if (!Convert2PdfTaskState.isFinish(state)) {
            throw new BizException(state.getDesc());
        }
        Convert2PdfTaskVO taskVO = BeanUtil.toBean(taskExist, Convert2PdfTaskVO.class);
        return R.ok(taskVO);
    }

}
