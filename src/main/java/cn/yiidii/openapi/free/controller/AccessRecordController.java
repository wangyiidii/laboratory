package cn.yiidii.openapi.free.controller;

import cn.yiidii.openapi.free.model.bo.system.AccessRecordBO;
import cn.yiidii.openapi.free.model.bo.system.AccessTrendBO;
import cn.yiidii.openapi.free.model.entity.system.AccessRecord;
import cn.yiidii.openapi.free.model.form.AccessRecordForm;
import cn.yiidii.openapi.free.model.vo.AccessOverviewVO;
import cn.yiidii.openapi.free.service.IAccessRecordService;
import cn.yiidii.pigeon.common.core.base.R;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author YiiDii Wang
 * @create 2021-07-31 23:41
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("accessRecord")
@Api(tags = "访问记录")
public class AccessRecordController {

    private final IAccessRecordService accessRecordService;

    @PostMapping
    @ApiOperation(value = "添加一个访问记录")
    private R<AccessRecord> saveOne(@RequestBody AccessRecordForm form) {
        AccessRecord record = accessRecordService.addOne(form);
        return R.ok(record);
    }

    @GetMapping("statistics")
    @ApiOperation(value = "统计")
    private R<List<AccessRecordBO>> statistics(@RequestParam String group, @RequestParam(required = false, defaultValue = "0") Integer topN) {
        return R.ok(accessRecordService.statistic(group, topN));
    }


    @GetMapping("trend")
    @ApiOperation(value = "访问趋势图")
    private R<JSONObject> accessTrend() {
        List<AccessTrendBO> accessTrendBOList = accessRecordService.accessTrend();
        List<Integer> x = accessTrendBOList.stream().map(AccessTrendBO::getHour).collect(Collectors.toList());
        List<Integer> y = accessTrendBOList.stream().map(AccessTrendBO::getNum).collect(Collectors.toList());
        JSONObject result = new JSONObject();
        result.put("x", x);
        result.put("y", y);
        return R.ok(result);
    }

    @GetMapping("overview")
    @ApiOperation(value = "概览")
    private R<AccessOverviewVO> overview(@RequestParam(required = false, defaultValue = "/") String path) {
        return R.ok(accessRecordService.getAccessOverview(path));
    }

}
