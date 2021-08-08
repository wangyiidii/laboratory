package cn.yiidii.openapi.common.annotation;


import cn.yiidii.openapi.common.constant.enums.FlowLimitType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 限流
 *
 * @author YiiDii Wang
 * @create 2021-08-07 10:31
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FlowLimit {

    /**
     * 限流类型, 默认时间间隔, 0秒. 即: 不限制
     *
     * @return FlowLimitType
     */
    FlowLimitType type() default FlowLimitType.INTERVAL;

    /**
     * 接口调用最小间隔
     *
     * @return long
     */
    long interval() default 0L;

    /**
     * 时间单位
     *
     * @return TimeUnit
     */
    TimeUnit unit() default TimeUnit.SECONDS;

    /**
     * type 为 TIMES有效
     *
     * @return
     */
    int times() default 20;

    /**
     * 匹配带限流的响应信息
     *
     * @return message
     */
    String message() default "请求频率过快";
}
