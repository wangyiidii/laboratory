package cn.yiidii.openapi.free.controller;

import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.PhoneUtil;
import cn.hutool.core.util.StrUtil;
import cn.yiidii.openapi.free.model.dto.jd.JdInfo;
import cn.yiidii.openapi.free.service.IJdService;
import cn.yiidii.pigeon.common.core.base.R;
import cn.yiidii.pigeon.common.core.exception.BizException;
import cn.yiidii.pigeon.log.annotation.Log;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 京东
 *
 * @author YiiDii Wang
 * @create 2021-06-01 10:13
 */
@Api(tags = "京东")
@Slf4j
@Validated
@RestController
@RequestMapping("jd")
@RequiredArgsConstructor
public class JdController {

    private final IJdService jdService;

    @GetMapping("smsCode")
    @ApiOperation(value = "发送验证码")
    @Log(content = "#mobile + '发送验证码'", type = "JD_COOKIE")
    public R<JdInfo> qrCode(@RequestParam @NotNull(message = "请填写手机号") String mobile) throws Exception {
        Assert.isTrue(PhoneUtil.isMobile(mobile), () -> {
            throw new BizException("手机号格式不正确");
        });
        return R.ok(jdService.sendSmsCode(mobile), "发送验证码成功");
    }

    @PostMapping("login")
    @ApiOperation(value = "登录")
    public R<JdInfo> check(@RequestBody JSONObject paramJo) throws Exception {
        String mobile = paramJo.getString("mobile");
        String code = paramJo.getString("code");
        Assert.isTrue(StrUtil.isNotBlank(mobile), () -> {
            throw new BizException("手机号不能为空");
        });
        Assert.isTrue(PhoneUtil.isMobile(mobile), () -> {
            throw new BizException("手机号格式不正确");
        });
        Assert.isTrue(StrUtil.isNotBlank(code), () -> {
            throw new BizException("验证码不能为空");
        });
        JdInfo jdInfo = jdService.login(mobile, code);
        log.info(StrUtil.format("{}获取了Cookie", DesensitizedUtil.mobilePhone(mobile)));
        return R.ok(jdInfo, "获取cookie成功");
    }

    @GetMapping("cookie")
    @Deprecated
    @ApiOperation(value = "获取cookie(通过wsKey)")
    public R<JdInfo> cookie(@RequestParam @Pattern(regexp = "pin=[^;]+;[ ]?wskey=[^;]+;", message = "格式不正确(pin=xxx; wskey=xxx;)") String key) throws Exception {
        return R.ok(jdService.getByWsKey(key), "获取cookie成功");
    }

}
