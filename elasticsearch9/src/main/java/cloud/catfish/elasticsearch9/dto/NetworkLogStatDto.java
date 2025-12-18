package cloud.catfish.elasticsearch9.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NetworkLogStatDto {
    private String key;
    private Long count;
    private Double totalBytes;
    private String firstCreated;
    private String lastCreated;
}
