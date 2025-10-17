package cloud.catfish.es.example;

import cloud.catfish.es.config.EnableEsMappers;
import cloud.catfish.es.mapper.SampleDocumentMapper;
import cloud.catfish.es.model.SampleDocument;
import cloud.catfish.es.template.EsSqlTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import java.util.Map;

/**
 * Elasticsearch Mapper使用示例
 * 展示如何在Spring Boot应用中使用ES Mapper
 */
@Slf4j
@SpringBootApplication
@EnableEsMappers(basePackages = "cloud.catfish.es.mapper")
public class EsMapperExample implements CommandLineRunner {

    @Autowired
    private SampleDocumentMapper sampleDocumentMapper;

    public static void main(String[] args) {
        SpringApplication.run(EsMapperExample.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("=== Elasticsearch Mapper Example ===");

        try {
            // 1. 基础查询示例
            demonstrateBasicQueries();

            // 2. 分页查询示例
            demonstratePaginationQueries();

            // 3. 聚合查询示例
            demonstrateAggregationQueries();

            // 4. 复合查询示例
            demonstrateComplexQueries();

        } catch (Exception e) {
            log.error("Error during demonstration", e);
        }
    }

    /**
     * 基础查询示例
     */
    private void demonstrateBasicQueries() {
        log.info("--- Basic Queries ---");

        // 根据分类查询（DSL）
        List<SampleDocument> techDocs = sampleDocumentMapper.findByCategory("technology");
        log.info("Found {} technology documents", techDocs.size());

        // 根据标题关键词搜索（SQL）
        List<SampleDocument> searchResults = sampleDocumentMapper.findByTitleKeyword("spring");
        log.info("Found {} documents with 'spring' in title", searchResults.size());

        // 查询所有文档
        List<SampleDocument> allDocs = sampleDocumentMapper.findAll("sample_documents");
        log.info("Total documents: {}", allDocs.size());

        // 计数查询
        long totalCount = sampleDocumentMapper.count("sample_documents");
        log.info("Total document count: {}", totalCount);

        // 按分类计数
        long techCount = sampleDocumentMapper.countByCategory("technology");
        log.info("Technology documents count: {}", techCount);
    }

    /**
     * 分页查询示例
     */
    private void demonstratePaginationQueries() {
        log.info("--- Pagination Queries ---");

        // 分页查询指定分类的文档
        EsSqlTemplate.PageResult<SampleDocument> pageResult = 
            sampleDocumentMapper.findByCategoryWithPage("technology", 0, 5);
        
        log.info("Page result - Total: {}, Current page size: {}", 
            pageResult.getTotal(), pageResult.getData().size());

        // 基础分页查询
        EsSqlTemplate.PageResult<SampleDocument> basicPage = 
            sampleDocumentMapper.findByPage("sample_documents", 0, 10);
        
        log.info("Basic page result - Total: {}, Page size: {}", 
            basicPage.getTotal(), basicPage.getData().size());
    }

    /**
     * 聚合查询示例
     */
    private void demonstrateAggregationQueries() {
        log.info("--- Aggregation Queries ---");

        // 按分类统计
        Map<String, Object> categoryStats = sampleDocumentMapper.getCategoryStatistics();
        log.info("Category statistics: {}", categoryStats);

        // 复杂聚合查询 - 分类和时间统计
        Map<String, Object> complexStats = sampleDocumentMapper.getCategoryTimeStatistics();
        log.info("Complex statistics: {}", complexStats);
    }

    /**
     * 复合查询示例
     */
    private void demonstrateComplexQueries() {
        log.info("--- Complex Queries ---");

        // 复合查询 - 分类和标题
        List<SampleDocument> complexResults = 
            sampleDocumentMapper.findByCategoryAndTitle("technology", "spring boot");
        log.info("Found {} documents matching category and title", complexResults.size());

        // 多字段搜索
        List<SampleDocument> multiFieldResults = 
            sampleDocumentMapper.searchMultiFields("elasticsearch");
        log.info("Multi-field search found {} documents", multiFieldResults.size());

        // 高亮搜索
        List<Map<String, Object>> highlightResults = 
            sampleDocumentMapper.searchWithHighlight("spring");
        log.info("Highlight search found {} results", highlightResults.size());

        // 获取热门文档
        List<SampleDocument> popularDocs = sampleDocumentMapper.getPopularDocuments();
        log.info("Found {} popular documents", popularDocs.size());

        // 范围查询
        List<SampleDocument> rangeResults = 
            sampleDocumentMapper.findByTimeRange("2023-01-01", "2023-12-31");
        log.info("Time range query found {} documents", rangeResults.size());
    }
}