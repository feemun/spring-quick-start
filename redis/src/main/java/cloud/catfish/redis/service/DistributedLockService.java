package cloud.catfish.redis.service;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 分布式锁服务接口
 * 
 * @author catfish
 * @since 1.0.0
 */
public interface DistributedLockService {

    /**
     * 尝试获取锁
     * 
     * @param lockKey 锁的键
     * @return 是否获取成功
     */
    boolean tryLock(String lockKey);

    /**
     * 尝试获取锁，指定等待时间和锁持有时间
     * 
     * @param lockKey   锁的键
     * @param waitTime  等待时间
     * @param leaseTime 锁持有时间
     * @param unit      时间单位
     * @return 是否获取成功
     */
    boolean tryLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit);

    /**
     * 释放锁
     * 
     * @param lockKey 锁的键
     */
    void unlock(String lockKey);

    /**
     * 强制释放锁
     * 
     * @param lockKey 锁的键
     * @return 是否释放成功
     */
    boolean forceUnlock(String lockKey);

    /**
     * 判断锁是否被持有
     * 
     * @param lockKey 锁的键
     * @return 是否被持有
     */
    boolean isLocked(String lockKey);

    /**
     * 判断锁是否被当前线程持有
     * 
     * @param lockKey 锁的键
     * @return 是否被当前线程持有
     */
    boolean isHeldByCurrentThread(String lockKey);

    /**
     * 获取锁的剩余持有时间
     * 
     * @param lockKey 锁的键
     * @return 剩余时间（毫秒）
     */
    long remainTimeToLive(String lockKey);

    /**
     * 执行带锁的操作（无返回值）
     * 
     * @param lockKey   锁的键
     * @param waitTime  等待时间
     * @param leaseTime 锁持有时间
     * @param unit      时间单位
     * @param action    要执行的操作
     * @return 是否执行成功
     */
    boolean executeWithLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit, Runnable action);

    /**
     * 执行带锁的操作（有返回值）
     * 
     * @param lockKey   锁的键
     * @param waitTime  等待时间
     * @param leaseTime 锁持有时间
     * @param unit      时间单位
     * @param supplier  要执行的操作
     * @param <T>       返回值类型
     * @return 操作结果
     */
    <T> T executeWithLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit, Supplier<T> supplier);

    /**
     * 执行带锁的操作（使用默认超时时间）
     * 
     * @param lockKey 锁的键
     * @param action  要执行的操作
     * @return 是否执行成功
     */
    boolean executeWithLock(String lockKey, Runnable action);

    /**
     * 执行带锁的操作（使用默认超时时间，有返回值）
     * 
     * @param lockKey  锁的键
     * @param supplier 要执行的操作
     * @param <T>      返回值类型
     * @return 操作结果
     */
    <T> T executeWithLock(String lockKey, Supplier<T> supplier);
}