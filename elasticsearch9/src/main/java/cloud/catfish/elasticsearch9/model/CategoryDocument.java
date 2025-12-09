package cloud.catfish.elasticsearch9.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CategoryDocument {
    @JsonProperty("category_id")
    private String categoryId;
    
    @JsonProperty("category_level")
    private Integer categoryLevel;
    
    @JsonProperty("category_name")
    private String categoryName;
    
    @JsonProperty("category_label")
    private String categoryLabel;
    
    @JsonProperty("level1_id")
    private String level1Id;
    
    @JsonProperty("level1_name")
    private String level1Name;
    
    @JsonProperty("level2_id")
    private String level2Id;
    
    @JsonProperty("level2_name")
    private String level2Name;
    
    @JsonProperty("level3_id")
    private String level3Id;
    
    @JsonProperty("level3_name")
    private String level3Name;
    
    @JsonProperty("level4_id")
    private String level4Id;
    
    @JsonProperty("level4_name")
    private String level4Name;
    
    @JsonProperty("level5_id")
    private String level5Id;
    
    @JsonProperty("level5_name")
    private String level5Name;
    
    @JsonProperty("level6_id")
    private String level6Id;
    
    @JsonProperty("level6_name")
    private String level6Name;
}
