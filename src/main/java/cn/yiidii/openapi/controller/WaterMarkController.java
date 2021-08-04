package cn.yiidii.openapi.controller;

import cn.yiidii.openapi.common.util.WaterMarkUtil;
import cn.yiidii.openapi.model.vo.DouYinVideoVO;
import cn.yiidii.pigeon.common.core.base.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import lombok.RequiredArgsConstructor;
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

    @PostMapping("dy")
    @ApiOperation("抖音")
    public R<List<DouYinVideoVO>> rmDouYinWaterMark(@RequestBody List<String> links) {
        return R.ok(WaterMarkUtil.rmDouYinWaterMark(links));
    }

}
