package cn.yiidii.openapi.job.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.yiidii.openapi.free.component.mi.MiBrushStepComponent;
import cn.yiidii.openapi.free.model.form.mi.MiBrushStepForm;
import cn.yiidii.pigeon.common.redis.core.RedisOps;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * LabJobHandler
 *
 * @author YiiDii Wang
 * @create 2021-10-10 18:44
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LabJobHandler {

    private final MiBrushStepComponent miBrushStepComponent;
    private final RedisOps redisOps;

    @XxlJob("miBrushStep")
    public ReturnT<?> miBrushStep() {
        // 获取redis里的信息
        Map<Object, Object> infoMap = redisOps.hmget(MiBrushStepComponent.KEY_MI_BRUSH_STEP_INFO);
        JSONArray ja = new JSONArray();
        infoMap.values().forEach(e -> {
            ja.add(JSONObject.parseObject(e.toString()));
        });
        List<MiBrushStepForm> infoList = JSONArray.parseArray(ja.toJSONString(), MiBrushStepForm.class);

        if (CollUtil.isEmpty(infoList)) {
            return ReturnT.SUCCESS;
        }

        int hour = DateUtil.thisHour(true);
        // 58800 / 16 = 3680
        Long interval = 3680L;
        if (hour < 6 || hour >= 24) {
            // 6点前和22点之后不更新
            ReturnT<String> result = ReturnT.SUCCESS;
            result.setMsg("6点前和22点之后不更新");
            return result;
        } else {
            Long step = RandomUtil.randomLong((hour - 5) * interval - 2000, (hour - 5) * interval + 2000);
            infoList.forEach(e -> {
                e.setStep(step);
                miBrushStepComponent.brushStep(e);
                log.info(StrUtil.format("[{}]自动刷新{}步", e.getPhone(), e.getStep()));
            });
        }
        return ReturnT.SUCCESS;
    }

}
