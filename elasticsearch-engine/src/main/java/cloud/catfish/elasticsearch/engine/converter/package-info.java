/**
 * 数据转换器包
 * 
 * 本包包含Elasticsearch搜索模块的所有数据转换器（Converter）�? * 转换器负责在不同数据对象之间进行转换，如Entity与DTO之间、DTO与VO之间的相互转换�? * 
 * <h2>转换器类�?/h2>
 * <ul>
 *   <li><strong>Entity转换�?/strong>：Entity �?DTO 相互转换</li>
 *   <li><strong>VO转换�?/strong>：DTO �?VO 相互转换</li>
 *   <li><strong>请求转换�?/strong>：请求参�?�?查询条件转换</li>
 *   <li><strong>响应转换�?/strong>：查询结�?�?响应对象转换</li>
 * </ul>
 * 
 * <h2>设计原则</h2>
 * <ul>
 *   <li><strong>单一职责</strong>：每个转换器专注于特定类型的转换</li>
 *   <li><strong>双向转换</strong>：支持正向和反向转换</li>
 *   <li><strong>空值处�?/strong>：妥善处理null值和空集�?/li>
 *   <li><strong>性能优化</strong>：避免不必要的对象创建和深拷�?/li>
 *   <li><strong>类型安全</strong>：使用泛型确保类型安�?/li>
 * </ul>
 * 
 * <h2>转换器接口示�?/h2>
 * <pre>{@code
 * public interface BaseConverter<E, D> {
 *     
 *     /**
 *      * Entity转换为DTO
 *      */
package cloud.catfish.elasticsearch.engine.converter;

import cloud.catfish.elasticsearch.engine.entity.Product;
import org.springframework.stereotype.Component;

import java.util.List;

/*
 *     D toDto(E entity);
 *
 */

      /**
 *      * DTO转换为Entity
 *      */
 *     E toEntity(D dto);
 *     
 *     /**
 *      * Entity列表转换为DTO列表
 *      */
 *     List<D> toDtoList(List<E> entityList);
 *     
 *     /**
 *      * DTO列表转换为Entity列表
 *      */
 *     List<E> toEntityList(List<D> dtoList);
 * }
 * }</pre>
 * 
 * <h2>商品转换器示�?/h2>
 * <pre>{@code
 * @Component
 * public class ProductConverter implements BaseConverter<Product, ProductDTO> {
 *     
 *     @Override
 *     public ProductDTO toDto(Product entity) {
 *         if (entity == null) {
 *             return null;
 *         }
 *         
 *         return ProductDTO.builder()
 *             .id(entity.getId())
 *             .name(entity.getName())
 *             .description(entity.getDescription())
 *             .price(entity.getPrice())
 *             .category(entity.getCategory())
 *             .brand(entity.getBrand())
 *             .sales(entity.getSales())
 *             .rating(entity.getRating())
 *             .inStock(entity.getInStock())
 *             .createTime(entity.getCreateTime())
 *             .updateTime(entity.getUpdateTime())
 *             .build();
 *     }
 *     
 *     @Override
 *     public Product toEntity(ProductDTO dto) {
 *         if (dto == null) {
 *             return null;
 *         }
 *         
 *         Product product = new Product();
 *         product.setId(dto.getId());
 *         product.setName(dto.getName());
 *         product.setDescription(dto.getDescription());
 *         product.setPrice(dto.getPrice());
 *         product.setCategory(dto.getCategory());
 *         product.setBrand(dto.getBrand());
 *         product.setSales(dto.getSales());
 *         product.setRating(dto.getRating());
 *         product.setInStock(dto.getInStock());
 *         product.setCreateTime(dto.getCreateTime());
 *         product.setUpdateTime(dto.getUpdateTime());
 *         return product;
 *     }
 * }
 * }</pre>
 * 
 * <h2>VO转换器示�?/h2>
 * <pre>{@code
 * @Component
 * public class ProductVOConverter {
 *     
 *     public ProductListVO toListVO(ProductDTO dto) {
 *         if (dto == null) {
 *             return null;
 *         }
 *         
 *         ProductListVO vo = new ProductListVO();
 *         vo.setId(dto.getId());
 *         vo.setName(dto.getName());
 *         vo.setPrice(dto.getPrice());
 *         vo.setCategory(dto.getCategory());
 *         vo.setBrand(dto.getBrand());
 *         vo.setSales(dto.getSales());
 *         vo.setRating(dto.getRating());
 *         vo.setInStock(dto.getInStock());
 *         
 *         // 格式化显示字�? *         vo.setDisplayPrice(formatPrice(dto.getPrice()));
 *         vo.setRatingDisplay(formatRating(dto.getRating()));
 *         vo.setStockStatus(dto.getInStock() ? "现货" : "缺货");
 *         vo.setCreateTimeDisplay(formatDateTime(dto.getCreateTime()));
 *         
 *         return vo;
 *     }
 * }
 * }</pre>
 * 
 * <h2>批量转换优化</h2>
 * <ul>
 *   <li>使用Stream API进行批量转换</li>
 *   <li>并行处理大量数据转换</li>
 *   <li>缓存转换结果避免重复计算</li>
 *   <li>使用对象池减少GC压力</li>
 * </ul>
 * 
 * <h2>转换工具�?/h2>
 * <ul>
 *   <li><strong>日期转换</strong>：LocalDateTime与String之间的转�?/li>
 *   <li><strong>枚举转换</strong>：枚举值与字符串之间的转换</li>
 *   <li><strong>集合转换</strong>：不同集合类型之间的转换</li>
 *   <li><strong>数值转�?/strong>：数值类型之间的安全转换</li>
 * </ul>
 * 
 * <h2>最佳实�?/h2>
 * <ul>
 *   <li>使用MapStruct等工具自动生成转换代�?/li>
 *   <li>为复杂转换逻辑编写单元测试</li>
 *   <li>处理循环引用问题</li>
 *   <li>考虑使用Builder模式简化对象构�?/li>
 *   <li>提供默认值处理策�?/li>
 * </ul>
 * 
 * @author catfish
 * @version 1.0.0
 * @since 1.0.0
 */
package cloud.catfish.elasticsearch.engine.converter;
