package cn.yiidii.openapi.netty.wschat.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应类型
 *
 * @author YiiDii Wang
 * @create 2021-08-14 10:34
 */
@Getter
@AllArgsConstructor
public enum Type {

    POP_INFO("10", "弹出info"),
    POP_WARNING("20", "弹出Warning"),
    NICKNAME_REPEATED("30", "昵称重复"),
    MESSAGE("40", "消息"),
    ONLINE_NUM("30", "在线人数");

    private String cmd;
    private String desc;

}

