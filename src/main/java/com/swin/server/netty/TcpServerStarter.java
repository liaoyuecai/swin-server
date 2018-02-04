package com.swin.server.netty;


import com.swin.manager.ConditionLock;
import com.swin.server.ServerThreadPool;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TcpServerStarter {
    private static final Logger logger = LoggerFactory.getLogger(TcpServerStarter.class);


    public void startServer(final Integer port, final ChannelInitializer channelInitializer) {
        EventLoopGroup boss = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() / 3);
        EventLoopGroup worker = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss, worker).
                channel(NioServerSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childHandler(channelInitializer);
        ServerThreadPool.execute(new Thread() {
            @Override
            public void run() {
                try {
                    ChannelFuture ch = bootstrap.bind(port).sync();
                    ConditionLock.getInstance().release("server_start", true);
                    ch.channel().closeFuture().sync();
                } catch (Exception e) {
                    logger.error("Server start error : port " + port, e);
                    ConditionLock.getInstance().release("server_start", false);
                } finally {
                    logger.error("Server has be closed");
                    boss.shutdownGracefully();
                    worker.shutdownGracefully();
                }
            }
        });


    }
}