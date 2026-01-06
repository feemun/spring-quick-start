package cloud.catfish.elasticsearch9.dto;

import cloud.catfish.elasticsearch9.json.CustomDateSerializer;
import tools.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NetworkLogStatDto {
    private String key;
    private Long count;
    private Double totalBytes;
    
    @JsonSerialize(using = CustomDateSerializer.class)
    private LocalDateTime firstCreated;
    
    @JsonSerialize(using = CustomDateSerializer.class)
    private LocalDateTime lastCreated;
}
