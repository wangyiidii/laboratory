package cn.yiidii.openapi.netty.wschat.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * 响应
 *
 * @author YiiDii Wang
 * @create 2021-08-14 10:38
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode
public class ChatResponse {

    private Type type;
    private String nickname;
    private String content;


}
