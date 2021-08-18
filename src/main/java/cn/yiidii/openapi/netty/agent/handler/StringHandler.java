package cn.yiidii.openapi.netty.agent.handler;

import cn.hutool.core.util.StrUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author YiiDii Wang
 * @create 2021-08-16 10:03
 */
@Slf4j
@Component
public class StringHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String message) throws Exception {
        log.info(StrUtil.format("agent({}): {} ", ctx.channel().remoteAddress(), message));
        ctx.channel().writeAndFlush("窝泥爹");
    }
}
