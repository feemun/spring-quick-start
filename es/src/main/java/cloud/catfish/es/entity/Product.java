package cloud.catfish.es.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体类
 */
public class Product {
    private String id;
    private String name;
    private String description;
    private String category;
    private String brand;
    private BigDecimal price;
    private Integer stock;
    private String status;
    private Double rating;
    private Integer salesCount;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    // 构造函数
    public Product() {}

    public Product(String id, String name, String category, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
        this.status = "ACTIVE";
        this.stock = 0;
        this.rating = 0.0;
        this.salesCount = 0;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getSalesCount() {
        return salesCount;
    }

    public void setSalesCount(Integer salesCount) {
        this.salesCount = salesCount;
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
                ", status='" + status + '\'' +
                ", rating=" + rating +
                ", salesCount=" + salesCount +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}