package cn.yiidii.openapi.netty.imtest.server;

import cn.yiidii.openapi.netty.imtest.handler.SessionHandler;
import cn.yiidii.openapi.netty.imtest.handler.TextWebSocketHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author YiiDii Wang
 * @create 2021-08-13 09:41
 */
@Component
@Slf4j
public class ImServerInitializer extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
                // 支持http协议, start
                // websocket协议本身是基于http协议的，所以这边也要使用http解编码器
                .addLast(new HttpServerCodec())
                // 以块的方式来写的处理器(添加对于读写大数据流的支持)
                .addLast(new ChunkedWriteHandler())
                // 对httpMessage进行聚合
                .addLast(new HttpObjectAggregator(8192))
                // 支持http协议, end

                // websocket 服务器处理的协议，用于给指定的客户端进行连接访问的路由地址
                .addLast(new WebSocketServerProtocolHandler("/ws", "WebSocket", true, 65536 * 10))
                // 服务端空闲检测
//                .addLast(new ServerIdleStateHandler())
                // 业务handler
                .addLast(new SessionHandler())
                .addLast(new TextWebSocketHandler());
    }
}
