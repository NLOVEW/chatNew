package com.nhb.chat.websocket;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhb.chat.bean.WebSocketMessage;
import com.nhb.chat.utils.IPUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.time.LocalDateTime;

/**
 * 
 * @Description: 处理消息的handler
 * TextWebSocketFrame：
 * 	在netty中，是用于为websocket专门处理文本的对象，frame是消息的载体
 */
public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
	private Logger logger = LoggerFactory.getLogger(getClass());
	// 用于记录和管理所有客户端的channel
	public static ChannelGroup users =
			new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

	private static String[] userNames = {"色批","极其猥琐","信波哥得永生","宁缺毋滥","黑胖","大波牛"};

	private static ObjectMapper json = new ObjectMapper();
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
		// 获取客户端传输过来的消息
		String content = msg.text();
		Channel currentChannel = ctx.channel();
		//获取客户端发来的消息
		WebSocketMessage sendMessage = json.readValue(content, WebSocketMessage.class);
        InetSocketAddress inetAddress = (InetSocketAddress) currentChannel.remoteAddress();
        String ip = inetAddress.getAddress().getHostAddress();
        sendMessage.setType("message");
        sendMessage.setIp(ip);
        try {
			sendMessage.setCity(IPUtil.parseCityByIP(ip).getCity());
		}catch (Exception e){
        	logger.warn("IP:{} 获取城市名失败",ip);
			sendMessage.setCity("内网IP");
		}
		logger.info("receive the message：{}",sendMessage.toString());
		if (sendMessage.getContent().equals("")){
			UserChannelMap.put(sendMessage.getSender(),currentChannel);
			String city = null;
			try {
				city = IPUtil.parseCityByIP(getServerIP(currentChannel)).getCity();
			}catch (Exception e){
				logger.warn("IP:{} 获取城市名失败",ip);
			}
			users.writeAndFlush(new TextWebSocketFrame(json.writeValueAsString(new WebSocketMessage("服务器端", "全体成员", sendMessage.getSender()+"已上线", LocalDateTime.now().toString().replace("T", " "), null,city,"online"))));
		}
		//转发数据
		if (sendMessage.getReceiver().equals("全体成员")){
			users.writeAndFlush(new TextWebSocketFrame(json.writeValueAsString(sendMessage)));
		}
	}
	
	/**
	 * 当客户端连接服务端之后（打开连接）
	 * 获取客户端的channel，并且放到ChannelGroup中去进行管理
	 */
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		users.add(ctx.channel());
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.info("send active success message to client");
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		String channelId = ctx.channel().id().asShortText();
		logger.info("client was removed,channelId：" + channelId);
		// 当触发handlerRemoved，ChannelGroup会自动移除对应客户端的channel
		removeClientChannel(ctx);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.error("connect happen error,the message:{}",cause.getMessage());
		// 发生异常之后关闭连接（关闭channel），随后从ChannelGroup中移除
		removeClientChannel(ctx);
		ctx.channel().close();
	}

	private void removeClientChannel(ChannelHandlerContext ctx) throws JsonProcessingException {
		String userName = UserChannelMap.getUserName(ctx.channel());
		UserChannelMap.removeByChannel(ctx.channel());
		users.writeAndFlush(new TextWebSocketFrame(json.writeValueAsString(new WebSocketMessage("服务器端", "全体成员", userName+":由于异常,已下线", LocalDateTime.now().toString(),null,null,"online"))));
		users.remove(ctx.channel());
	}

	public static String getServerIP(Channel currentChannel){
		InetSocketAddress localAddress = (InetSocketAddress) currentChannel.localAddress();
		return localAddress.getAddress().getHostAddress();
	}

	@Test
	public void test() throws JsonProcessingException {
		String content = "{\"sender\":\"信波哥得永生\",\"receiver\":\"全体成员\",\"content\":\"测试数据\"}";
		WebSocketMessage webSocketMessage = json.readValue(content, WebSocketMessage.class);
		System.out.println(webSocketMessage.toString());
	}
}
