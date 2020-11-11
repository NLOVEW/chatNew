package com.nhb.chat.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhb.chat.bean.WebSocketMessage;
import com.nhb.chat.utils.IPUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

/**
 * @Description: 用于检测channel的心跳handler
 * 继承ChannelInboundHandlerAdapter，从而不需要实现channelRead0方法
 */
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private static ObjectMapper json = new ObjectMapper();

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        // 判断evt是否是IdleStateEvent（用于触发用户事件，包含 读空闲/写空闲/读写空闲 ）
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;// 强制类型转换
            if (event.state() == IdleState.READER_IDLE) {
                logger.warn("进入读空闲...");
            } else if (event.state() == IdleState.WRITER_IDLE) {
                logger.warn("进入写空闲...");
            } else if (event.state() == IdleState.ALL_IDLE) {
                Channel channel = ctx.channel();
                //主动向客户端发送提示
                String city = null;
                try {
                    city = IPUtil.parseCityByIP(WebSocketHandler.getServerIP(channel)).getCity();
                }catch (Exception e){
                    logger.warn("IP:{} 获取城市名失败",e.getMessage());
                }
                channel.writeAndFlush(new TextWebSocketFrame(json.writeValueAsString(new WebSocketMessage("服务器端", UserChannelMap.getUserName(channel), ">>>>>>>>>>>>>>ミ(ﾉ゜д゜)ﾉ打你哦   傻逼快回复<<<<<<<<<<<<<<", LocalDateTime.now().toString().replace("T", " "), null,city,"message"))));
            }
        }

    }

}
