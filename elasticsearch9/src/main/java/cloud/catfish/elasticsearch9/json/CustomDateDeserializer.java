package cloud.catfish.elasticsearch9.json;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.ParsePosition;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义日期反序列化器
 * 支持多种日期格式和时间戳，转换为 LocalDateTime
 * 使用 java.time API 替代 java.util.Date
 * 优化：避免异常流，提高性能
 */
@Slf4j
public class CustomDateDeserializer extends JsonDeserializer<LocalDateTime> {

    private static final List<DateTimeFormatter> FORMATTERS = new ArrayList<>();

    static {
        FORMATTERS.add(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        FORMATTERS.add(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        FORMATTERS.add(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        FORMATTERS.add(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
        
        // Literal 'Z' implies UTC
        FORMATTERS.add(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneId.of("UTC")));
        FORMATTERS.add(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("UTC")));
        
        // Offset Z
        FORMATTERS.add(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ"));
        FORMATTERS.add(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
        
        FORMATTERS.add(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        FORMATTERS.add(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
        FORMATTERS.add(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        FORMATTERS.add(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
        FORMATTERS.add(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        FORMATTERS.add(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        FORMATTERS.add(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String text = p.getText();
        if (StringUtils.isBlank(text)) {
            return null;
        }

        // 1. 尝试解析为时间戳 (优化：先检查是否为数字，避免抛出异常)
        // StringUtils.isNumeric 只检查Unicode数字，不包含负号，但时间戳通常为正数
        if (StringUtils.isNumeric(text)) {
            try {
                long timestamp = Long.parseLong(text);
                return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
            } catch (NumberFormatException e) {
                // 忽略，可能超长
            }
        } else if (text.length() > 1 && text.startsWith("-") && StringUtils.isNumeric(text.substring(1))) {
             // 处理负数时间戳
             try {
                long timestamp = Long.parseLong(text);
                return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
            } catch (NumberFormatException e) {
                // 忽略
            }
        }

        // 2. 遍历格式器尝试解析 (优化：使用 ParsePosition 避免抛出异常)
        for (DateTimeFormatter formatter : FORMATTERS) {
            ParsePosition pos = new ParsePosition(0);
            TemporalAccessor accessor = formatter.parse(text, pos);

            // 检查是否解析成功，并且消耗了整个字符串
            if (accessor != null && pos.getErrorIndex() < 0 && pos.getIndex() == text.length()) {
                try {
                    return convertToLocalDateTime(accessor);
                } catch (Exception e) {
                    // 转换失败（例如缺少必要字段），继续尝试下一个
                }
            }
        }

        log.warn("Failed to parse date: {}", text);
        return null;
    }

    private LocalDateTime convertToLocalDateTime(TemporalAccessor accessor) {
        // 1. 如果包含时区/偏移量信息，转换为 Instant 再转为 LocalDateTime (使用系统默认时区)
        if (accessor.isSupported(ChronoField.INSTANT_SECONDS) || accessor.isSupported(ChronoField.OFFSET_SECONDS)) {
            Instant instant = Instant.from(accessor);
            return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        }

        // 2. 如果包含时间信息 (HOUR_OF_DAY)
        if (accessor.isSupported(ChronoField.HOUR_OF_DAY)) {
            return LocalDateTime.from(accessor);
        }

        // 3. 如果只包含日期信息 (DAY_OF_MONTH)
        if (accessor.isSupported(ChronoField.DAY_OF_MONTH)) {
            return LocalDate.from(accessor).atStartOfDay();
        }
        
        throw new IllegalArgumentException("Unsupported temporal accessor type");
    }
}
