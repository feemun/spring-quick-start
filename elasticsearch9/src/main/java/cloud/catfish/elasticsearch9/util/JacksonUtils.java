package cloud.catfish.elasticsearch9.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

/**
 * Jackson 工具类
 * 提供统一配置的 ObjectMapper 及便捷的序列化/反序列化方法
 * 自动处理 Checked Exception，简化调用
 */
@Slf4j
public class JacksonUtils {

    private static final ObjectMapper OBJECT_MAPPER = createObjectMapper();

    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // 1. 注册 JavaTimeModule 以支持 java.time 类型 (LocalDateTime 等)
        mapper.registerModule(new JavaTimeModule());
        
        // 2. 禁用 "将日期写为时间戳"，使其序列化为字符串 (ISO-8601 或自定义格式)
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        // 3. 忽略 JSON 中存在但 Java 对象中不存在的字段，防止反序列化报错
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        // 4. 忽略空值字段 (可选，视需求而定)
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        
        return mapper;
    }

    /**
     * 获取全局统一配置的 ObjectMapper
     */
    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }

    // ========================================================================
    // 序列化方法 (Object -> JSON String)
    // ========================================================================

    /**
     * 将对象序列化为 JSON 字符串
     * @param object 任意对象
     * @return JSON 字符串，如果对象为 null 返回 null
     */
    public static String toJson(Object object) {
        return toJson(OBJECT_MAPPER, object);
    }

    /**
     * 将对象序列化为 JSON 字符串 (支持自定义 ObjectMapper)
     */
    public static String toJson(ObjectMapper mapper, Object object) {
        if (object == null) {
            return null;
        }
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("JSON serialization failed: {}", object, e);
            throw new RuntimeException("JSON serialization failed", e);
        }
    }

    // ========================================================================
    // 反序列化方法 (JSON String -> Object)
    // ========================================================================

    /**
     * 将 JSON 字符串反序列化为对象
     * @param json JSON 字符串
     * @param clazz 目标类型 Class
     * @return 反序列化后的对象，如果 json 为空返回 null
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        return fromJson(OBJECT_MAPPER, json, clazz);
    }

    /**
     * 将 JSON 字符串反序列化为对象 (支持自定义 ObjectMapper)
     */
    public static <T> T fromJson(ObjectMapper mapper, String json, Class<T> clazz) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        try {
            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            log.error("JSON deserialization failed: json={}, class={}", json, clazz.getName(), e);
            throw new RuntimeException("JSON deserialization failed", e);
        }
    }

    /**
     * 将 JSON 字符串反序列化为复杂类型对象 (如 List<User>)
     * @param json JSON 字符串
     * @param typeReference 类型引用，例如 new TypeReference<List<User>>() {}
     * @return 反序列化后的对象
     */
    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        return fromJson(OBJECT_MAPPER, json, typeReference);
    }

    /**
     * 将 JSON 字符串反序列化为复杂类型对象 (支持自定义 ObjectMapper)
     */
    public static <T> T fromJson(ObjectMapper mapper, String json, TypeReference<T> typeReference) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        try {
            return mapper.readValue(json, typeReference);
        } catch (IOException e) {
            log.error("JSON deserialization failed: json={}, type={}", json, typeReference.getType(), e);
            throw new RuntimeException("JSON deserialization failed", e);
        }
    }
}