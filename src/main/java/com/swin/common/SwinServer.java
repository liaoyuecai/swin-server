package com.swin.common;

import com.swin.server.netty.TcpServerStarter;
import com.swin.server.netty.factory.MessageDecoder;
import com.swin.server.netty.factory.MessageEncoder;
import com.swin.server.netty.factory.MessageHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

public class SwinServer {

    public static void init(Integer port) {
        ChannelInitializer channelInitializer = new ChannelInitializer<SocketChannel>() {
            public void initChannel(SocketChannel channel)
                    throws Exception {
                channel.pipeline().addLast(new LengthFieldBasedFrameDecoder(16 * 1024, 0, 4, 0, 4))
                        .addLast(new MessageDecoder())
                        .addLast("idleStateHandler", new IdleStateHandler(300 * 1000, 300 * 1000, 300 * 1000))
                        .addLast(new MessageEncoder())
                        .addLast(new MessageHandler());
            }
        };
        TcpServerStarter starter = new TcpServerStarter();
        starter.startServer(port,channelInitializer);
    }


}
