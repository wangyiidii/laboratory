package cn.yiidii.openapi.netty.agent.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

/**
 * 服务端空闲检测
 *
 * @author YiiDii Wang
 * @create 2021-08-13 12:17
 */
@Slf4j
public class ServerIdleStateHandler extends IdleStateHandler {

    public ServerIdleStateHandler(long readerIdleTime, long writerIdleTime, long allIdleTime, TimeUnit unit) {
        super(readerIdleTime, writerIdleTime, allIdleTime, unit);
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        log.info("{} 秒内没有读取到数据,关闭连接", this.getReaderIdleTimeInMillis() / 1000);
        ctx.channel().close();
    }
}

