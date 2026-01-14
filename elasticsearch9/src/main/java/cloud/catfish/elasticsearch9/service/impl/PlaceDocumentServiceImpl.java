package cloud.catfish.elasticsearch9.service.impl;

import cloud.catfish.elasticsearch9.dto.PlaceDocumentDto;
import cloud.catfish.elasticsearch9.service.PlaceDocumentService;
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
public class PlaceDocumentServiceImpl implements PlaceDocumentService {

    private static final String INDEX_NAME = "place-index";

    private final ElasticsearchClient elasticsearchClient;

    @Override
    public String createIndex() throws IOException {
        elasticsearchClient.indices().create(c -> c
                .index(INDEX_NAME)
                .mappings(m -> m
                        .properties("fsq_id", p -> p.keyword(k -> k))
                        .properties("name", p -> p.text(t -> t.analyzer("ik_max_word").searchAnalyzer("ik_smart")))
                        .properties("latitude", p -> p.double_(d -> d))
                        .properties("longitude", p -> p.double_(d -> d))
                        .properties("address", p -> p.text(t -> t.analyzer("ik_max_word").searchAnalyzer("ik_smart")))
                        .properties("city", p -> p.text(t -> t.analyzer("ik_max_word").searchAnalyzer("ik_smart")))
                        .properties("region", p -> p.text(t -> t.analyzer("ik_max_word").searchAnalyzer("ik_smart")))
                        .properties("postcode", p -> p.keyword(k -> k))
                        .properties("admin_region", p -> p.text(t -> t.analyzer("ik_max_word").searchAnalyzer("ik_smart")))
                        .properties("post_town", p -> p.text(t -> t.analyzer("ik_max_word").searchAnalyzer("ik_smart")))
                        .properties("po_box", p -> p.text(t -> t.analyzer("ik_max_word").searchAnalyzer("ik_smart")))
                        .properties("country", p -> p.text(t -> t.analyzer("ik_max_word").searchAnalyzer("ik_smart")))
                        .properties("created_date", p -> p.date(d -> d.format("yyyy/MM/dd HH:mm:ss||yyyy/MM/dd||strict_date_optional_time||epoch_millis")))
                        .properties("refreshed_date", p -> p.date(d -> d.format("yyyy/MM/dd HH:mm:ss||yyyy/MM/dd||strict_date_optional_time||epoch_millis")))
                        .properties("closed_date", p -> p.date(d -> d.format("yyyy/MM/dd HH:mm:ss||yyyy/MM/dd||strict_date_optional_time||epoch_millis")))
                        .properties("phone", p -> p.keyword(k -> k))
                        .properties("website", p -> p.text(t -> t.analyzer("ik_max_word").searchAnalyzer("ik_smart")))
                        .properties("email", p -> p.text(t -> t.analyzer("ik_max_word").searchAnalyzer("ik_smart")))
                        .properties("facebook_id", p -> p.text(t -> t.analyzer("ik_max_word").searchAnalyzer("ik_smart")))
                        .properties("instagram", p -> p.text(t -> t.analyzer("ik_max_word").searchAnalyzer("ik_smart")))
                        .properties("twitter", p -> p.text(t -> t.analyzer("ik_max_word").searchAnalyzer("ik_smart")))
                        .properties("category_ids", p -> p.text(t -> t.analyzer("ik_max_word").searchAnalyzer("ik_smart")))
                        .properties("category_labels", p -> p.text(t -> t.analyzer("ik_max_word").searchAnalyzer("ik_smart")))
                        .properties("geometry", p -> p.keyword(k -> k))
                        .properties("bounds", p -> p.text(t -> t.analyzer("ik_max_word").searchAnalyzer("ik_smart")))
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
    public void bulkCreateDocuments(List<PlaceDocumentDto> documents) throws IOException {
        if (documents.isEmpty()) return;

        BulkRequest.Builder br = new BulkRequest.Builder();

        for (PlaceDocumentDto document : documents) {
            br.operations(op -> op
                .index(idx -> idx
                    .index(INDEX_NAME)
                    .id(document.fsqId())
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
