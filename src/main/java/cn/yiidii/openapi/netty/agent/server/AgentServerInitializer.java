package cn.yiidii.openapi.netty.agent.server;

import cn.yiidii.openapi.netty.agent.handler.ServerIdleStateHandler;
import cn.yiidii.openapi.netty.agent.handler.SessionHandler;
import cn.yiidii.openapi.netty.agent.handler.StringHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author YiiDii Wang
 * @create 2021-08-13 09:41
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AgentServerInitializer extends ChannelInitializer<Channel> {

    private final AgentServerConfig agentServerConfig;
    private final SessionHandler sessionHandler;
    private final StringHandler stringHandler;

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
                .addLast(new StringDecoder(Charset.forName("gbk")))
                .addLast(new StringEncoder(Charset.forName("gbk")))
                // 服务端空闲检测
                .addLast(new ServerIdleStateHandler(agentServerConfig.getIdleTime(), 0, 0, TimeUnit.SECONDS))
                // 业务handler
                .addLast(sessionHandler)
                .addLast(stringHandler);
    }
}
