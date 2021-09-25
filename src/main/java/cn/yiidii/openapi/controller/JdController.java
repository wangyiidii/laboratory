package cn.yiidii.openapi.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.PhoneUtil;
import cn.yiidii.openapi.component.selenium.jd.JdChromeSessionManager;
import cn.yiidii.openapi.model.dto.jd.JdInfo;
import cn.yiidii.openapi.model.form.jd.JdChromeLoginForm;
import cn.yiidii.openapi.model.vo.JdChromeSessionVO;
import cn.yiidii.openapi.service.IJdService;
import cn.yiidii.pigeon.common.core.base.R;
import cn.yiidii.pigeon.log.annotation.Log;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import javax.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    private final JdChromeSessionManager jdChromeSessionManager;

    @GetMapping("qrCode")
    @ApiOperation(value = "获取京东登陆二维码")
    @Log(content = "'获取京东登陆二维码'")
    public R<JdInfo> qrCode() throws Exception {
        return R.ok(jdService.getQrCode(), "获取二维码成功");
    }

    @PostMapping("check")
    @ApiOperation(value = "检查并获取cookie")
    public R<JdInfo> check(@RequestBody JdInfo info) throws Exception {
        return R.ok(jdService.checkLogin(info), "获取cookie成功");
    }

    @GetMapping("cookie")
    @Deprecated
    @ApiOperation(value = "获取cookie(通过wsKey)")
    public R<JdInfo> cookie(@RequestParam @Pattern(regexp = "pin=[^;]+;[ ]?wskey=[^;]+;", message = "格式不正确(pin=xxx; wskey=xxx;)") String key) throws Exception {
        return R.ok(jdService.getByWsKey(key), "获取cookie成功");
    }

    @GetMapping("/chrome/sendSmsCode")
    @ApiOperation(value = "[模拟chrome]发送验证码")
    public R<?> mockChromeSendSmsCode(@RequestParam String phone, @RequestParam(required = false) String sid) throws Exception {
        Assert.isTrue(PhoneUtil.isPhone(phone), "手机号码格式不正确");
        JdChromeSessionVO jdChromeSessionVO = jdChromeSessionManager.sendSmsCode(sid, phone);
        return R.ok(jdChromeSessionVO, "发送成功");
    }

    @PostMapping("/chrome/{sid}/login")
    @ApiOperation(value = "[模拟chrome]登录")
    public R<?> mockChromeLogin(@PathVariable String sid, @Validated @RequestBody JdChromeLoginForm form) {
        if (Objects.nonNull(form) && form.isMock()) {
            return R.ok(BeanUtil.toBean(jdChromeSessionManager.get(sid), JdChromeSessionVO.class).setCk("pt_key=xxx; pt_pin=_xxx;"));
        }
        JdChromeSessionVO jdChromeSessionVO = jdChromeSessionManager.login(sid, form.getCode());
        return R.ok(jdChromeSessionVO, "登录成功");
    }

    @GetMapping("/chrome/clear")
    @ApiOperation(value = "[模拟chrome]手动触发清除无用session")
    public R<?> mockChromeClear() {
        jdChromeSessionManager.rmUnusedSession();
        return R.ok("清除完成");
    }

    @GetMapping("/chrome/info")
    @ApiOperation(value = "chrome info")
    public R<?> info() {
        List<JdChromeSessionVO> jdChromeSessionVOS = BeanUtil.copyToList(jdChromeSessionManager.getAllSession().values(), JdChromeSessionVO.class);
        jdChromeSessionVOS.stream().sorted(Comparator.comparing(JdChromeSessionVO::getCreateTime));
        return R.ok(jdChromeSessionVOS);
    }

}
