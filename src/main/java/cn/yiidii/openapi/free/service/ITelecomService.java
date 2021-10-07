package cn.yiidii.openapi.free.service;

import cn.yiidii.openapi.free.model.form.TelecomLoginForm;
import cn.yiidii.pigeon.common.core.base.R;

/**
 * 运营商接口
 *
 * @author YiiDii Wang
 * @create 2021-03-07 16:55
 */
public interface ITelecomService {

    /**
     * 发送手机验证码
     *
     * @param telecomLoginForm telecomLoginForm
     * @return String
     */
    String sendRandomNum(TelecomLoginForm telecomLoginForm);

    /**
     * 验证码登陆
     *
     * @param telecomLoginForm telecomLoginForm
     * @return R R
     */

    R<?> randomLogin(TelecomLoginForm telecomLoginForm);

}
