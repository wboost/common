package top.wboost.common.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import top.wboost.common.netty.protocol.NettyProtocol;

import java.net.InetSocketAddress;

import static top.wboost.common.netty.protocol.NettyConstant.MAX_BYTES;

public class Client {

    public static void main(String[] args) {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerGroup).handler(new LoggingHandler(LogLevel.INFO)).channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            NettyProtocol.addHandler(socketChannel);
                            socketChannel.pipeline().addLast(new ClientHandler());
                        }
                    });
            ChannelFuture future = bootstrap.connect(new InetSocketAddress("127.0.01", 8765)).sync();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < (MAX_BYTES - 8); i++) {
                sb.append('0');
            }
            /*for (int i = 0; i < (1); i++) {
                sb.append('0');
            }*/
            future.channel().writeAndFlush(new NettyProtocol((sb.toString()).getBytes()));
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}