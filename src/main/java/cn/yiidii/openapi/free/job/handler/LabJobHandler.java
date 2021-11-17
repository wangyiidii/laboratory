package cn.yiidii.openapi.free.job.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.yiidii.openapi.free.component.signin.MiBrushStepComponent;
import cn.yiidii.openapi.free.model.form.signin.MiBrushStepForm;
import cn.yiidii.openapi.free.mongodao.OptLogDAO;
import cn.yiidii.pigeon.common.redis.core.RedisOps;
import cn.yiidii.pigeon.log.model.OptLogDTO;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
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
    private final OptLogDAO optLogDAO;

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
        if (hour < 6 || hour >= 22) {
            // 6点前和22点之后不更新
            ReturnT<String> result = ReturnT.SUCCESS;
            result.setMsg("6点前和22点之后不更新");
            return result;
        } else {
            Long step = RandomUtil.randomLong((hour - 5) * interval - 2000, (hour - 5) * interval + 2000);
            AtomicInteger successCount = new AtomicInteger();
            infoList.forEach(e -> {
                e.setStep(step);
                try {
                    miBrushStepComponent.brushStep(e, false);
                } catch (Exception exception) {
                    log.error(StrUtil.format("{}自动刷新步数异常, e:{}", e.getPhone(), exception.getMessage()));
                }
                successCount.getAndIncrement();
                log.info(StrUtil.format("[{}]自动刷新{}步", e.getPhone(), e.getStep()));
            });

            // 记录
            String randomPhone = infoList.get(RandomUtil.randomInt(infoList.size())).getPhone();
            randomPhone = DesensitizedUtil.mobilePhone(randomPhone);
            String content = StrUtil.format("{}等{}个小伙伴自动刷新了步数", randomPhone, infoList.size());
            optLogDAO.save(buildAutoBrushStep(content));
            return ReturnT.SUCCESS;
        }
    }

    private OptLogDTO buildAutoBrushStep(String content) {
        OptLogDTO optLogDTO = OptLogDTO.builder()
                .type("MI_BRUSH_LATEST_INFO")
                .traceId("")
                .content(content)
                .method("")
                .params("")
                .url("")
                .ip("")
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now())
                .consumingTime(0L)
                .location("")
                .exception("")
                .username(0L)
                .createBy(0L)
                .build();
        return optLogDTO;
    }

}
