package cn.yiidii.openapi.service;

import cn.yiidii.openapi.model.dto.jd.JdInfo;

/**
 * 京东业务接口
 *
 * @author YiiDii Wang
 * @create 2021-06-01 10:14
 */
public interface IJdService {

    /**
     * 获取京东信息
     *
     * @return JdInfo
     * @throws Exception
     */
    JdInfo getQrCode() throws Exception;

    /**
     * 检查二维码是否扫描
     *
     * @param info info
     * @throws Exception e
     * @return JdInfo
     */
    JdInfo checkLogin(JdInfo info) throws Exception;

}
