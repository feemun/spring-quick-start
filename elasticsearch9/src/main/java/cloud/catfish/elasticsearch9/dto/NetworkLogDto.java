package cloud.catfish.elasticsearch9.dto;

import cloud.catfish.elasticsearch9.json.CustomDateDeserializer;
import cloud.catfish.elasticsearch9.json.CustomDateSerializer;
import tools.jackson.annotation.JsonAlias;
import tools.jackson.annotation.JsonIgnoreProperties;
import tools.jackson.annotation.JsonProperty;
import tools.jackson.databind.annotation.JsonDeserialize;
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
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @JsonSerialize(using = CustomDateSerializer.class)
    private LocalDateTime createTime;
    
    private Long bytes;
}
