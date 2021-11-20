package cn.yiidii.openapi.free.model.bo.office;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Convert2PdfTaskState
 *
 * @author YiiDii Wang
 * @create 2021-11-20 13:56
 */
@Getter
@AllArgsConstructor
public enum Convert2PdfTaskState {
    /**
     * 等待执行
     */
    INIT("等待执行"),
    /**
     * 任务执行中
     */
    RUNNING("任务执行中"),
    /**
     * 转换成功
     */
    SUCCESS("转换成功"),
    /**
     * 转换成功
     */
    PART_SUCCESS("部分转换成功"),
    /**
     * 任务失败
     */
    FAIL("任务失败");

    /**
     * 状态描述
     */
    String desc;

}
