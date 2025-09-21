package cloud.catfish.elasticsearch.engine.service.impl;

import cloud.catfish.elasticsearch.engine.entity.Product;
import cloud.catfish.elasticsearch.engine.repository.ProductRepository;
import cloud.catfish.elasticsearch.engine.service.HighFrequencyQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 高频查询服务实现�? * 基于spring-data-elasticsearch实现各种高频查询操作
 * 
 * @author catfish
 * @since 1.0.0
 */
@Service
public class HighFrequencyQueryServiceImpl implements HighFrequencyQueryService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    private static final String PRODUCT_INDEX = "products";

    @Override
    public Page<Product> fullTextSearch(String keyword, Pageable pageable) {
        return productRepository.findByNameOrDescription(keyword, pageable);
    }

    @Override
    public Page<Product> findByCategory(String category, Pageable pageable) {
        return productRepository.findByCategory(category, pageable);
    }

    @Override
    public Page<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return productRepository.findByPriceBetween(minPrice, maxPrice, pageable);
    }

    @Override
    public Page<Product> findByBrand(String brand, Pageable pageable) {
        return productRepository.findByBrand(brand, pageable);
    }

    @Override
    public Page<Product> findInStock(Pageable pageable) {
        return productRepository.findByStockGreaterThan(0, pageable);
    }

    @Override
    public List<Product> findHotProducts(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "sales"));
        return productRepository.findAll(pageable).getContent();
    }

    @Override
    public List<Product> findLatestProducts(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createTime"));
        return productRepository.findAll(pageable).getContent();
    }

    @Override
    public Page<Product> complexSearch(String keyword, String category, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            boolQuery.should(QueryBuilders.matchQuery("name", keyword))
                    .should(QueryBuilders.matchQuery("description", keyword))
                    .should(QueryBuilders.matchQuery("brand", keyword))
                    .minimumShouldMatch(1);
        }
        
        if (category != null && !category.trim().isEmpty()) {
            boolQuery.filter(QueryBuilders.termQuery("category.keyword", category));
        }
        
        if (minPrice != null && maxPrice != null) {
            boolQuery.filter(QueryBuilders.rangeQuery("price")
                    .gte(minPrice)
                    .lte(maxPrice));
        } else if (minPrice != null) {
            boolQuery.filter(QueryBuilders.rangeQuery("price").gte(minPrice));
        } else if (maxPrice != null) {
            boolQuery.filter(QueryBuilders.rangeQuery("price").lte(maxPrice));
        }
        
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withPageable(pageable)
                .build();
        
        SearchHits<Product> searchHits = elasticsearchOperations.search(searchQuery, Product.class, IndexCoordinates.of(PRODUCT_INDEX));
        return elasticsearchOperations.searchForPage(searchQuery, Product.class, IndexCoordinates.of(PRODUCT_INDEX));
    }

    @Override
    public List<Product> findSimilarProducts(String productId, int limit) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return Collections.emptyList();
        }
        
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .mustNot(QueryBuilders.termQuery("id", productId))
                .should(QueryBuilders.matchQuery("category", product.getCategory()).boost(2.0f))
                .should(QueryBuilders.matchQuery("brand", product.getBrand()).boost(1.5f))
                .should(QueryBuilders.rangeQuery("price")
                        .gte(product.getPrice().multiply(BigDecimal.valueOf(0.8)))
                        .lte(product.getPrice().multiply(BigDecimal.valueOf(1.2))))
                .minimumShouldMatch(1);
        
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withPageable(PageRequest.of(0, limit))
                .build();
        
        SearchHits<Product> searchHits = elasticsearchOperations.search(searchQuery, Product.class, IndexCoordinates.of(PRODUCT_INDEX));
        return searchHits.stream()
                .map(hit -> hit.getContent())
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Long> getCategoryStatistics() {
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchAllQuery())
                .addAggregation(AggregationBuilders.terms("category_stats").field("category.keyword").size(100))
                .withMaxResults(0)
                .build();
        
        SearchHits<Product> searchHits = elasticsearchOperations.search(searchQuery, Product.class, IndexCoordinates.of(PRODUCT_INDEX));
        Terms categoryAgg = searchHits.getAggregations().get("category_stats");
        
        Map<String, Long> result = new HashMap<>();
        for (Terms.Bucket bucket : categoryAgg.getBuckets()) {
            result.put(bucket.getKeyAsString(), bucket.getDocCount());
        }
        return result;
    }

    @Override
    public Map<String, Long> getBrandStatistics() {
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchAllQuery())
                .addAggregation(AggregationBuilders.terms("brand_stats").field("brand.keyword").size(100))
                .withMaxResults(0)
                .build();
        
        SearchHits<Product> searchHits = elasticsearchOperations.search(searchQuery, Product.class, IndexCoordinates.of(PRODUCT_INDEX));
        Terms brandAgg = searchHits.getAggregations().get("brand_stats");
        
        Map<String, Long> result = new HashMap<>();
        for (Terms.Bucket bucket : brandAgg.getBuckets()) {
            result.put(bucket.getKeyAsString(), bucket.getDocCount());
        }
        return result;
    }

    @Override
    public Map<String, Long> getPriceRangeStatistics() {
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchAllQuery())
                .addAggregation(AggregationBuilders.range("price_ranges")
                        .field("price")
                        .addRange("0-100", 0, 100)
                        .addRange("100-500", 100, 500)
                        .addRange("500-1000", 500, 1000)
                        .addRange("1000-5000", 1000, 5000)
                        .addRange("5000+", 5000, Double.MAX_VALUE))
                .withMaxResults(0)
                .build();
        
        SearchHits<Product> searchHits = elasticsearchOperations.search(searchQuery, Product.class, IndexCoordinates.of(PRODUCT_INDEX));
        org.elasticsearch.search.aggregations.bucket.range.Range priceRangeAgg = searchHits.getAggregations().get("price_ranges");
        
        Map<String, Long> result = new HashMap<>();
        for (org.elasticsearch.search.aggregations.bucket.range.Range.Bucket bucket : priceRangeAgg.getBuckets()) {
            result.put(bucket.getKeyAsString(), bucket.getDocCount());
        }
        return result;
    }

    @Override
    public List<String> getSearchSuggestions(String prefix, int limit) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .should(QueryBuilders.prefixQuery("name", prefix))
                .should(QueryBuilders.prefixQuery("brand", prefix))
                .should(QueryBuilders.prefixQuery("category", prefix));
        
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withPageable(PageRequest.of(0, limit))
                .withSourceFilter(org.springframework.data.elasticsearch.core.query.FetchSourceFilter.of(new String[]{"name", "brand", "category"}, null))
                .build();
        
        SearchHits<Product> searchHits = elasticsearchOperations.search(searchQuery, Product.class, IndexCoordinates.of(PRODUCT_INDEX));
        
        Set<String> suggestions = new HashSet<>();
        for (var hit : searchHits) {
            Product product = hit.getContent();
            if (product.getName() != null && product.getName().toLowerCase().startsWith(prefix.toLowerCase())) {
                suggestions.add(product.getName());
            }
            if (product.getBrand() != null && product.getBrand().toLowerCase().startsWith(prefix.toLowerCase())) {
                suggestions.add(product.getBrand());
            }
            if (product.getCategory() != null && product.getCategory().toLowerCase().startsWith(prefix.toLowerCase())) {
                suggestions.add(product.getCategory());
            }
        }
        
        return suggestions.stream().limit(limit).collect(Collectors.toList());
    }

    @Override
    public Product findById(String id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public List<Product> findByIds(List<String> ids) {
        return (List<Product>) productRepository.findAllById(ids);
    }
}
