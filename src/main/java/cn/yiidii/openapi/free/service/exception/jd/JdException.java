package cn.yiidii.openapi.free.service.exception.jd;

import cn.yiidii.pigeon.common.core.exception.BaseUncheckedException;

/**
 * 京东异常
 *
 * @author YiiDii Wang
 * @create 2021-06-01 11:07
 */
public class JdException extends BaseUncheckedException {
    public JdException(int code, String message) {
        super(code, message);
    }
}
