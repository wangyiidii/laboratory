package cn.yiidii.openapi.netty.imtest.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 服务端空闲检测
 *
 * @author YiiDii Wang
 * @create 2021-08-13 12:17
 */
@Slf4j
public class ServerIdleStateHandler extends IdleStateHandler {

    /**
     * 设置空闲检测时间为 30s
     */
    private static final int READER_IDLE_TIME = 10;

    public ServerIdleStateHandler() {
        super(READER_IDLE_TIME, 0, 0, TimeUnit.SECONDS);
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        log.info("{} 秒内没有读取到数据,关闭连接", READER_IDLE_TIME);
        ctx.channel().close();
    }
}

