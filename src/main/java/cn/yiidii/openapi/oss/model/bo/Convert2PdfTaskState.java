package cn.yiidii.openapi.oss.model.bo;

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
     * 已回调
     */
    CALLBACKED("已回调"),
    /**
     * 任务失败
     */
    FAIL("任务失败");

    /**
     * 状态描述
     */
    String desc;

    /**
     * 是否结束
     *
     * @param state 状态
     * @return true 结束
     */
    public static boolean isFinish(Convert2PdfTaskState state) {
        return state.equals(SUCCESS) || state.equals(CALLBACKED) || state.equals(FAIL);
    }

}
