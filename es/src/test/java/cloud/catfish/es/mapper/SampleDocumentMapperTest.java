package cloud.catfish.es.mapper;

import cloud.catfish.es.config.EnableEsMappers;
import cloud.catfish.es.model.SampleDocument;
import cloud.catfish.es.template.EsSqlTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SampleDocumentMapper单元测试
 * 验证Mapper功能的正确性
 */
@SpringBootTest(classes = {SampleDocumentMapperTest.TestConfiguration.class})
@TestPropertySource(properties = {
    "es.host=localhost",
    "es.port=9200",
    "es.scheme=http"
})
class SampleDocumentMapperTest {

    @Autowired
    private SampleDocumentMapper sampleDocumentMapper;

    @Test
    void testFindByCategory() {
        // 测试根据分类查询
        List<SampleDocument> results = sampleDocumentMapper.findByCategory("technology");
        assertNotNull(results);
        // 注意：实际测试需要有测试数据
    }

    @Test
    void testFindByTitleKeyword() {
        // 测试根据标题关键词搜索
        List<SampleDocument> results = sampleDocumentMapper.findByTitleKeyword("spring");
        assertNotNull(results);
    }

    @Test
    void testFindByCategoryAndTitle() {
        // 测试复合查询
        List<SampleDocument> results = sampleDocumentMapper.findByCategoryAndTitle("technology", "spring");
        assertNotNull(results);
    }

    @Test
    void testFindByCategoryWithPage() {
        // 测试分页查询
        EsSqlTemplate.PageResult<SampleDocument> pageResult = 
            sampleDocumentMapper.findByCategoryWithPage("technology", 0, 5);
        
        assertNotNull(pageResult);
        assertNotNull(pageResult.getData());
        assertTrue(pageResult.getTotal() >= 0);
    }

    @Test
    void testGetCategoryStatistics() {
        // 测试聚合查询
        Map<String, Object> stats = sampleDocumentMapper.getCategoryStatistics();
        assertNotNull(stats);
    }

    @Test
    void testSearchMultiFields() {
        // 测试多字段搜索
        List<SampleDocument> results = sampleDocumentMapper.searchMultiFields("elasticsearch");
        assertNotNull(results);
    }

    @Test
    void testSearchWithHighlight() {
        // 测试高亮搜索
        List<Map<String, Object>> results = sampleDocumentMapper.searchWithHighlight("spring");
        assertNotNull(results);
    }

    @Test
    void testCountByCategory() {
        // 测试计数查询
        long count = sampleDocumentMapper.countByCategory("technology");
        assertTrue(count >= 0);
    }

    @Test
    void testGetPopularDocuments() {
        // 测试获取热门文档
        List<SampleDocument> results = sampleDocumentMapper.getPopularDocuments();
        assertNotNull(results);
    }

    @Test
    void testFindByTimeRange() {
        // 测试范围查询
        List<SampleDocument> results = sampleDocumentMapper.findByTimeRange("2023-01-01", "2023-12-31");
        assertNotNull(results);
    }

    @Test
    void testBaseMapperMethods() {
        // 测试基础Mapper方法
        String index = "sample_documents";
        
        // 测试查询所有
        List<SampleDocument> allDocs = sampleDocumentMapper.findAll(index);
        assertNotNull(allDocs);
        
        // 测试计数
        long count = sampleDocumentMapper.count(index);
        assertTrue(count >= 0);
        
        // 测试分页查询
        EsSqlTemplate.PageResult<SampleDocument> pageResult = 
            sampleDocumentMapper.findByPage(index, 0, 10);
        assertNotNull(pageResult);
        assertNotNull(pageResult.getData());
    }

    /**
     * 测试配置类
     */
    @org.springframework.boot.test.context.TestConfiguration
    @EnableEsMappers(basePackages = "cloud.catfish.es.mapper")
    static class TestConfiguration {
        // 测试配置
    }
}