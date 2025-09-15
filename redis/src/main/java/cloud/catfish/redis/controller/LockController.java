package cloud.catfish.redis.controller;

import cloud.catfish.redis.service.DistributedLockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁管理控制器
 * 
 * @author catfish
 * @since 1.0.0
 */
@Tag(name = "分布式锁管理", description = "Redis分布式锁操作接口")
@RestController
@RequestMapping("/api/lock")
@RequiredArgsConstructor
public class LockController {

    private final DistributedLockService distributedLockService;

    @Operation(summary = "尝试获取锁", description = "尝试获取分布式锁")
    @PostMapping("/try-lock")
    public ResponseEntity<Boolean> tryLock(
            @Parameter(description = "锁名称") @RequestParam String lockName,
            @Parameter(description = "等待时间（秒）") @RequestParam(defaultValue = "10") long waitTime,
            @Parameter(description = "锁定时间（秒）") @RequestParam(defaultValue = "30") long leaseTime) {
        try {
            boolean locked = distributedLockService.tryLock(lockName, waitTime, leaseTime, TimeUnit.SECONDS);
            return ResponseEntity.ok(locked);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.ok(false);
        }
    }

    @Operation(summary = "释放锁", description = "释放指定的分布式锁")
    @PostMapping("/unlock")
    public ResponseEntity<String> unlock(
            @Parameter(description = "锁名称") @RequestParam String lockName) {
        distributedLockService.unlock(lockName);
        return ResponseEntity.ok("锁已释放");
    }

    @Operation(summary = "检查锁状态", description = "检查指定锁是否被锁定")
    @GetMapping("/is-locked")
    public ResponseEntity<Boolean> isLocked(
            @Parameter(description = "锁名称") @RequestParam String lockName) {
        boolean locked = distributedLockService.isLocked(lockName);
        return ResponseEntity.ok(locked);
    }

    @Operation(summary = "检查锁持有状态", description = "检查当前线程是否持有指定锁")
    @GetMapping("/is-held-by-current-thread")
    public ResponseEntity<Boolean> isHeldByCurrentThread(
            @Parameter(description = "锁名称") @RequestParam String lockName) {
        boolean held = distributedLockService.isHeldByCurrentThread(lockName);
        return ResponseEntity.ok(held);
    }

    @Operation(summary = "获取锁持有数量", description = "获取当前线程持有指定锁的数量")
    @GetMapping("/hold-count")
    public ResponseEntity<Integer> getHoldCount(
            @Parameter(description = "锁名称") @RequestParam String lockName) {
        int count = distributedLockService.getHoldCount(lockName);
        return ResponseEntity.ok(count);
    }

    @Operation(summary = "强制释放锁", description = "强制释放指定的分布式锁")
    @PostMapping("/force-unlock")
    public ResponseEntity<String> forceUnlock(
            @Parameter(description = "锁名称") @RequestParam String lockName) {
        boolean result = distributedLockService.forceUnlock(lockName);
        return ResponseEntity.ok(result ? "强制释放成功" : "强制释放失败");
    }

    @Operation(summary = "执行带锁操作", description = "在锁保护下执行指定操作")
    @PostMapping("/execute-with-lock")
    public ResponseEntity<String> executeWithLock(
            @Parameter(description = "锁名称") @RequestParam String lockName,
            @Parameter(description = "等待时间（秒）") @RequestParam(defaultValue = "10") long waitTime,
            @Parameter(description = "锁定时间（秒）") @RequestParam(defaultValue = "30") long leaseTime,
            @Parameter(description = "操作描述") @RequestParam String operation) {
        try {
            String result = distributedLockService.executeWithLock(lockName, waitTime, leaseTime, TimeUnit.SECONDS, () -> {
                // 模拟业务操作
                try {
                    Thread.sleep(1000); // 模拟耗时操作
                    return "执行操作: " + operation + " 成功";
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return "操作被中断";
                }
            });
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.ok("操作执行失败: " + e.getMessage());
        }
    }
}