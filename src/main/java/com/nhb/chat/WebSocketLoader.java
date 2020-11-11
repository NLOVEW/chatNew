package com.nhb.chat;



import com.nhb.chat.websocket.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * @Auther: luck_nhb
 * @Date: 2019/1/6 13:05
 * @Version 1.0
 * @Description:  spring 环境启动后加载
 */
@Component
public class WebSocketLoader implements ApplicationListener<ContextRefreshedEvent> {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        try {
            if (contextRefreshedEvent.getApplicationContext().getParent() == null){
                logger.info(">>>>>>>>>>> starting webSocketServer port is 9797 <<<<<<<<<<");
                WebSocketServer.getInstance().start(9797);
            }
        } catch (Exception e) {
            logger.error("webSocketServer start happen error,the message:{}",e.getMessage());
        }
    }
}
