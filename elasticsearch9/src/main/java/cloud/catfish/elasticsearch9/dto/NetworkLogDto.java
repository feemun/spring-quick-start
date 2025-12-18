package cloud.catfish.elasticsearch9.dto;


import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NetworkLogDto {
    private String id;
    
    @JsonProperty("src_ip")
    @JsonAlias("srcIp")
    private String srcIp;
    
    @JsonProperty("dest_ip")
    @JsonAlias("destIp")
    private String destIp;
    
    private String tags;
    
    @JsonProperty("src_cidr")
    @JsonAlias("srcCidr")
    private String srcCidr;
    
    @JsonProperty("src_station_id")
    @JsonAlias("srcStationId")
    private String srcStationId;
    
    @JsonProperty("src_station_name")
    @JsonAlias("srcStationName")
    private String srcStationName;
    
    @JsonProperty("dest_cidr")
    @JsonAlias("destCidr")
    private String destCidr;
    
    @JsonProperty("dest_station_id")
    @JsonAlias("destStationId")
    private String destStationId;
    
    @JsonProperty("dest_station_name")
    @JsonAlias("destStationName")
    private String destStationName;
    
    @JsonProperty("create_time")
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    private Long bytes;
}
