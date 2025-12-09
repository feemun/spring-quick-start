//package cloud.catfish.elasticsearch9;
//
//import cloud.catfish.elasticsearch9.util.ElasticsearchHelper;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Component
//public class ElasticsearchHelperRunner implements CommandLineRunner {
//
//    private final ElasticsearchHelper helper;
//
//    public ElasticsearchHelperRunner(ElasticsearchHelper helper) {
//        this.helper = helper;
//    }
//
//    @Override
//    public void run(String... args) throws Exception {
//        // 1️⃣ ES|QL 查询
//        String sql = "SELECT userId, userName, age FROM users WHERE age > :minAge";
//        Map<String, Object> params = new HashMap<>();
//        params.put("minAge", 20);
//        List<Map<String, Object>> sqlResult = helper.sqlQuery(sql, params, 500);
//        sqlResult.forEach(System.out::println);
//
//        // 2️⃣ 查询 DSL
//        String queryJson = "{ \"match_all\": {} }";
//        helper.queryDsl("users", queryJson).hits().hits().forEach(hit -> System.out.println(hit.source()));
//
//        // 3️⃣ Index 单条
//        Map<String,Object> doc = new HashMap<>();
//        doc.put("userId", "1001");
//        doc.put("userName", "Alice");
//        doc.put("age", 25);
//        helper.index("users", "1001", doc);
//
//        // 4️⃣ Bulk 批量
//        List<Map<String,Object>> bulkDocs = List.of(
//                Map.of("userId","1002","userName","Bob","age",30),
//                Map.of("userId","1003","userName","Carol","age",28)
//        );
//        helper.bulkIndex("users", bulkDocs);
//
//        // 5️⃣ 自动创建索引
//        String mappingJson = "{ \"properties\": { \"userId\": {\"type\":\"keyword\"}, \"userName\": {\"type\":\"text\"}, \"age\": {\"type\":\"integer\"} } }";
//        helper.createIndexIfNotExists("users", mappingJson);
//    }
//}
