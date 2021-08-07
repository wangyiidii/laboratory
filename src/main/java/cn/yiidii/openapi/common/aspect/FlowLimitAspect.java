package cn.yiidii.openapi.common.aspect;

import cn.yiidii.openapi.common.annotation.FlowLimit;
import cn.yiidii.pigeon.common.core.base.aspect.BaseAspect;
import cn.yiidii.pigeon.common.core.exception.BizException;
import cn.yiidii.pigeon.common.core.util.WebUtils;
import cn.yiidii.pigeon.common.redis.core.RedisOps;
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

}
