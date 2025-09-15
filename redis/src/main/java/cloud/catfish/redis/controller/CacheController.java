package cloud.catfish.redis.controller;

import cloud.catfish.redis.service.CacheService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 缓存管理控制器
 * 
 * @author catfish
 * @since 1.0.0
 */
@Tag(name = "缓存管理", description = "Redis缓存操作接口")
@RestController
@RequestMapping("/api/cache")
@RequiredArgsConstructor
public class CacheController {

    private final CacheService cacheService;

    @Operation(summary = "设置缓存", description = "设置键值对缓存")
    @PostMapping("/set")
    public ResponseEntity<String> set(
            @Parameter(description = "缓存键") @RequestParam String key,
            @Parameter(description = "缓存值") @RequestBody Object value) {
        cacheService.set(key, value);
        return ResponseEntity.ok("设置成功");
    }

    @Operation(summary = "设置缓存并指定过期时间", description = "设置键值对缓存并指定过期时间")
    @PostMapping("/set-with-expire")
    public ResponseEntity<String> setWithExpire(
            @Parameter(description = "缓存键") @RequestParam String key,
            @Parameter(description = "缓存值") @RequestBody Object value,
            @Parameter(description = "过期时间（秒）") @RequestParam long timeout) {
        cacheService.set(key, value, timeout, TimeUnit.SECONDS);
        return ResponseEntity.ok("设置成功");
    }

    @Operation(summary = "获取缓存", description = "根据键获取缓存值")
    @GetMapping("/get")
    public ResponseEntity<Object> get(
            @Parameter(description = "缓存键") @RequestParam String key) {
        Object value = cacheService.get(key);
        return ResponseEntity.ok(value);
    }

    @Operation(summary = "删除缓存", description = "根据键删除缓存")
    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(
            @Parameter(description = "缓存键") @RequestParam String key) {
        Boolean result = cacheService.delete(key);
        return ResponseEntity.ok(result ? "删除成功" : "删除失败");
    }

    @Operation(summary = "检查键是否存在", description = "检查指定键是否存在")
    @GetMapping("/exists")
    public ResponseEntity<Boolean> hasKey(
            @Parameter(description = "缓存键") @RequestParam String key) {
        Boolean exists = cacheService.hasKey(key);
        return ResponseEntity.ok(exists);
    }

    @Operation(summary = "设置过期时间", description = "为指定键设置过期时间")
    @PostMapping("/expire")
    public ResponseEntity<String> expire(
            @Parameter(description = "缓存键") @RequestParam String key,
            @Parameter(description = "过期时间（秒）") @RequestParam long timeout) {
        Boolean result = cacheService.expire(key, timeout, TimeUnit.SECONDS);
        return ResponseEntity.ok(result ? "设置成功" : "设置失败");
    }

    @Operation(summary = "获取过期时间", description = "获取指定键的剩余过期时间")
    @GetMapping("/ttl")
    public ResponseEntity<Long> getExpire(
            @Parameter(description = "缓存键") @RequestParam String key) {
        Long expire = cacheService.getExpire(key);
        return ResponseEntity.ok(expire);
    }

    // =============================Hash操作=============================

    @Operation(summary = "设置Hash值", description = "设置Hash类型的键值对")
    @PostMapping("/hash/set")
    public ResponseEntity<String> hSet(
            @Parameter(description = "Hash键") @RequestParam String key,
            @Parameter(description = "Hash字段") @RequestParam String hashKey,
            @Parameter(description = "Hash值") @RequestBody Object value) {
        cacheService.hSet(key, hashKey, value);
        return ResponseEntity.ok("设置成功");
    }

    @Operation(summary = "获取Hash值", description = "获取Hash类型的指定字段值")
    @GetMapping("/hash/get")
    public ResponseEntity<Object> hGet(
            @Parameter(description = "Hash键") @RequestParam String key,
            @Parameter(description = "Hash字段") @RequestParam String hashKey) {
        Object value = cacheService.hGet(key, hashKey);
        return ResponseEntity.ok(value);
    }

    @Operation(summary = "获取Hash所有键值对", description = "获取Hash类型的所有键值对")
    @GetMapping("/hash/get-all")
    public ResponseEntity<Map<Object, Object>> hGetAll(
            @Parameter(description = "Hash键") @RequestParam String key) {
        Map<Object, Object> map = cacheService.hGetAll(key);
        return ResponseEntity.ok(map);
    }

