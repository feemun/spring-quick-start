package cloud.catfish.elasticsearch.engine.controller;

import cloud.catfish.elasticsearch.engine.entity.Product;
import cloud.catfish.elasticsearch.engine.repository.ProductRepository;
import cloud.catfish.common.api.CommonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * 测试数据控制�? * 用于生成测试数据验证高频查询功能
 * 
 * @author catfish
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/es/test")
public class TestDataController {

    @Autowired
    private ProductRepository productRepository;

    private final Random random = new Random();
    
    // 测试数据模板
    private final String[] categories = {"电子产品", "服装鞋帽", "家居用品", "食品饮料", "图书音像", "运动户外", "美妆个护", "母婴用品"};
    private final String[] brands = {"苹果", "华为", "小米", "三星", "联想", "戴尔", "耐克", "阿迪达斯", "优衣�?, "海尔"};
    private final String[] productNames = {
        "iPhone 15 Pro", "华为Mate60", "小米14", "MacBook Pro", "ThinkPad X1", 
        "AirPods Pro", "华为Watch GT4", "小米手环8", "iPad Air", "Surface Pro",
        "运动�?, "休闲�?, "T�?, "连衣�?, "牛仔�?,
        "咖啡�?, "电饭�?, "空气净化器", "吸尘�?, "洗衣�?,
        "咖啡�?, "绿茶", "牛奶", "面包", "巧克�?
    };
    private final String[] descriptions = {
        "高品质产品，性能卓越，用户体验极�?,
        "时尚设计，功能强大，适合日常使用",
        "经济实惠，质量可靠，性价比超�?,
        "创新科技，引领潮流，值得拥有",
        "精工制造，品质保证，用户首�?
    };

    /**
     * 生成测试数据
     * POST /api/es/test/generate?count=100
     */
    @PostMapping("/generate")
    public CommonResult<String> generateTestData(@RequestParam(defaultValue = "100") int count) {
        List<Product> products = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            Product product = createRandomProduct(i + 1);
            products.add(product);
        }
        
        productRepository.saveAll(products);
        return CommonResult.success("成功生成 " + count + " 条测试数�?);
    }

    /**
     * 清空所有数�?     * DELETE /api/es/test/clear
     */
    @DeleteMapping("/clear")
    public CommonResult<String> clearAllData() {
        productRepository.deleteAll();
        return CommonResult.success("已清空所有测试数�?);
    }

    /**
     * 获取数据统计
     * GET /api/es/test/stats
     */
    @GetMapping("/stats")
    public CommonResult<String> getDataStats() {
        long count = productRepository.count();
        return CommonResult.success("当前索引中共�?" + count + " 条商品数�?);
    }

    /**
     * 生成特定分类的测试数�?     * POST /api/es/test/generate-category?category=电子产品&count=50
     */
    @PostMapping("/generate-category")
    public CommonResult<String> generateCategoryData(
            @RequestParam String category,
            @RequestParam(defaultValue = "50") int count) {
        List<Product> products = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            Product product = createRandomProduct(i + 1);
            product.setCategory(category);
            products.add(product);
        }
        
        productRepository.saveAll(products);
        return CommonResult.success("成功生成 " + count + " �?" + category + " 分类的测试数�?);
    }

    /**
     * 创建随机商品
     */
    private Product createRandomProduct(int index) {
        Product product = new Product();
        
        // 基本信息
        product.setId("product_" + index);
        product.setName(getRandomElement(productNames) + " " + index);
        product.setDescription(getRandomElement(descriptions));
        product.setCategory(getRandomElement(categories));
        product.setBrand(getRandomElement(brands));
        
        // 价格和库�?        product.setPrice(BigDecimal.valueOf(random.nextDouble() * 10000 + 100).setScale(2, BigDecimal.ROUND_HALF_UP));
        product.setStock(random.nextInt(1000) + 10);
        product.setSales(random.nextInt(10000));
        
        // 评价信息
        product.setRating(random.nextDouble() * 2 + 3); // 3-5�?        product.setReviewCount(random.nextInt(1000) + 10);
        
        // 状态和时间
        product.setStatus(random.nextInt(10) > 1 ? 1 : 0); // 90%上架
        product.setCreateTime(LocalDateTime.now().minusDays(random.nextInt(365)));
        product.setUpdateTime(LocalDateTime.now().minusDays(random.nextInt(30)));
        product.setPublishTime(product.getCreateTime().plusDays(random.nextInt(7)));
        
        // 其他信息
        product.setWeight(random.nextDouble() * 5000 + 100); // 100g-5kg
        product.setDimensions(generateRandomDimensions());
        product.setSupplierId("supplier_" + (random.nextInt(20) + 1));
        product.setSupplierName("供应�? + (random.nextInt(20) + 1));
        
        // 标签
        product.setTags(generateRandomTags());
        
        // 图片URL
        product.setImageUrls(generateRandomImageUrls());
        
        // 规格信息
        product.setSpecifications(generateRandomSpecifications());
        
        return product;
    }

    private String getRandomElement(String[] array) {
        return array[random.nextInt(array.length)];
    }

    private String generateRandomDimensions() {
        int length = random.nextInt(50) + 10;
        int width = random.nextInt(50) + 10;
        int height = random.nextInt(30) + 5;
        return length + "x" + width + "x" + height + "cm";
    }

    private List<String> generateRandomTags() {
        String[] allTags = {"热销", "新品", "推荐", "限时优惠", "包邮", "正品保证", "7天退�?, "品质保证"};
        List<String> tags = new ArrayList<>();
        int tagCount = random.nextInt(4) + 1; // 1-4个标�?        
        for (int i = 0; i < tagCount; i++) {
            String tag = getRandomElement(allTags);
            if (!tags.contains(tag)) {
                tags.add(tag);
            }
        }
        return tags;
    }

    private List<String> generateRandomImageUrls() {
        List<String> urls = new ArrayList<>();
        int imageCount = random.nextInt(5) + 1; // 1-5张图�?        
        for (int i = 0; i < imageCount; i++) {
            urls.add("https://example.com/images/product_" + random.nextInt(1000) + ".jpg");
        }
        return urls;
    }

    private String generateRandomSpecifications() {
        return "{\"color\":\"" + getRandomElement(new String[]{"红色", "蓝色", "黑色", "白色", "灰色"}) + 
               "\",\"size\":\"" + getRandomElement(new String[]{"S", "M", "L", "XL", "XXL"}) + 
               "\",\"material\":\"" + getRandomElement(new String[]{"棉质", "涤纶", "真丝", "羊毛", "混纺"}) + 
               "\"}";
    }
}
