package cloud.catfish.admin.controller;

import cloud.catfish.admin.cron.MyTaskScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Controller
public class ScheduledWsController {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private MyTaskScheduler myTaskScheduler;

    @MessageMapping("/search")
    public void receiveSearchCriteria(String searchCriteria) {
        // 根据搜索条件创建定时任务
        createScheduledTask("username", searchCriteria, 5000L);
    }

    private void createScheduledTask(String username, String searchCriteria, Long delay) {
        // 实现创建定时任务的逻辑
        myTaskScheduler.scheduleTask(username, () -> {
            // 查询数据并推送到用户主题
            String message = fetchDataBasedOnSearchCriteria(searchCriteria);
            messagingTemplate.convertAndSendToUser(username, "/queue/search", message);
        }, delay); // 每5秒执行一次任务
    }

    private String fetchDataBasedOnSearchCriteria(String searchCriteria) {
        // 根据搜索条件查询数据的逻辑
        return "Sample data based on: " + searchCriteria;
    }

    @MessageMapping("/send")
    public void handleMessage(@Payload ChatMessage chatMessage,
                              SimpMessageHeaderAccessor headerAccessor) {
        // 处理接收到的消息
        String name = headerAccessor.getUser().getName();
        System.out.println("Received message: " + chatMessage);

        // 构建响应消息
        messagingTemplate.convertAndSendToUser(name, "/queue/echo", "echo -> " + chatMessage.getContent());
    }
}
