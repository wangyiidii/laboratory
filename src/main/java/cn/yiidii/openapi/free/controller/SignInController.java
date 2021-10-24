package cn.yiidii.openapi.free.controller;

import cn.hutool.core.util.StrUtil;
import cn.yiidii.openapi.free.component.signin.MiBrushStepComponent;
import cn.yiidii.openapi.free.model.form.signin.MiBrushStepForm;
import cn.yiidii.pigeon.common.core.base.R;
import cn.yiidii.pigeon.common.core.exception.BizException;
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
 * 签到接口
 *
 * @author YiiDii Wang
 * @create 2021-10-08 13:00
 */
@Api(tags = "签到接口")
@Slf4j
@RestController
@RequestMapping("/free")
@RequiredArgsConstructor
public class SignInController {

    private final MiBrushStepComponent miBrushStepComponent;

    @PostMapping("/mi/step")
    @ApiOperation("Mi运动刷步数")
    public R<?> miBrushStep(@RequestBody @Validated MiBrushStepForm form) {
        try {
            miBrushStepComponent.brushStep(form);
        } catch (BizException e) {
            log.error(StrUtil.format("[{}]打卡失败, e: {}", form.getPhone(), e.getMessage()));
            throw new BizException(e.getMessage());
        } catch (Exception e) {
            log.error(StrUtil.format("[{}]打卡失败, e: {}", form.getPhone(), e.getMessage()));
            throw new BizException(StrUtil.format("打卡失败"));
        }
        log.info(StrUtil.format("[{}]打卡成功, 步数: {}", form.getPhone(), form.getStep()));
        return R.ok(null, StrUtil.format("打卡成功, 步数: {}", form.getStep()));
    }

}
