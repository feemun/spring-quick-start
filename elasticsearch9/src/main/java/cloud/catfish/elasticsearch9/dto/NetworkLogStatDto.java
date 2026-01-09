package cloud.catfish.elasticsearch9.dto;

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

    private LocalDateTime firstCreated;

    private LocalDateTime lastCreated;
}
