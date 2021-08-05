package cn.yiidii.openapi.service;

import cn.yiidii.openapi.model.form.TelecomLoginForm;
import cn.yiidii.pigeon.common.core.base.R;

/**
 * 运营商接口
 *
 * @author: YiiDii Wang
 * @create: 2021-03-07 16:55
 */
public interface ITelecomService {

    /**
     * 发送手机验证码
     *
     * @param telecomLoginForm
     * @return
     */
    String sendRandomNum(TelecomLoginForm telecomLoginForm);

    /**
     * 验证码登陆
     *
     * @param telecomLoginForm
     * @return
     */
    R randomLogin(TelecomLoginForm telecomLoginForm);

}
