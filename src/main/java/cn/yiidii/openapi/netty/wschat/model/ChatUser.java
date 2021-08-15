package cn.yiidii.openapi.netty.wschat.model;

import lombok.Builder;
import lombok.Data;
import org.yeauty.pojo.Session;

/**
 * 用户
 *
 * @author YiiDii Wang
 * @create 2021-08-13 18:20
 */
@Data
@Builder
public class ChatUser {

    private String id;
    private String nickname;
    private Session session;
}
