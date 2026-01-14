package cloud.catfish.elasticsearch9.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record PlaceDocumentDto(
        @JsonProperty("fsq_id") String fsqId,
        @JsonProperty("name") String name,
        @JsonProperty("latitude") Double latitude,
        @JsonProperty("longitude") Double longitude,
        @JsonProperty("address") String address,
        @JsonProperty("city") String city,
        @JsonProperty("region") String region,
        @JsonProperty("postcode") String postcode,
        @JsonProperty("admin_region") String adminRegion,
        @JsonProperty("post_town") String postTown,
        @JsonProperty("po_box") String poBox,
        @JsonProperty("country") String country,
        @JsonProperty("created_date") @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss") LocalDateTime createdDate,
        @JsonProperty("refreshed_date") @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss") LocalDateTime refreshedDate,
        @JsonProperty("closed_date") @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss") LocalDateTime closedDate,
        @JsonProperty("phone") String phone,
        @JsonProperty("website") String website,
        @JsonProperty("email") String email,
        @JsonProperty("facebook_id") String facebookId,
        @JsonProperty("instagram") String instagram,
        @JsonProperty("twitter") String twitter,
        @JsonProperty("category_ids") String categoryIds,
        @JsonProperty("category_labels") String categoryLabels,
        @JsonProperty("geometry") String geometry,
        @JsonProperty("bounds") String bounds
) {
}

