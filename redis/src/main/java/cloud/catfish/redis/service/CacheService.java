package cloud.catfish.redis.service;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 通用缓存服务接口
 * 
 * @author catfish
 * @since 1.0.0
 */
public interface CacheService {

    // =============================String操作=============================

    /**
     * 设置缓存
     * 
     * @param key   键
     * @param value 值
     */
    void set(String key, Object value);

    /**
     * 设置缓存并指定过期时间
     * 
     * @param key     键
     * @param value   值
     * @param timeout 过期时间
     * @param unit    时间单位
     */
    void set(String key, Object value, long timeout, TimeUnit unit);

    /**
     * 设置缓存并指定过期时间
     * 
     * @param key      键
     * @param value    值
     * @param duration 过期时间
     */
    void set(String key, Object value, Duration duration);

    /**
     * 获取缓存
     * 
     * @param key 键
     * @return 值
     */
    Object get(String key);

    /**
     * 获取缓存并转换为指定类型
     * 
     * @param key   键
     * @param clazz 目标类型
     * @param <T>   泛型
     * @return 值
     */
    <T> T get(String key, Class<T> clazz);

    /**
     * 删除缓存
     * 
     * @param key 键
     * @return 是否删除成功
     */
    Boolean delete(String key);

    /**
     * 批量删除缓存
     * 
     * @param keys 键集合
     * @return 删除的数量
     */
    Long delete(Collection<String> keys);

    /**
     * 判断键是否存在
     * 
     * @param key 键
     * @return 是否存在
     */
    Boolean hasKey(String key);

    /**
     * 设置过期时间
     * 
     * @param key     键
     * @param timeout 过期时间
     * @param unit    时间单位
     * @return 是否设置成功
     */
    Boolean expire(String key, long timeout, TimeUnit unit);

    /**
     * 获取过期时间
     * 
     * @param key 键
     * @return 过期时间（秒）
     */
    Long getExpire(String key);

    // =============================Hash操作=============================

    /**
     * 设置Hash值
     * 
     * @param key     键
     * @param hashKey Hash键
     * @param value   值
     */
    void hSet(String key, String hashKey, Object value);

    /**
     * 获取Hash值
     * 
     * @param key     键
     * @param hashKey Hash键
     * @return 值
     */
    Object hGet(String key, String hashKey);

    /**
     * 获取Hash值并转换为指定类型
     * 
     * @param key     键
     * @param hashKey Hash键
     * @param clazz   目标类型
     * @param <T>     泛型
     * @return 值
     */
    <T> T hGet(String key, String hashKey, Class<T> clazz);

    /**
     * 获取Hash所有键值对
     * 
     * @param key 键
     * @return 键值对Map
     */
    Map<Object, Object> hGetAll(String key);

    /**
     * 批量设置Hash值
     * 
     * @param key 键
     * @param map 键值对Map
     */
    void hSetAll(String key, Map<String, Object> map);

    /**
     * 删除Hash值
     * 
     * @param key      键
     * @param hashKeys Hash键数组
     * @return 删除的数量
     */
    Long hDelete(String key, Object... hashKeys);

    /**
     * 判断Hash键是否存在
     * 
     * @param key     键
     * @param hashKey Hash键
     * @return 是否存在
     */
    Boolean hHasKey(String key, String hashKey);

    // =============================List操作=============================

    /**
     * 从左侧推入List
     * 
     * @param key   键
     * @param value 值
     * @return List长度
     */
    Long lLeftPush(String key, Object value);

    /**
     * 从右侧推入List
     * 
     * @param key   键
     * @param value 值
     * @return List长度
     */
    Long lRightPush(String key, Object value);

    /**
     * 从左侧弹出List
     * 
     * @param key 键
     * @return 值
     */
    Object lLeftPop(String key);

    /**
     * 从右侧弹出List
     * 
     * @param key 键
     * @return 值
     */
    Object lRightPop(String key);

    /**
     * 获取List指定范围的元素
     * 
     * @param key   键
     * @param start 开始位置
     * @param end   结束位置
     * @return 元素列表
     */
    List<Object> lRange(String key, long start, long end);

    /**
     * 获取List长度
     * 
     * @param key 键
     * @return 长度
     */
    Long lSize(String key);

    // =============================Set操作=============================

    /**
     * 添加Set元素
     * 
     * @param key    键
     * @param values 值数组
     * @return 添加的数量
     */
    Long sAdd(String key, Object... values);

    /**
     * 获取Set所有元素
     * 
     * @param key 键
     * @return 元素集合
     */
    Set<Object> sMembers(String key);

    /**
     * 判断Set是否包含元素
     * 
     * @param key   键
     * @param value 值
     * @return 是否包含
     */
    Boolean sIsMember(String key, Object value);

    /**
     * 获取Set大小
     * 
     * @param key 键
     * @return 大小
     */
    Long sSize(String key);

    /**
     * 移除Set元素
     * 
     * @param key    键
     * @param values 值数组
     * @return 移除的数量
     */
    Long sRemove(String key, Object... values);

    // =============================ZSet操作=============================

    /**
     * 添加ZSet元素
     * 
     * @param key   键
     * @param value 值
     * @param score 分数
     * @return 是否添加成功
     */
    Boolean zAdd(String key, Object value, double score);

    /**
     * 获取ZSet指定范围的元素（按分数升序）
     * 
     * @param key   键
     * @param start 开始位置
     * @param end   结束位置
     * @return 元素集合
     */
    Set<Object> zRange(String key, long start, long end);

    /**
     * 获取ZSet指定分数范围的元素
     * 
     * @param key 键
     * @param min 最小分数
     * @param max 最大分数
     * @return 元素集合
     */
    Set<Object> zRangeByScore(String key, double min, double max);

    /**
     * 获取ZSet大小
     * 
     * @param key 键
     * @return 大小
     */
    Long zSize(String key);

    /**
     * 移除ZSet元素
     * 
     * @param key    键
     * @param values 值数组
     * @return 移除的数量
     */
    Long zRemove(String key, Object... values);
}