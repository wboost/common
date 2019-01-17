package top.wboost.common.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import top.wboost.common.netty.protocol.NettyProtocol;

/**
 * @Auther: jwsun
 * @Date: 2019/1/11 22:14
 */
public abstract class StringHandler<T extends NettyProtocol> extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        T buf = (T) msg;
        String request = new String(buf.getContent(), "utf-8");
        channelReadInternal(ctx, request);
    }

    public abstract void channelReadInternal(ChannelHandlerContext ctx,String read);

}
