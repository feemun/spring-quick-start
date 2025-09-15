package cloud.catfish.redis.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 * 
 * @author catfish
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisUtils {

    private final RedisTemplate<String, Object> redisTemplate;

    // =============================通用操作=============================

    /**
     * 指定缓存失效时间
     * 
     * @param key  键
     * @param time 时间(秒)
     * @return 是否成功
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            log.error("设置过期时间失败", e);
            return false;
        }
    }

    /**
     * 根据key获取过期时间
     * 
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 判断key是否存在
     * 
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("判断key是否存在失败", e);
            return false;
        }
    }

    /**
     * 删除缓存
     * 
     * @param key 可以传一个值 或多个
     */
    @SuppressWarnings("unchecked")
    public void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete((Collection<String>) Arrays.asList(key));
            }
        }
    }

    /**
     * 批量删除key
     * 
     * @param keys 键集合
     * @return 删除的数量
     */
    public Long del(Collection<String> keys) {
        return redisTemplate.delete(keys);
    }

    /**
     * 获取匹配的key
     * 
     * @param pattern 模式
     * @return key集合
     */
    public Set<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    // =============================String操作=============================

    /**
     * 普通缓存获取
     * 
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 普通缓存放入
     * 
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            log.error("设置缓存失败", e);
            return false;
        }
    }

    /**
     * 普通缓存放入并设置时间
     * 
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            log.error("设置缓存失败", e);
            return false;
        }
    }

    /**
     * 递增
     * 
     * @param key   键
     * @param delta 要增加几(大于0)
     * @return 递增后的值
     */
    public long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     * 
     * @param key   键
     * @param delta 要减少几(小于0)
     * @return 递减后的值
     */
    public long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, -delta);
    }

    // =============================Hash操作=============================

    /**
     * HashGet
     * 
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public Object hget(String key, String item) {
        return redisTemplate.opsForHash().get(key, item);
    }

    /**
     * 获取hashKey对应的所有键值
     * 
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object, Object> hmget(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * HashSet
     * 
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public boolean hmset(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            log.error("设置Hash失败", e);
            return false;
        }
    }

    /**
     * HashSet 并设置时间
     * 
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public boolean hmset(String key, Map<String, Object> map, long time) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("设置Hash失败", e);
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     * 
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            log.error("设置Hash项失败", e);
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     * 
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value, long time) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("设置Hash项失败", e);
            return false;
        }
    }

    /**
     * 删除hash表中的值
     * 
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hdel(String key, Object... item) {
        redisTemplate.opsForHash().delete(key, item);
    }

    /**
     * 判断hash表中是否有该项的值
     * 
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean hHasKey(String key, String item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    // =============================Set操作=============================

    /**
     * 根据key获取Set中的所有值
     * 
     * @param key 键
     * @return Set集合
     */
    public Set<Object> sGet(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            log.error("获取Set失败", e);
            return null;
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     * 
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    public boolean sHasKey(String key, Object value) {
        try {
            return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, value));
        } catch (Exception e) {
            log.error("判断Set成员失败", e);
            return false;
        }
    }

    /**
     * 将数据放入set缓存
     * 
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSet(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            log.error("设置Set失败", e);
            return 0;
        }
    }

    /**
     * 将set数据放入缓存
     * 
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSetAndTime(String key, long time, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if (time > 0) {
                expire(key, time);
            }
            return count;
        } catch (Exception e) {
            log.error("设置Set失败", e);
            return 0;
        }
    }

    /**
     * 获取set缓存的长度
     * 
     * @param key 键
     * @return 长度
     */
    public long sGetSetSize(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            log.error("获取Set大小失败", e);
            return 0;
        }
    }

    /**
     * 移除值为value的
     * 
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public long setRemove(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().remove(key, values);
            return count;
        } catch (Exception e) {
            log.error("移除Set元素失败", e);
            return 0;
        }
    }

    // =============================List操作=============================

    /**
     * 获取list缓存的内容
     * 
     * @param key   键
     * @param start 开始
     * @param end   结束 0 到 -1代表所有值
     * @return List
     */
    public List<Object> lGet(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            log.error("获取List失败", e);
            return null;
        }
    }

    /**
     * 获取list缓存的长度
     * 
     * @param key 键
     * @return 长度
     */
    public long lGetListSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            log.error("获取List大小失败", e);
            return 0;
        }
    }

    /**
     * 通过索引 获取list中的值
     * 
     * @param key   键
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return 值
     */
    public Object lGetIndex(String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            log.error("获取List索引元素失败", e);
            return null;
        }
    }

    /**
     * 将list放入缓存
     * 
     * @param key   键
     * @param value 值
     * @return 是否成功
     */
    public boolean lSet(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            log.error("设置List失败", e);
            return false;
        }
    }

    /**
     * 将list放入缓存
     * 
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return 是否成功
     */
    public boolean lSet(String key, Object value, long time) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("设置List失败", e);
            return false;
        }
    }

    /**
     * 将list放入缓存
     * 
     * @param key   键
     * @param value 值
     * @return 是否成功
     */
    public boolean lSet(String key, List<Object> value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            log.error("设置List失败", e);
            return false;
        }
    }

    /**
     * 将list放入缓存
     * 
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return 是否成功
     */
    public boolean lSet(String key, List<Object> value, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("设置List失败", e);
            return false;
        }
    }

    /**
     * 根据索引修改list中的某条数据
     * 
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return 是否成功
     */
    public boolean lUpdateIndex(String key, long index, Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            log.error("更新List索引元素失败", e);
            return false;
        }
    }

    /**
     * 移除N个值为value
     * 
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public long lRemove(String key, long count, Object value) {
        try {
            Long remove = redisTemplate.opsForList().remove(key, count, value);
            return remove;
        } catch (Exception e) {
            log.error("移除List元素失败", e);
            return 0;
        }
    }

    // =============================Lua脚本操作=============================

    /**
     * 执行Lua脚本
     * 
     * @param script Lua脚本
     * @param keys   键列表
     * @param args   参数列表
     * @return 执行结果
     */
    public Object executeLuaScript(String script, List<String> keys, Object... args) {
        try {
            DefaultRedisScript<Object> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptText(script);
            redisScript.setResultType(Object.class);
            return redisTemplate.execute(redisScript, keys, args);
        } catch (Exception e) {
            log.error("执行Lua脚本失败", e);
            return null;
        }
    }

    /**
     * 原子性设置值和过期时间（使用Lua脚本）
     * 
     * @param key    键
     * @param value  值
     * @param expire 过期时间（秒）
     * @return 是否成功
     */
    public boolean setWithExpire(String key, Object value, long expire) {
        String script = "return redis.call('setex', KEYS[1], ARGV[1], ARGV[2])";
        List<String> keys = Collections.singletonList(key);
        Object result = executeLuaScript(script, keys, expire, value);
        return "OK".equals(result);
    }

    /**
     * 分布式限流（令牌桶算法）
     * 
     * @param key      限流键
     * @param capacity 桶容量
     * @param tokens   请求令牌数
     * @param window   时间窗口（秒）
     * @return 是否允许通过
     */
    public boolean rateLimiter(String key, long capacity, long tokens, long window) {
        String script = 
            "local key = KEYS[1] " +
            "local capacity = tonumber(ARGV[1]) " +
            "local tokens = tonumber(ARGV[2]) " +
            "local window = tonumber(ARGV[3]) " +
            "local current = redis.call('GET', key) " +
            "if current == false then " +
            "  redis.call('SETEX', key, window, capacity - tokens) " +
            "  return 1 " +
            "end " +
            "current = tonumber(current) " +
            "if current >= tokens then " +
            "  redis.call('DECRBY', key, tokens) " +
            "  return 1 " +
            "else " +
            "  return 0 " +
            "end";
        
        List<String> keys = Collections.singletonList(key);
        Object result = executeLuaScript(script, keys, capacity, tokens, window);
        return Long.valueOf(1).equals(result);
    }
}