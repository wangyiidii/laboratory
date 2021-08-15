package cn.yiidii.openapi.netty.wschat.manager;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.yiidii.openapi.netty.wschat.model.ChatConstant;
import cn.yiidii.openapi.netty.wschat.model.ChatResponse;
import cn.yiidii.openapi.netty.wschat.model.ChatRoom;
import cn.yiidii.openapi.netty.wschat.model.ChatRoom.HistoryMessage;
import cn.yiidii.openapi.netty.wschat.model.ChatUser;
import cn.yiidii.openapi.netty.wschat.model.Type;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.yeauty.pojo.Session;

/**
 * session管理
 *
 * @author YiiDii Wang
 * @create 2021-08-13 18:07
 */
@Slf4j
public class SessionManager {

    private static Map<String, ChatUser> USER_SESSION = new ConcurrentHashMap<>();
    private static Map<String, ChatRoom> ROOM = new ConcurrentHashMap<>();

    static {
        // 默认大厅
        ChatRoom dftGroup = ChatRoom.builder()
                .id(ChatConstant.DFT_ROOM_ID)
                .name(ChatConstant.DFT_ROOM_NAME)
                .userList(new ArrayList<>())
                .historyMessageList(new ArrayList<>())
                .build();
        ROOM.put(dftGroup.getId(), dftGroup);
    }

