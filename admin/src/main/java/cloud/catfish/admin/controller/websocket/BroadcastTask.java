package cloud.catfish.admin.controller.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class BroadcastTask {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Scheduled(fixedRate = 5000)
    public void send() {
        messagingTemplate.convertAndSend("/topic/messages", "hello" + new Date());
    }
}
