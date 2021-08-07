package cn.yiidii.openapi.common.annotation;


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
     * 接口调用最小间隔, 单位
     *
     * @return long
     */
    long interval() default 0L;

    TimeUnit unit() default TimeUnit.MILLISECONDS;

    String message() default "请求频率过快";
}
