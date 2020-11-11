package com.nhb.chat.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author luck_nhb
 * @version 1.0
 * @description
 * @date 2020/11/10 16:59
 */
@Component
public class WebSocketServer {
    private static Logger logger = LoggerFactory.getLogger(WebSocketServer.class);



    private EventLoopGroup bossGroup;
    private EventLoopGroup workGroup;
    private ServerBootstrap serverBootstrap;
    private ChannelFuture channelFuture;
    private static WebSocketServer webSocketServer = new WebSocketServer();

    private WebSocketServer(){
        bossGroup = new NioEventLoopGroup(1);
        workGroup = new NioEventLoopGroup();
        serverBootstrap = new ServerBootstrap()
                .group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new WebSocketServerInitializer());
    }
    public static WebSocketServer getInstance(){
        return webSocketServer;
    }

    public void start(int port){
        this.channelFuture = this.serverBootstrap.bind(port);
        logger.info("-----------------webSocketServer start success-----------------");
    }
}
