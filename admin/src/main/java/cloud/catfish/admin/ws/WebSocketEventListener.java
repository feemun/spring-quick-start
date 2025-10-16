package cloud.catfish.admin.ws;

import cloud.catfish.admin.cron.MyTaskScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

/**
 * Created by rajeevkumarsingh on 25/07/17.
 */
@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);
    @Autowired
    private MyTaskScheduler myTaskScheduler;


    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        String username = (String) event.getUser().getName();
        logger.info("{} connected", username);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        String username = (String) event.getUser().getName();
        if (username != null) {
            logger.info("{} Disconnected.", username);
            myTaskScheduler.cancelTask(username);
        }
    }

    @EventListener
    public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = headerAccessor.getDestination();
        String username = event.getUser().getName();

        logger.info("New subscription - user: {}, destination: {}", username, destination);
    }

    @EventListener
    public void handleSessionUnsubscribeEvent(SessionUnsubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = headerAccessor.getDestination();
        String username = event.getUser().getName();

        logger.info("Cancel subscription - user: {}, destination: {}", username, destination);
    }

    @MessageExceptionHandler
    public void handleExceptions(Throwable t) {
        t.printStackTrace();
        logger.error("Error handling message: " + t.getMessage());
    }

}
