package cn.yiidii.openapi.common.aspect;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.yiidii.openapi.common.annotation.FlowLimit;
import cn.yiidii.openapi.common.enums.FlowLimitType;
import cn.yiidii.pigeon.common.core.base.aspect.BaseAspect;
import cn.yiidii.pigeon.common.core.exception.BizException;
import cn.yiidii.pigeon.common.core.util.WebUtils;
import cn.yiidii.pigeon.common.redis.core.RedisOps;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

/**
 * 限流切面
 *
 * @author YiiDii Wang
 * @create 2021-08-07 10:34
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
@ConditionalOnClass(RedisOps.class)
public class FlowLimitAspect extends BaseAspect {

    private final RedisOps redisOps;

    @Pointcut("@annotation(cn.yiidii.openapi.common.annotation.FlowLimit)")
    public void watchFlowLimit() {
    }

    @Before("watchFlowLimit()")
    public void doBefore(JoinPoint joinPoint) {
        FlowLimit flowLimit = getAnnotation(joinPoint, FlowLimit.class);
        if (Objects.isNull(flowLimit)) {
            return;
        }
        final FlowLimitType type = flowLimit.type();
        if (type == FlowLimitType.INTERVAL) {
            handleInterval(flowLimit);
        } else if (type == FlowLimitType.TIMES) {
            handleTimes(flowLimit);
        }
    }

    /**
     * 限制调用间隔模式
     *
     * @param flowLimit FlowLimit
     */
    private void handleInterval(FlowLimit flowLimit) {
        final long interval = flowLimit.interval();
        final TimeUnit unit = flowLimit.unit();

        HttpServletRequest request = WebUtils.getRequest();
        String ip = WebUtils.getIpAddr(request);
        String url = request.getRequestURI();
        String key = StringUtils.join(new String[]{ip, url}, ":");
        final Long pre = redisOps.getExpire(key);
        if (pre > 0) {
            throw new BizException(flowLimit.message());
        }
        redisOps.set(key, "", interval, unit);
    }

    /**
     * 限制调用次数模式
     *
     * @param flowLimit FlowLimit
     */
    private void handleTimes(FlowLimit flowLimit) {
        int timeThreshold = flowLimit.times();

        HttpServletRequest request = WebUtils.getRequest();
        String ip = WebUtils.getIpAddr(request);
        String url = request.getRequestURI();
        Object timesCacheObj = redisOps.hget(ip, url);
        int timeCache = Objects.isNull(timesCacheObj) ? 0 : Integer.parseInt(timesCacheObj.toString());
        if (timeCache >= timeThreshold) {
            throw new BizException("限制调用次数");
        }
        final Date now = new Date();
        long expire = DateUtil.between(now, DateUtil.endOfDay(now), DateUnit.SECOND);
        redisOps.hset(ip, url, ++timeCache, expire);
    }
}