    @Operation(summary = "删除Hash字段", description = "删除Hash类型的指定字段")
    @DeleteMapping("/hash/delete")
    public ResponseEntity<String> hDelete(
            @Parameter(description = "Hash键") @RequestParam String key,
            @Parameter(description = "Hash字段") @RequestParam String hashKey) {
        Long result = cacheService.hDelete(key, hashKey);
        return ResponseEntity.ok("删除了 " + result + " 个字段");
    }

    // =============================List操作=============================

    @Operation(summary = "从左侧推入List", description = "从List左侧推入元素")
    @PostMapping("/list/left-push")
    public ResponseEntity<String> lLeftPush(
            @Parameter(description = "List键") @RequestParam String key,
            @Parameter(description = "元素值") @RequestBody Object value) {
        Long size = cacheService.lLeftPush(key, value);
        return ResponseEntity.ok("推入成功，当前长度：" + size);
    }

    @Operation(summary = "从右侧推入List", description = "从List右侧推入元素")
    @PostMapping("/list/right-push")
    public ResponseEntity<String> lRightPush(
            @Parameter(description = "List键") @RequestParam String key,
            @Parameter(description = "元素值") @RequestBody Object value) {
        Long size = cacheService.lRightPush(key, value);
        return ResponseEntity.ok("推入成功，当前长度：" + size);
    }

    @Operation(summary = "从左侧弹出List", description = "从List左侧弹出元素")
    @PostMapping("/list/left-pop")
    public ResponseEntity<Object> lLeftPop(
            @Parameter(description = "List键") @RequestParam String key) {
        Object value = cacheService.lLeftPop(key);
        return ResponseEntity.ok(value);
    }

    @Operation(summary = "从右侧弹出List", description = "从List右侧弹出元素")
    @PostMapping("/list/right-pop")
    public ResponseEntity<Object> lRightPop(
            @Parameter(description = "List键") @RequestParam String key) {
        Object value = cacheService.lRightPop(key);
        return ResponseEntity.ok(value);
    }

    @Operation(summary = "获取List范围元素", description = "获取List指定范围的元素")
    @GetMapping("/list/range")
    public ResponseEntity<java.util.List<Object>> lRange(
            @Parameter(description = "List键") @RequestParam String key,
            @Parameter(description = "开始位置") @RequestParam(defaultValue = "0") long start,
            @Parameter(description = "结束位置") @RequestParam(defaultValue = "-1") long end) {
        java.util.List<Object> list = cacheService.lRange(key, start, end);
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "获取List长度", description = "获取List的长度")
    @GetMapping("/list/size")
    public ResponseEntity<Long> lSize(
            @Parameter(description = "List键") @RequestParam String key) {
        Long size = cacheService.lSize(key);
        return ResponseEntity.ok(size);
    }

    // =============================Set操作=============================

    @Operation(summary = "添加Set元素", description = "向Set中添加元素")
    @PostMapping("/set/add")
    public ResponseEntity<String> sAdd(
            @Parameter(description = "Set键") @RequestParam String key,
            @Parameter(description = "元素值") @RequestBody Object value) {
        Long count = cacheService.sAdd(key, value);
        return ResponseEntity.ok("添加了 " + count + " 个元素");
    }

    @Operation(summary = "获取Set所有元素", description = "获取Set中的所有元素")
    @GetMapping("/set/members")
    public ResponseEntity<Set<Object>> sMembers(
            @Parameter(description = "Set键") @RequestParam String key) {
        Set<Object> members = cacheService.sMembers(key);
        return ResponseEntity.ok(members);
    }

    @Operation(summary = "检查Set元素", description = "检查Set中是否包含指定元素")
    @GetMapping("/set/is-member")
    public ResponseEntity<Boolean> sIsMember(
            @Parameter(description = "Set键") @RequestParam String key,
            @Parameter(description = "元素值") @RequestParam String value) {
        Boolean isMember = cacheService.sIsMember(key, value);
        return ResponseEntity.ok(isMember);
    }

    @Operation(summary = "获取Set大小", description = "获取Set的大小")
    @GetMapping("/set/size")
    public ResponseEntity<Long> sSize(
            @Parameter(description = "Set键") @RequestParam String key) {
        Long size = cacheService.sSize(key);
        return ResponseEntity.ok(size);
    }

    @Operation(summary = "移除Set元素", description = "从Set中移除指定元素")
    @DeleteMapping("/set/remove")
    public ResponseEntity<String> sRemove(
            @Parameter(description = "Set键") @RequestParam String key,
            @Parameter(description = "元素值") @RequestParam String value) {
        Long count = cacheService.sRemove(key, value);
        return ResponseEntity.ok("移除了 " + count + " 个元素");
    }
}