package com.swin.server;

import com.swin.exception.ServerStartException;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by LiaoYuecai on 2017/9/29.
 */
public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void init() throws Exception {
        MapDBFactory.init();
        startServer(ParamsLoader.getPort());
        boolean flag = (boolean) ConditionLock.await("server_start", 30000);
        if (flag) {
            logger.info("Server has been started");
        } else {
            throw new ServerStartException("Server start failed");
        }
    }

    static void startServer(Integer port) {
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
        starter.startServer(port, channelInitializer);
    }


}
