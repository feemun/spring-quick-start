package cloud.catfish.elasticsearch9.model;

import tools.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CategoryDocument {
    /**
     * 类别ID
     */
    @JsonProperty("category_id")
    private String categoryId;
    
    /**
     * 类别层级
     */
    @JsonProperty("category_level")
    private Integer categoryLevel;
    
    /**
     * 类别名称
     */
    @JsonProperty("category_name")
    private String categoryName;
    
    /**
     * 类别标签
     */
    @JsonProperty("category_label")
    private String categoryLabel;
    
    /**
     * 一级类别ID
     */
    @JsonProperty("level1_id")
    private String level1Id;
    
    /**
     * 一级类别名称
     */
    @JsonProperty("level1_name")
    private String level1Name;
    
    /**
     * 二级类别ID
     */
    @JsonProperty("level2_id")
    private String level2Id;
    
    /**
     * 二级类别名称
     */
    @JsonProperty("level2_name")
    private String level2Name;
    
    /**
     * 三级类别ID
     */
    @JsonProperty("level3_id")
    private String level3Id;
    
    /**
     * 三级类别名称
     */
    @JsonProperty("level3_name")
    private String level3Name;
    
    /**
     * 四级类别ID
     */
    @JsonProperty("level4_id")
    private String level4Id;
    
    /**
     * 四级类别名称
     */
    @JsonProperty("level4_name")
    private String level4Name;
    
    /**
     * 五级类别ID
     */
    @JsonProperty("level5_id")
    private String level5Id;
    
    /**
     * 五级类别名称
     */
    @JsonProperty("level5_name")
    private String level5Name;
    
    /**
     * 六级类别ID
     */
    @JsonProperty("level6_id")
    private String level6Id;
    
    /**
     * 六级类别名称
     */
    @JsonProperty("level6_name")
    private String level6Name;
}
