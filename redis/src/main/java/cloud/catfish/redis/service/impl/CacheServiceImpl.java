package cloud.catfish.redis.service.impl;

import cloud.catfish.redis.service.CacheService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 通用缓存服务实现类
 * 
 * @author catfish
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CacheServiceImpl implements CacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    // =============================String操作=============================

    @Override
    public void set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            log.error("设置缓存失败，key: {}", key, e);
            throw new RuntimeException("设置缓存失败", e);
        }
    }

    @Override
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, unit);
        } catch (Exception e) {
            log.error("设置缓存失败，key: {}, timeout: {}, unit: {}", key, timeout, unit, e);
            throw new RuntimeException("设置缓存失败", e);
        }
    }

    @Override
    public void set(String key, Object value, Duration duration) {
        try {
            redisTemplate.opsForValue().set(key, value, duration);
        } catch (Exception e) {
            log.error("设置缓存失败，key: {}, duration: {}", key, duration, e);
            throw new RuntimeException("设置缓存失败", e);
        }
    }

    @Override
    public Object get(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("获取缓存失败，key: {}", key, e);
            return null;
        }
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                return null;
            }
            return objectMapper.convertValue(value, clazz);
        } catch (Exception e) {
            log.error("获取缓存失败，key: {}, class: {}", key, clazz.getName(), e);
            return null;
        }
    }

    @Override
    public Boolean delete(String key) {
        try {
            return redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("删除缓存失败，key: {}", key, e);
            return false;
        }
    }

    @Override
    public Long delete(Collection<String> keys) {
        try {
            return redisTemplate.delete(keys);
        } catch (Exception e) {
            log.error("批量删除缓存失败，keys: {}", keys, e);
            return 0L;
        }
    }

    @Override
    public Boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error("判断键是否存在失败，key: {}", key, e);
            return false;
        }
    }

    @Override
    public Boolean expire(String key, long timeout, TimeUnit unit) {
        try {
            return redisTemplate.expire(key, timeout, unit);
        } catch (Exception e) {
            log.error("设置过期时间失败，key: {}, timeout: {}, unit: {}", key, timeout, unit, e);
            return false;
        }
    }

    @Override
    public Long getExpire(String key) {
        try {
            return redisTemplate.getExpire(key);
        } catch (Exception e) {
            log.error("获取过期时间失败，key: {}", key, e);
            return -1L;
        }
    }

    // =============================Hash操作=============================

    @Override
    public void hSet(String key, String hashKey, Object value) {
        try {
            redisTemplate.opsForHash().put(key, hashKey, value);
        } catch (Exception e) {
            log.error("设置Hash值失败，key: {}, hashKey: {}", key, hashKey, e);
            throw new RuntimeException("设置Hash值失败", e);
        }
    }

    @Override
    public Object hGet(String key, String hashKey) {
        try {
            return redisTemplate.opsForHash().get(key, hashKey);
        } catch (Exception e) {
            log.error("获取Hash值失败，key: {}, hashKey: {}", key, hashKey, e);
            return null;
        }
    }

    @Override
    public <T> T hGet(String key, String hashKey, Class<T> clazz) {
        try {
            Object value = redisTemplate.opsForHash().get(key, hashKey);
            if (value == null) {
                return null;
            }
            return objectMapper.convertValue(value, clazz);
        } catch (Exception e) {
            log.error("获取Hash值失败，key: {}, hashKey: {}, class: {}", key, hashKey, clazz.getName(), e);
            return null;
        }
    }

    @Override
    public Map<Object, Object> hGetAll(String key) {
        try {
            return redisTemplate.opsForHash().entries(key);
        } catch (Exception e) {
            log.error("获取Hash所有键值对失败，key: {}", key, e);
            return null;
        }
    }

    @Override
    public void hSetAll(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
        } catch (Exception e) {
            log.error("批量设置Hash值失败，key: {}", key, e);
            throw new RuntimeException("批量设置Hash值失败", e);
        }
    }

    @Override
    public Long hDelete(String key, Object... hashKeys) {
        try {
            return redisTemplate.opsForHash().delete(key, hashKeys);
        } catch (Exception e) {
            log.error("删除Hash值失败，key: {}", key, e);
            return 0L;
        }
    }

    @Override
    public Boolean hHasKey(String key, String hashKey) {
        try {
            return redisTemplate.opsForHash().hasKey(key, hashKey);
        } catch (Exception e) {
            log.error("判断Hash键是否存在失败，key: {}, hashKey: {}", key, hashKey, e);
            return false;
        }
    }

    // =============================List操作=============================

    @Override
    public Long lLeftPush(String key, Object value) {
        try {
            return redisTemplate.opsForList().leftPush(key, value);
        } catch (Exception e) {
            log.error("从左侧推入List失败，key: {}", key, e);
            return 0L;
        }
    }

    @Override
    public Long lRightPush(String key, Object value) {
        try {
            return redisTemplate.opsForList().rightPush(key, value);
        } catch (Exception e) {
            log.error("从右侧推入List失败，key: {}", key, e);
            return 0L;
        }
    }

    @Override
    public Object lLeftPop(String key) {
        try {
            return redisTemplate.opsForList().leftPop(key);
        } catch (Exception e) {
            log.error("从左侧弹出List失败，key: {}", key, e);
            return null;
        }
    }

    @Override
    public Object lRightPop(String key) {
        try {
            return redisTemplate.opsForList().rightPop(key);
        } catch (Exception e) {
            log.error("从右侧弹出List失败，key: {}", key, e);
            return null;
        }
    }

    @Override
    public List<Object> lRange(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            log.error("获取List指定范围的元素失败，key: {}, start: {}, end: {}", key, start, end, e);
            return null;
        }
    }

    @Override
    public Long lSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            log.error("获取List长度失败，key: {}", key, e);
            return 0L;
        }
    }

    // =============================Set操作=============================

    @Override
    public Long sAdd(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            log.error("添加Set元素失败，key: {}", key, e);
            return 0L;
        }
    }

    @Override
    public Set<Object> sMembers(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            log.error("获取Set所有元素失败，key: {}", key, e);
            return null;
        }
    }

    @Override
    public Boolean sIsMember(String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            log.error("判断Set是否包含元素失败，key: {}", key, e);
            return false;
        }
    }

    @Override
    public Long sSize(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            log.error("获取Set大小失败，key: {}", key, e);
            return 0L;
        }
    }

    @Override
    public Long sRemove(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().remove(key, values);
        } catch (Exception e) {
            log.error("移除Set元素失败，key: {}", key, e);
            return 0L;
        }
    }

    // =============================ZSet操作=============================

    @Override
    public Boolean zAdd(String key, Object value, double score) {
        try {
            return redisTemplate.opsForZSet().add(key, value, score);
        } catch (Exception e) {
            log.error("添加ZSet元素失败，key: {}", key, e);
            return false;
        }
    }

    @Override
    public Set<Object> zRange(String key, long start, long end) {
        try {
            return redisTemplate.opsForZSet().range(key, start, end);
        } catch (Exception e) {
            log.error("获取ZSet指定范围的元素失败，key: {}, start: {}, end: {}", key, start, end, e);
            return null;
        }
    }

    @Override
    public Set<Object> zRangeByScore(String key, double min, double max) {
        try {
            return redisTemplate.opsForZSet().rangeByScore(key, min, max);
        } catch (Exception e) {
            log.error("获取ZSet指定分数范围的元素失败，key: {}, min: {}, max: {}", key, min, max, e);
            return null;
        }
    }

    @Override
    public Long zSize(String key) {
        try {
            return redisTemplate.opsForZSet().size(key);
        } catch (Exception e) {
            log.error("获取ZSet大小失败，key: {}", key, e);
            return 0L;
        }
    }

    @Override
    public Long zRemove(String key, Object... values) {
        try {
            return redisTemplate.opsForZSet().remove(key, values);
        } catch (Exception e) {
            log.error("移除ZSet元素失败，key: {}", key, e);
            return 0L;
        }
    }
}