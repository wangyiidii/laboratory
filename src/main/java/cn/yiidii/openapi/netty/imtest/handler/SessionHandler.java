package cn.yiidii.openapi.netty.imtest.handler;

import cn.hutool.core.util.StrUtil;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author YiiDii Wang
 * @create 2021-08-13 14:32
 */
@Slf4j
public class SessionHandler extends ChannelDuplexHandler {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info(StrUtil.format("{}加入im", ctx.channel().remoteAddress()));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info(StrUtil.format("{}离开了im", ctx.channel().remoteAddress()));
    }
}
