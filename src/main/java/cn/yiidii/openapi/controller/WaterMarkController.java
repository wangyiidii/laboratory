package cn.yiidii.openapi.controller;

import cn.yiidii.openapi.component.wm.BaseRmWaterMarkHandler;
import cn.yiidii.openapi.model.form.RmWaterMarkForm;
import cn.yiidii.openapi.model.vo.DouYinVideoVO;
import cn.yiidii.pigeon.common.core.base.R;
import cn.yiidii.pigeon.common.core.util.SpringContextHolder;
import cn.yiidii.pigeon.common.strategy.component.HandlerContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 水印
 *
 * @author YiiDii Wang
 * @create 2021-08-04 11:49
 */
@RestController
@RequestMapping("wm")
@Api(tags = "去水印工具")
@RequiredArgsConstructor
public class WaterMarkController {

    private final HandlerContext handlerContext;

    @PostMapping
    @ApiOperation("去水印")
    public R<List<DouYinVideoVO>> rmDouYinWaterMark(@RequestBody @Validated RmWaterMarkForm form) {
        String beanName = handlerContext.getBeanName(form.getBizCode());
        BaseRmWaterMarkHandler handler = SpringContextHolder.getBean(beanName, BaseRmWaterMarkHandler.class);
        return R.ok(handler.remove(form.getLinks()));
    }

}
