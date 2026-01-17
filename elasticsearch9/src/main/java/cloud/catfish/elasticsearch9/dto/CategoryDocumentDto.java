package cloud.catfish.elasticsearch9.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CategoryDocumentDto(
        @JsonProperty("category_id") String categoryId,
        @JsonProperty("category_level") Integer categoryLevel,
        @JsonProperty("category_name") String categoryName,
        @JsonProperty("category_label") String categoryLabel,
        @JsonProperty("level1_id") String level1Id,
        @JsonProperty("level1_name") String level1Name,
        @JsonProperty("level2_id") String level2Id,
        @JsonProperty("level2_name") String level2Name,
        @JsonProperty("level3_id") String level3Id,
        @JsonProperty("level3_name") String level3Name,
        @JsonProperty("level4_id") String level4Id,
        @JsonProperty("level4_name") String level4Name,
        @JsonProperty("level5_id") String level5Id,
        @JsonProperty("level5_name") String level5Name,
        @JsonProperty("level6_id") String level6Id,
        @JsonProperty("level6_name") String level6Name
) {
}

