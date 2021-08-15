package cn.yiidii.openapi.netty.imtest.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

/**
 * @author YiiDii Wang
 * @create 2021-08-13 14:25
 */
@Slf4j
public class TextWebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame textWebSocketFrame) throws Exception {
        log.info("TextWebSocketFrame: {}", textWebSocketFrame.text());
        ctx.channel().writeAndFlush(new TextWebSocketFrame("窝泥爹"));
    }
}
