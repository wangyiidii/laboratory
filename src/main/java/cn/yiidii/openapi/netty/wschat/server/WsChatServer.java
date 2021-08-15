package cn.yiidii.openapi.netty.wschat.server;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.yiidii.openapi.netty.wschat.manager.SessionManager;
import cn.yiidii.openapi.netty.wschat.model.ChatConstant;
import cn.yiidii.openapi.netty.wschat.model.ChatResponse;
import cn.yiidii.openapi.netty.wschat.model.ChatUser;
import cn.yiidii.openapi.netty.wschat.model.Type;
import com.alibaba.fastjson.JSON;
import io.netty.handler.codec.http.HttpHeaders;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.yeauty.annotation.OnClose;
import org.yeauty.annotation.OnError;
import org.yeauty.annotation.OnMessage;
import org.yeauty.annotation.OnOpen;
import org.yeauty.annotation.RequestParam;
import org.yeauty.annotation.ServerEndpoint;
import org.yeauty.pojo.Session;

/**
 * Web IM Server
 *
 * @author YiiDii Wang
 * @create 2021-08-13 17:32
 */
@Slf4j
@ServerEndpoint(path = "${pigeon.netty.wsChat.server.path:/ws/chat}", port = "${pigeon.netty.wsChat.server.port:7878}")
public class WsChatServer {

    @OnOpen
    public void onOpen(Session session, HttpHeaders headers, @RequestParam String nickname, @RequestParam String roomId) {
        if (StrUtil.isBlank(nickname)) {
            // 没有nickname直接关闭通道
            session.channel().close();
        }
        // nickname 重复检验
        final List<String> nicknameList = SessionManager.getNicknameList(null);
        if (nicknameList.contains(nickname)) {
            final ChatResponse resp = ChatResponse.builder().type(Type.NICKNAME_REPEATED).content(StrUtil.format("昵称{}已存在", nickname)).build();
            session.sendText(JSON.toJSONString(resp));
            session.channel().close();
        }
        // 添加用户, 并进入房间，默认为大厅
        roomId = StrUtil.isBlank(roomId) ? ChatConstant.DFT_ROOM_ID : roomId;
        String uid = RandomUtil.randomStringUpper(12);
        ChatUser user = ChatUser.builder().id(uid).nickname(nickname).session(session).build();
        SessionManager.add(user, roomId);
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        SessionManager.remove(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        SessionManager.remove(session);
        throwable.printStackTrace();
    }


    @OnMessage
    public void onMessage(Session session, String message) {
        SessionManager.speakInRoom(session, message);
    }

}
