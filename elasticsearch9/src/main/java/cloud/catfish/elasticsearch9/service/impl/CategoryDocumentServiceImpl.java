package cloud.catfish.elasticsearch9.service.impl;

import cloud.catfish.elasticsearch9.model.CategoryDocument;
import cloud.catfish.elasticsearch9.service.CategoryDocumentService;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
                        .properties("categoryId", p -> p.keyword(k -> k))
                        .properties("categoryLevel", p -> p.integer(i -> i))
                        .properties("categoryName", p -> p.text(t -> t.analyzer("ik_max_word").searchAnalyzer("ik_smart")))
                        .properties("categoryLabel", p -> p.text(t -> t.analyzer("ik_max_word").searchAnalyzer("ik_smart")))
                        .properties("level1Id", p -> p.keyword(k -> k))
                        .properties("level1Name", p -> p.text(t -> t.analyzer("ik_max_word").searchAnalyzer("ik_smart")))
                        .properties("level2Id", p -> p.keyword(k -> k))
                        .properties("level2Name", p -> p.text(t -> t.analyzer("ik_max_word").searchAnalyzer("ik_smart")))
                        .properties("level3Id", p -> p.keyword(k -> k))
                        .properties("level3Name", p -> p.text(t -> t.analyzer("ik_max_word").searchAnalyzer("ik_smart")))
                        .properties("level4Id", p -> p.keyword(k -> k))
                        .properties("level4Name", p -> p.text(t -> t.analyzer("ik_max_word").searchAnalyzer("ik_smart")))
                        .properties("level5Id", p -> p.keyword(k -> k))
                        .properties("level5Name", p -> p.text(t -> t.analyzer("ik_max_word").searchAnalyzer("ik_smart")))
                        .properties("level6Id", p -> p.keyword(k -> k))
                        .properties("level6Name", p -> p.text(t -> t.analyzer("ik_max_word").searchAnalyzer("ik_smart")))
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
    public void bulkCreateDocuments(List<CategoryDocument> documents) throws IOException {
        if (documents.isEmpty()) return;

        BulkRequest.Builder br = new BulkRequest.Builder();

        for (CategoryDocument document : documents) {
            br.operations(op -> op
                .index(idx -> idx
                    .index(INDEX_NAME)
                    .id(document.getCategoryId())
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
}
