package cn.yiidii.openapi.controller;

import cn.yiidii.openapi.model.form.TelecomLoginForm;
import cn.yiidii.openapi.service.ITelecomService;
import cn.yiidii.pigeon.common.core.base.R;
import cn.yiidii.pigeon.common.core.util.SpringContextHolder;
import cn.yiidii.pigeon.common.strategy.component.HandlerContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 运营商接口
 *
 * @author: YiiDii Wang
 * @create: 2021-03-07 16:52
 */
@Slf4j
@RestController
@RequestMapping("telecom")
@Api(tags = "运营商工具")
@RequiredArgsConstructor
public class TelecomController {

    private final HandlerContext handlerContext;

    @PostMapping("/sendRandomNum")
    @ApiOperation(value = "发手机验证码")
    public R sendRandomNum(@RequestBody @Validated TelecomLoginForm telecomLoginForm) {
        String beanName = handlerContext.getBeanName(telecomLoginForm.getBizCode());
        ITelecomService service = SpringContextHolder.getBean(beanName, ITelecomService.class);
        return R.ok(service.sendRandomNum(telecomLoginForm));
    }

    @PostMapping("/randomLogin")
    @ApiOperation(value = "验证码登陆")
    public R randomLogin(@RequestBody @Validated TelecomLoginForm telecomLoginForm) {
        String beanName = handlerContext.getBeanName(telecomLoginForm.getBizCode());
        ITelecomService service = SpringContextHolder.getBean(beanName, ITelecomService.class);
        return service.randomLogin(telecomLoginForm);
    }
}
