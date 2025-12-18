package cloud.catfish.elasticsearch9.json;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 自定义日期序列化器
 * 默认序列化为 yyyy-MM-dd HH:mm:ss 格式
 * 支持通过 @JsonFormat 注解覆盖格式或序列化为时间戳
 */
public class CustomDateSerializer extends JsonSerializer<LocalDateTime> implements ContextualSerializer {

    private final DateTimeFormatter formatter;
    private final boolean useTimestamp;

    // 默认构造函数
    public CustomDateSerializer() {
        this(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"), false);
    }

    // 私有构造函数，用于创建配置后的实例
    private CustomDateSerializer(DateTimeFormatter formatter, boolean useTimestamp) {
        this.formatter = formatter;
        this.useTimestamp = useTimestamp;
    }

    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }

        if (useTimestamp) {
            // 转换为毫秒时间戳
            gen.writeNumber(value.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        } else {
            // 按照指定格式序列化字符串
            gen.writeString(formatter.format(value));
        }
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        // 如果字段上有注解，尝试获取 @JsonFormat 配置
        if (property != null) {
            JsonFormat.Value format = prov.getAnnotationIntrospector().findFormat(property.getMember());
            if (format != null) {
                // 1. 检查是否配置为 NUMBER (时间戳)
                if (format.getShape() == JsonFormat.Shape.NUMBER || format.getShape() == JsonFormat.Shape.NUMBER_INT) {
                    return new CustomDateSerializer(null, true);
                }
                
                // 2. 检查是否有自定义 Pattern
                if (format.hasPattern()) {
                    try {
                        DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern(format.getPattern());
                        return new CustomDateSerializer(customFormatter, false);
                    } catch (IllegalArgumentException e) {
                        // 如果 Pattern 无效，回退到默认
                    }
                }
            }
        }
        // 如果没有特殊配置，返回当前默认实例
        return this;
    }
}
