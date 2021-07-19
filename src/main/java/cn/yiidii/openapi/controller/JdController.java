package cn.yiidii.openapi.controller;

import cn.yiidii.openapi.model.dto.jd.JdInfo;
import cn.yiidii.openapi.service.IJdService;
import cn.yiidii.pigeon.common.core.base.R;
import cn.yiidii.pigeon.log.annotation.Log;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 京东
 *
 * @author YiiDii Wang
 * @create 2021-06-01 10:13
 */
@Api(tags = "京东")
@Slf4j
@RestController
@RequestMapping("jd")
@RequiredArgsConstructor
public class JdController {

    private final IJdService jdService;

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

}
