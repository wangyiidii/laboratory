package cn.yiidii.openapi.netty.agent.handler;

import cn.hutool.core.util.StrUtil;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author YiiDii Wang
 * @create 2021-08-13 14:32
 */
@Slf4j
@Component
public class SessionHandler extends ChannelDuplexHandler {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info(StrUtil.format("agent {} 已连接", ctx.channel().remoteAddress()));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info(StrUtil.format("agent {} 已断开", ctx.channel().remoteAddress()));
    }
}
