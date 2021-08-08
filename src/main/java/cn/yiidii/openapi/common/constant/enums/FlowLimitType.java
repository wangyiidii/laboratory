package cn.yiidii.openapi.common.constant.enums;

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

    INTERVAL("时间间隔"),
    TIMES("次数");

    private String label;

}
