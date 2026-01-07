package cloud.catfish.cache.config;

import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Component
public class CacheCodec {

    private final ObjectMapper mapper;

    public CacheCodec(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public <T> String encode(T value) {
        return mapper.writeValueAsString(value);
    }

    public <T> T decode(String json, Class<T> type) {
        return mapper.readValue(json, type);
    }

    public <T> T decode(String json, TypeReference<T> typeRef) {
        return mapper.readValue(json, typeRef);
    }
}

