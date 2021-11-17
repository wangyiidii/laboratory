package cn.yiidii.openapi.free.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.StrUtil;
import cn.yiidii.openapi.common.annotation.FlowLimit;
import cn.yiidii.openapi.common.enums.FlowLimitType;
import cn.yiidii.openapi.free.component.signin.MiBrushStepComponent;
import cn.yiidii.openapi.free.model.form.signin.MiBrushStepForm;
import cn.yiidii.openapi.free.model.vo.signin.MiBrushStepTopNVO;
import cn.yiidii.openapi.free.mongodao.OptLogDAO;
import cn.yiidii.pigeon.common.core.base.R;
import cn.yiidii.pigeon.common.core.exception.BizException;
import cn.yiidii.pigeon.log.annotation.Log;
import cn.yiidii.pigeon.log.model.OptLogDTO;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    private final OptLogDAO optLogDAO;

    @PostMapping("/mi/step")
    @ApiOperation("Mi运动刷步数")
    @Log(content = "#form.phone + '刷新了' + #form.step + '步'", type = "MI_BRUSH_LATEST_INFO")
    @FlowLimit(type = FlowLimitType.PERIOD, periods = "06:00:00-22:00:00")
    public R<?> miBrushStep(@RequestBody @Validated MiBrushStepForm form) {
        try {
            miBrushStepComponent.brushStep(form, true);
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

    @GetMapping("/mi/step/latest")
    @ApiOperation("Mi运动刷步数topN")
    public R<?> miBrushStepTopN(@RequestParam(defaultValue = "10") Integer topN) {
        List<OptLogDTO> optLogDTOS = optLogDAO.find(new Query().addCriteria(Criteria.where("type").is("MI_BRUSH_LATEST_INFO")).with(Sort.by(Order.desc("startTime"))).limit(topN));
        List<MiBrushStepTopNVO> vos = optLogDTOS.stream().map(e -> {
            MiBrushStepTopNVO vo = BeanUtil.toBean(e, MiBrushStepTopNVO.class);
            try {
                JSONObject paramJo = JSONArray.parseArray(e.getParams()).getJSONObject(0);
                vo.setContent(StrUtil.format("{}刷新了{}步",
                        DesensitizedUtil.mobilePhone(paramJo.getString("phone")),
                        paramJo.getString("step")));
            } catch (Exception ex) {
            }
            return vo;
        }).distinct().collect(Collectors.toList());
        return R.ok(vos);
    }

}