    /**
     * 添加一个session
     *
     * @param user   user
     * @param roomId 房间ID
     */
    public static void add(ChatUser user, String roomId) {
        USER_SESSION.put(user.getId(), user);
        roomId = StrUtil.isBlank(roomId) || !ROOM.containsKey(roomId) ? ChatConstant.DFT_ROOM_ID : roomId;
        ChatRoom room = ROOM.get(roomId);
        List<String> userIdList = room.getUserList();
        List<Channel> channelList = USER_SESSION.values().stream().filter(e -> userIdList.contains(e.getId())).map(e -> e.getSession().channel()).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(channelList)) {
            // 广播
            ChatResponse resp = ChatResponse.builder().type(Type.POP_INFO).content(StrUtil.format("欢迎{}进入{}", user.getNickname(), room.getName())).build();
            DefaultChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
            channelGroup.addAll(channelList);
            channelGroup.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(resp)));
        }

        // 历史信息
        List<HistoryMessage> allHistoryMessageList = room.getHistoryMessageList();
        int size = allHistoryMessageList.size();
        ChatResponse historyMessage = ChatResponse.builder()
                .type(Type.MESSAGE)
                .nickname(user.getNickname())
                .build();
        // 可查看最近100条信息
        if (size <= 100) {
            historyMessage.setContent(JSON.toJSONString(allHistoryMessageList));
        } else {
            historyMessage.setContent(JSON.toJSONString(allHistoryMessageList.subList(size - 30, size)));
        }
        user.getSession().sendText(JSON.toJSONString(historyMessage));

        // 加入
        userIdList.add(user.getId());

        // 广播所有人在线人数
        ChatResponse onlineNumResp = ChatResponse.builder()
                .type(Type.ONLINE_NUM)
                .content(String.valueOf(USER_SESSION.size()))
                .build();
        final ChannelGroup channelGroup = getChannelGroupByUserIdList(new ArrayList<>(USER_SESSION.keySet()));
        channelGroup.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(onlineNumResp)));
    }

    /**
     * 移除sessin
     *
     * @param session session
     */
    public static void remove(Session session) {
        // 当前用户
        String userId = getUserIdBySession(session);
        final ChatUser currUser = USER_SESSION.get(userId);
        // 用户所在房间
        String roomId = getRoomIdBySession(session);
        if (StrUtil.isNotBlank(roomId)) {
            // 广播
            ChatRoom room = ROOM.get(roomId);
            List<String> roomUserList = room.getUserList();
            if (CollUtil.isNotEmpty(roomUserList)) {
                // 删除
                roomUserList.remove(userId);
                String msg = StrUtil.format("{}离开了{}", currUser.getNickname(), room.getName());
                ChannelGroup channelGroup = getChannelGroupByUserIdList(roomUserList);
                ChatResponse resp = ChatResponse.builder().type(Type.POP_INFO).content(msg).build();
                channelGroup.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(resp)));
            }
        }

        // 从USER_SESSION移除
        USER_SESSION.remove(userId);

        // 广播所有人在线人数
        ChatResponse onlineNumResp = ChatResponse.builder()
                .type(Type.ONLINE_NUM)
                .content(String.valueOf(USER_SESSION.size()))
                .build();
        final ChannelGroup channelGroup = getChannelGroupByUserIdList(new ArrayList<>(USER_SESSION.keySet()));
        channelGroup.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(onlineNumResp)));
    }

    /**
     * 发言
     *
     * @param session session
     * @param message 信息
     */
    public static void speakInRoom(Session session, String message) {
        // 当前用户
        String userId = getUserIdBySession(session);
        final ChatUser currUser = USER_SESSION.get(userId);
        // 用户所在房间
        String roomId = getRoomIdBySession(session);
        if (StrUtil.isNotBlank(roomId)) {
            ChatRoom room = ROOM.get(roomId);
            // 记录历史消息
            HistoryMessage msg = HistoryMessage.builder()
                    .mid(RandomUtil.randomStringUpper(12))
                    .nickname(currUser.getNickname())
                    .content(message)
                    .timestamp(LocalDateTime.now())
                    .build();
            room.getHistoryMessageList().add(msg);

            // 广播(除自己外)
            List<String> roomUserList = room.getUserList();
            List<String> triggerUserList = new ArrayList<>(roomUserList);
            triggerUserList.remove(userId);
            if (CollUtil.isNotEmpty(triggerUserList)) {
                ChannelGroup channelGroup = getChannelGroupByUserIdList(triggerUserList);
                final HistoryMessage currMessage = HistoryMessage.builder().mid(RandomUtil.randomStringUpper(12))
                        .timestamp(LocalDateTime.now())
                        .content(message)
                        .nickname(currUser.getNickname())
                        .build();
                ChatResponse resp = ChatResponse.builder()
                        .type(Type.MESSAGE)
                        .nickname(currUser.getNickname())
                        .content(JSONObject.toJSONString(Lists.newArrayList(currMessage)))
                        .build();
                channelGroup.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(resp)));
            }
        }
    }

    /**
     * 获取房间号在线用户名称集合. 默认所有
     *
     * @param roomId 房间ID
     * @return nameList
     */
    public static List<String> getNicknameList(String roomId) {
        if (StrUtil.isNotBlank(roomId) && ROOM.containsKey(roomId)) {
            return USER_SESSION.values().stream().filter(e -> ROOM.get(roomId).getUserList().contains(e.getId())).map(ChatUser::getNickname).distinct().collect(Collectors.toList());
        } else {
            return USER_SESSION.values().stream().map(ChatUser::getNickname).distinct().collect(Collectors.toList());
        }
    }

    /**
     * 通过session获取userId
     *
     * @param session session
     * @return userId
     */
    private static String getUserIdBySession(Session session) {
        // <channelId, userId>
        Map<String, String> map = USER_SESSION.values().stream().collect(Collectors.toMap(e -> e.getSession().id().asLongText(), e -> e.getId(), (e1, e2) -> e2));
        return map.get(session.id().asLongText());
    }

    /**
     * 通过session获取所在房间ID
     *
     * @param session session
     * @return roomId
     */
    private static String getRoomIdBySession(Session session) {
        // 通过channelId获取userId
        Map<String, String> map = USER_SESSION.values().stream().collect(Collectors.toMap(e -> e.getSession().id().asLongText(), e -> e.getId(), (e1, e2) -> e2));
        String currChannelId = session.id().asLongText();
        if (!map.containsKey(currChannelId)) {
            return null;
        }
        final String userId = map.get(currChannelId);

        final Iterator<String> keyIterator = ROOM.keySet().iterator();
        while (keyIterator.hasNext()) {
            String roomId = keyIterator.next();
            ChatRoom room = ROOM.get(roomId);
            List<String> roomUserList = room.getUserList();
            if (roomUserList.contains(userId)) {
                return roomId;
            }
        }
        return null;
    }

    /**
     * 通过userIdList构建ChannelGroup
     *
     * @param userIdList userIdList
     * @return ChannelGroup
     */
    private static ChannelGroup getChannelGroupByUserIdList(List<String> userIdList) {
        final List<Channel> channelList = USER_SESSION.values().stream().filter(e -> userIdList.contains(e.getId())).map(e -> e.getSession().channel()).collect(Collectors.toList());
        final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
        channelGroup.addAll(channelList);
        return channelGroup;
    }
}
