package cloud.catfish.elasticsearch9.model;

import tools.jackson.annotation.JsonFormat;
import tools.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PlaceDocument {
    /**
     * Foursquare地点ID
     */
    @JsonProperty("fsq_id")
    private String fsqId;

    /**
     * 名称
     */
    @JsonProperty("name")
    private String name;

    /**
     * 纬度
     */
    @JsonProperty("latitude")
    private Double latitude;

    /**
     * 经度
     */
    @JsonProperty("longitude")
    private Double longitude;

    /**
     * 地址
     */
    @JsonProperty("address")
    private String address;

    /**
     * 所在城市
     */
    @JsonProperty("city")
    private String city;

    /**
     * 区域
     */
    @JsonProperty("region")
    private String region;

    /**
     * 邮政编码
     */
    @JsonProperty("postcode")
    private String postcode;

    /**
     * 行政区域
     */
    @JsonProperty("admin_region")
    private String adminRegion;

    /**
     * 邮寄城镇
     */
    @JsonProperty("post_town")
    private String postTown;

    /**
     * 邮政信箱
     */
    @JsonProperty("po_box")
    private String poBox;

    /**
     * 国家
     */
    @JsonProperty("country")
    private String country;

    /**
     * 创建日期
     */
    @JsonProperty("created_date")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    private LocalDateTime createdDate;

    /**
     * 刷新日期
     */
    @JsonProperty("refreshed_date")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    private LocalDateTime refreshedDate;

    /**
     * 关闭日期
     */
    @JsonProperty("closed_date")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    private LocalDateTime closedDate;

    /**
     * 电话
     */
    @JsonProperty("phone")
    private String phone;

    /**
     * 网站
     */
    @JsonProperty("website")
    private String website;

    /**
     * 电子邮件
     */
    @JsonProperty("email")
    private String email;

    /**
     * Facebook ID
     */
    @JsonProperty("facebook_id")
    private String facebookId;

    /**
     * Instagram账号
     */
    @JsonProperty("instagram")
    private String instagram;

    /**
     * Twitter账号
     */
    @JsonProperty("twitter")
    private String twitter;

    /**
     * 类别ID
     */
    @JsonProperty("category_ids")
    private String categoryIds;

    /**
     * 类别标签
     */
    @JsonProperty("category_labels")
    private String categoryLabels;

    /**
     * 几何形状
     */
    @JsonProperty("geometry")
    private String geometry;

    /**
     * 边界框
     */
    @JsonProperty("bounds")
    private String bounds;
}
