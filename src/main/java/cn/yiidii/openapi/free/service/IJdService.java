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
     * 获取京东信息
     *
     * @return JdInfo
     * @throws Exception e
     */
    JdInfo getQrCode() throws Exception;

    /**
     * 检查二维码是否扫描
     *
     * @param info info
     * @return JdInfo
     * @throws Exception e
     */
    JdInfo checkLogin(JdInfo info) throws Exception;

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