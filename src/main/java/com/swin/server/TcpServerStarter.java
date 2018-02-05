package com.swin.server;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TcpServerStarter {
    private static final Logger logger = LoggerFactory.getLogger(TcpServerStarter.class);


    void startServer(final Integer port, final ChannelInitializer channelInitializer) {
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
                    ConditionLock.release("server_start", true);
                    ch.channel().closeFuture().sync();
                } catch (Exception e) {
                    logger.error("Server start error : port " + port, e);
                    ConditionLock.release("server_start", false);
                } finally {
                    logger.error("Server has be closed");
                    boss.shutdownGracefully();
                    worker.shutdownGracefully();
                }
            }
        });


    }
}