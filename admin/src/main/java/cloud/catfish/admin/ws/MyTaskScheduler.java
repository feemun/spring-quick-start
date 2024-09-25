package cloud.catfish.admin.ws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MyTaskScheduler {

    private final ConcurrentHashMap<String, ScheduledFuture<?>> tasks = new ConcurrentHashMap<>();

    @Autowired
    private ThreadPoolTaskScheduler scheduler;

    @Autowired
    public MyTaskScheduler(ThreadPoolTaskScheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void scheduleTask(String username, Runnable task, long delay) {
        ScheduledFuture<?> scheduledTask = scheduler.scheduleWithFixedDelay(task, delay);
        tasks.put(username, scheduledTask);
    }

    public void cancelTask(String username) {
        ScheduledFuture<?> scheduledTask = tasks.get(username);
        if (scheduledTask != null) {
            scheduledTask.cancel(false);
            tasks.remove(username);
        }
    }
}
