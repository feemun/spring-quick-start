package cloud.catfish.redis.service.impl;

import cloud.catfish.redis.service.DistributedLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 分布式锁服务实现类
 * 
 * @author catfish
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DistributedLockServiceImpl implements DistributedLockService {

    private final RedissonClient redissonClient;

    private static final String LOCK_PREFIX = "distributed_lock:";
    private static final long DEFAULT_WAIT_TIME = 10L;
    private static final long DEFAULT_LEASE_TIME = 30L;
    private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.SECONDS;

    @Override
    public boolean tryLock(String lockKey) {
        return tryLock(lockKey, 0L, DEFAULT_LEASE_TIME, DEFAULT_TIME_UNIT);
    }

    @Override
    public boolean tryLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit) {
        String fullLockKey = LOCK_PREFIX + lockKey;
        RLock lock = redissonClient.getLock(fullLockKey);
        
        try {
            boolean acquired = lock.tryLock(waitTime, leaseTime, unit);
            if (acquired) {
                log.debug("成功获取分布式锁: {}", fullLockKey);
            } else {
                log.debug("获取分布式锁失败: {}", fullLockKey);
            }
            return acquired;
        } catch (InterruptedException e) {
            log.error("获取分布式锁被中断: {}", fullLockKey, e);
            Thread.currentThread().interrupt();
            return false;
        } catch (Exception e) {
            log.error("获取分布式锁异常: {}", fullLockKey, e);
            return false;
        }
    }

    @Override
    public void unlock(String lockKey) {
        String fullLockKey = LOCK_PREFIX + lockKey;
        RLock lock = redissonClient.getLock(fullLockKey);
        
        try {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.debug("成功释放分布式锁: {}", fullLockKey);
            } else {
                log.warn("尝试释放未持有的锁: {}", fullLockKey);
            }
        } catch (Exception e) {
            log.error("释放分布式锁异常: {}", fullLockKey, e);
        }
    }

    @Override
    public boolean forceUnlock(String lockKey) {
        String fullLockKey = LOCK_PREFIX + lockKey;
        RLock lock = redissonClient.getLock(fullLockKey);
        
        try {
            boolean result = lock.forceUnlock();
            if (result) {
                log.debug("成功强制释放分布式锁: {}", fullLockKey);
            } else {
                log.debug("强制释放分布式锁失败（锁可能不存在）: {}", fullLockKey);
            }
            return result;
        } catch (Exception e) {
            log.error("强制释放分布式锁异常: {}", fullLockKey, e);
            return false;
        }
    }

    @Override
    public boolean isLocked(String lockKey) {
        String fullLockKey = LOCK_PREFIX + lockKey;
        RLock lock = redissonClient.getLock(fullLockKey);
        
        try {
            return lock.isLocked();
        } catch (Exception e) {
            log.error("检查锁状态异常: {}", fullLockKey, e);
            return false;
        }
    }

    @Override
    public boolean isHeldByCurrentThread(String lockKey) {
        String fullLockKey = LOCK_PREFIX + lockKey;
        RLock lock = redissonClient.getLock(fullLockKey);
        
        try {
            return lock.isHeldByCurrentThread();
        } catch (Exception e) {
            log.error("检查锁持有状态异常: {}", fullLockKey, e);
            return false;
        }
    }

    @Override
    public long remainTimeToLive(String lockKey) {
        String fullLockKey = LOCK_PREFIX + lockKey;
        RLock lock = redissonClient.getLock(fullLockKey);
        
        try {
            return lock.remainTimeToLive();
        } catch (Exception e) {
            log.error("获取锁剩余时间异常: {}", fullLockKey, e);
            return -1L;
        }
    }

    @Override
    public boolean executeWithLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit, Runnable action) {
        if (tryLock(lockKey, waitTime, leaseTime, unit)) {
            try {
                action.run();
                return true;
            } catch (Exception e) {
                log.error("执行带锁操作异常: {}", lockKey, e);
                throw e;
            } finally {
                unlock(lockKey);
            }
        } else {
            log.warn("获取锁失败，无法执行操作: {}", lockKey);
            return false;
        }
    }

    @Override
    public <T> T executeWithLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit, Supplier<T> supplier) {
        if (tryLock(lockKey, waitTime, leaseTime, unit)) {
            try {
                return supplier.get();
            } catch (Exception e) {
                log.error("执行带锁操作异常: {}", lockKey, e);
                throw e;
            } finally {
                unlock(lockKey);
            }
        } else {
            log.warn("获取锁失败，无法执行操作: {}", lockKey);
            return null;
        }
    }

    @Override
    public boolean executeWithLock(String lockKey, Runnable action) {
        return executeWithLock(lockKey, DEFAULT_WAIT_TIME, DEFAULT_LEASE_TIME, DEFAULT_TIME_UNIT, action);
    }

    @Override
    public <T> T executeWithLock(String lockKey, Supplier<T> supplier) {
        return executeWithLock(lockKey, DEFAULT_WAIT_TIME, DEFAULT_LEASE_TIME, DEFAULT_TIME_UNIT, supplier);
    }
}