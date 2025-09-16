package cloud.catfish.elasticsearch.engine.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品实体�? * 对应Elasticsearch中的products索引
 * 
 * @author catfish
 * @since 1.0.0
 */
@Document(indexName = "products")
@Setting(numberOfShards = 3, numberOfReplicas = 1)
public class Product {

    /**
     * 商品ID
     */
    @Id
    private String id;

    /**
     * 商品名称
     * 支持全文搜索，使用IK分词�?     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String name;

    /**
     * 商品描述
     * 支持全文搜索
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String description;

    /**
     * 商品分类
     * 支持精确匹配和全文搜�?     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart", 
           fielddata = true)
    private String category;

    /**
     * 商品品牌
     * 支持精确匹配和全文搜�?     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart",
           fielddata = true)
    private String brand;

    /**
     * 商品价格
     * 支持范围查询和排�?     */
    @Field(type = FieldType.Double)
    private BigDecimal price;

    /**
     * 库存数量
     * 支持范围查询
     */
    @Field(type = FieldType.Integer)
    private Integer stock;

    /**
     * 销�?     * 用于热门商品排序
     */
    @Field(type = FieldType.Integer)
    private Integer sales;

    /**
     * 商品图片URL列表
     */
    @Field(type = FieldType.Keyword)
    private List<String> imageUrls;

    /**
     * 商品标签
     * 支持多标签搜�?     */
    @Field(type = FieldType.Keyword)
    private List<String> tags;

    /**
     * 商品规格信息
     * 存储JSON格式的规格数�?     */
    @Field(type = FieldType.Text)
    private String specifications;

    /**
     * 商品状�?     * 0-下架, 1-上架, 2-预售
     */
    @Field(type = FieldType.Integer)
    private Integer status;

    /**
     * 商品评分
     * 用于推荐算法
     */
    @Field(type = FieldType.Double)
    private Double rating;

    /**
     * 评价数量
     */
    @Field(type = FieldType.Integer)
    private Integer reviewCount;

    /**
     * 商品重量（克�?     */
    @Field(type = FieldType.Double)
    private Double weight;

    /**
     * 商品尺寸
     */
    @Field(type = FieldType.Keyword)
    private String dimensions;

    /**
     * 供应商ID
     */
    @Field(type = FieldType.Keyword)
    private String supplierId;

    /**
     * 供应商名�?     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String supplierName;

    /**
     * 创建时间
     */
    @Field(type = FieldType.Date)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Field(type = FieldType.Date)
    private LocalDateTime updateTime;

    /**
     * 上架时间
     */
    @Field(type = FieldType.Date)
    private LocalDateTime publishTime;

    // 构造函�?    public Product() {}

    public Product(String name, String description, String category, String brand, BigDecimal price) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.brand = brand;
        this.price = price;
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
        this.status = 1; // 默认上架
        this.stock = 0;
        this.sales = 0;
        this.rating = 0.0;
        this.reviewCount = 0;
    }

    // Getter和Setter方法
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getSales() {
        return sales;
    }

    public void setSales(Integer sales) {
        this.sales = sales;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getSpecifications() {
        return specifications;
    }

    public void setSpecifications(String specifications) {
        this.specifications = specifications;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getDimensions() {
        return dimensions;
    }

    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public LocalDateTime getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(LocalDateTime publishTime) {
        this.publishTime = publishTime;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", brand='" + brand + '\'' +
                ", price=" + price +
                ", stock=" + stock +
                ", sales=" + sales +
                ", status=" + status +
                ", rating=" + rating +
                ", createTime=" + createTime +
                '}';
    }
}
