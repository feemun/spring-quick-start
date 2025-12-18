package cloud.catfish.elasticsearch9.dto;

import cloud.catfish.elasticsearch9.json.CustomDateDeserializer;
import cloud.catfish.elasticsearch9.json.CustomDateSerializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NetworkLogDto {
    private String id;
    
    @JsonProperty("src_ip")
    private String srcIp;
    
    @JsonProperty("dest_ip")
    private String destIp;
    
    private String tags;
    
    @JsonProperty("src_cidr")
    private String srcCidr;
    
    @JsonProperty("src_station_id")
    private String srcStationId;
    
    @JsonProperty("src_station_name")
    private String srcStationName;
    
    @JsonProperty("dest_cidr")
    private String destCidr;
    
    @JsonProperty("dest_station_id")
    private String destStationId;
    
    @JsonProperty("dest_station_name")
    private String destStationName;
    
    @JsonProperty("create_time")
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @JsonSerialize(using = CustomDateSerializer.class)
    private LocalDateTime createTime;
    
    private Long bytes;
}
