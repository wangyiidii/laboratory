package cn.yiidii.openapi.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 限流类型
 *
 * @author YiiDii Wang
 * @create 2021-08-08 11:22
 */
@Getter
@AllArgsConstructor
public enum FlowLimitType {

    /**
     * 时间间隔
     */
    INTERVAL("时间间隔"),

    /**
     * 次数
     */
    TIMES("次数"),

    /**
     * 时间周期
     */
    PERIOD("时间周期");

    private String label;

}
