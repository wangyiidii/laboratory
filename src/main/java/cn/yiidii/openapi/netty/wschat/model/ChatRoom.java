package cn.yiidii.openapi.netty.wschat.model;

import com.alibaba.fastjson.annotation.JSONField;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * 房间
 *
 * @author YiiDii Wang
 * @create 2021-08-13 18:41
 */
@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class ChatRoom {

    private String id;
    private String name;
    private List<String> userList;
    private List<HistoryMessage> historyMessageList;

    @Data
    @Builder
    @RequiredArgsConstructor
    @AllArgsConstructor
    public static class HistoryMessage {

        private String mid;
        private String nickname;
        private String content;
        @JSONField(format = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime timestamp;
    }
}
