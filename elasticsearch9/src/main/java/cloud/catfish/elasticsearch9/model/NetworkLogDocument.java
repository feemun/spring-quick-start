package cloud.catfish.elasticsearch9.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class NetworkLogDocument {
    @JsonProperty("id")
    private String id;

    @JsonProperty("src_ip")
    private String srcIp;

    @JsonProperty("dest_ip")
    private String destIp;

    @JsonProperty("tags")
    private List<String> tags;

    @JsonProperty("extra_info")
    private Map<String, Object> extraInfo;
}
