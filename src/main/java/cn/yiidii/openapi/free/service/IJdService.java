package cn.yiidii.openapi.free.service;

import cn.yiidii.openapi.free.model.dto.jd.JdInfo;

/**
 * 京东业务接口
 *
 * @author YiiDii Wang
 * @create 2021-06-01 10:14
 */
public interface IJdService {

    /**
     * 发送验证码
     *
     * @param mobile 手机号
     * @return JdInfo
     * @throws Exception e
     */
    JdInfo sendSmsCode(String mobile) throws Exception;

    /**
     * 登录
     *
     * @param mobile 手机号
     * @param code   验证码
     * @return JdInfo
     * @throws Exception e
     */
    JdInfo login(String mobile, String code) throws Exception;

    /**
     * 通过wsKey获取cookie
     * <p/>
     * wsKey通过抓JD app包获取
     *
     * @param wsKey wsKey
     * @return JdInfo
     * @throws Exception e
     */
    JdInfo getByWsKey(String wsKey) throws Exception;
}
