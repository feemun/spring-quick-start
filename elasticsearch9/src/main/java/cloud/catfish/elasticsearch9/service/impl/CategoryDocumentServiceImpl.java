package cloud.catfish.elasticsearch9.service.impl;

import cloud.catfish.elasticsearch9.dto.CategoryDocumentDto;
import cloud.catfish.elasticsearch9.service.CategoryDocumentService;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryDocumentServiceImpl implements CategoryDocumentService {

    private static final String INDEX_NAME = "category-index";

    private final ElasticsearchClient elasticsearchClient;

    @Override
    public String createIndex() throws IOException {
        elasticsearchClient.indices().create(c -> c
                .index(INDEX_NAME)
                .mappings(m -> m
                        .properties("category_id", p -> p.keyword(k -> k))
                        .properties("category_level", p -> p.integer(i -> i))
                        .properties("category_name", p -> p.text(t -> t.analyzer("ik_max_word").searchAnalyzer("ik_smart")))
                        .properties("category_label", p -> p.text(t -> t.analyzer("ik_max_word").searchAnalyzer("ik_smart")))
                        .properties("level1_id", p -> p.keyword(k -> k))
                        .properties("level1_name", p -> p.text(t -> t.analyzer("ik_max_word").searchAnalyzer("ik_smart")))
                        .properties("level2_id", p -> p.keyword(k -> k))
                        .properties("level2_name", p -> p.text(t -> t.analyzer("ik_max_word").searchAnalyzer("ik_smart")))
                        .properties("level3_id", p -> p.keyword(k -> k))
                        .properties("level3_name", p -> p.text(t -> t.analyzer("ik_max_word").searchAnalyzer("ik_smart")))
                        .properties("level4_id", p -> p.keyword(k -> k))
                        .properties("level4_name", p -> p.text(t -> t.analyzer("ik_max_word").searchAnalyzer("ik_smart")))
                        .properties("level5_id", p -> p.keyword(k -> k))
                        .properties("level5_name", p -> p.text(t -> t.analyzer("ik_max_word").searchAnalyzer("ik_smart")))
                        .properties("level6_id", p -> p.keyword(k -> k))
                        .properties("level6_name", p -> p.text(t -> t.analyzer("ik_max_word").searchAnalyzer("ik_smart")))
                )
        );
        return "Index created with mapping";
    }

    @Override
    public String deleteIndex() throws IOException {
        elasticsearchClient.indices().delete(d -> d
                .index(INDEX_NAME)
        );
        return "Index deleted";
    }

    @Override
    public void bulkCreateDocuments(List<CategoryDocumentDto> documents) throws IOException {
        if (documents.isEmpty()) return;

        BulkRequest.Builder br = new BulkRequest.Builder();

        for (CategoryDocumentDto document : documents) {
            br.operations(op -> op
                .index(idx -> idx
                    .index(INDEX_NAME)
                    .id(document.categoryId())
                    .document(document)
                )
            );
        }

        BulkResponse result = elasticsearchClient.bulk(br.build());

        if (result.errors()) {
            log.error("Bulk had errors");
            for (BulkResponseItem item : result.items()) {
                if (item.error() != null) {
                    log.error("Error creating document with id: {}, message: {}", item.id(), item.error().reason());
                }
            }
        }
    }

    @Override
    public List<CategoryDocumentDto> searchByCategoryName(String categoryName) throws IOException {
        SearchResponse<CategoryDocumentDto> response = elasticsearchClient.search(s -> s
                .index(INDEX_NAME)
                .query(q -> q
                        .match(m -> m
                                .field("category_name") // Use snake_case field name here
                                .query(categoryName)
                        )
                ),
                CategoryDocumentDto.class
        );

        List<CategoryDocumentDto> result = new ArrayList<>();
        for (Hit<CategoryDocumentDto> hit : response.hits().hits()) {
            result.add(hit.source());
        }
        return result;
    }
}
