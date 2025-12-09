package cloud.catfish.elasticsearch9.model;

import lombok.Data;

@Data
public class CategoryDocument {
    private String categoryId;
    private Integer categoryLevel;
    private String categoryName;
    private String categoryLabel;
    
    private String level1Id;
    private String level1Name;
    
    private String level2Id;
    private String level2Name;
    
    private String level3Id;
    private String level3Name;
    
    private String level4Id;
    private String level4Name;
    
    private String level5Id;
    private String level5Name;
    
    private String level6Id;
    private String level6Name;
}
