package cn.yiidii.openapi.free.model.ex;

import cn.yiidii.pigeon.common.core.exception.BaseUncheckedException;

/**
 * Document 异常
 *
 * @author YiiDii Wang
 * @create 2021-06-01 11:07
 */
public class DocumentException extends BaseUncheckedException {

    public DocumentException(String message) {
        super(-1, message);
    }

    public DocumentException(int code, String message) {
        super(code, message);
    }
}
