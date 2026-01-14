package cloud.catfish.elasticsearch9.service.impl;

import cloud.catfish.elasticsearch9.dto.MyDocumentDto;
import cloud.catfish.elasticsearch9.service.MyDocumentService;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MyDocumentServiceImpl implements MyDocumentService {
    
    private static final Logger log = LoggerFactory.getLogger(MyDocumentServiceImpl.class);

    private static final String INDEX_NAME = "my-index";

    private final ElasticsearchClient elasticsearchClient;

    @Override
    public String createIndex() throws IOException {
        elasticsearchClient.indices().create(c -> c
                .index(INDEX_NAME)
                .mappings(m -> m
                        .properties("id", p -> p.text(t -> t.fields("keyword", k -> k.keyword(kw -> kw.ignoreAbove(256)))))
                        .properties("title", p -> p.text(t -> t.fields("keyword", k -> k.keyword(kw -> kw.ignoreAbove(256)))))
                        .properties("description", p -> p.text(t -> t.fields("keyword", k -> k.keyword(kw -> kw.ignoreAbove(256)))))
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
    public String createDocument(MyDocumentDto document) throws IOException {
        elasticsearchClient.index(i -> i
                .index(INDEX_NAME)
                .id(document.id())
                .document(document)
        );
        return "Document created";
    }

    @Override
    public MyDocumentDto getDocument(String id) throws IOException {
        return elasticsearchClient.get(g -> g
                .index(INDEX_NAME)
                .id(id),
                MyDocumentDto.class
        ).source();
    }

    @Override
    public String updateDocument(MyDocumentDto document) throws IOException {
        elasticsearchClient.update(u -> u
                .index(INDEX_NAME)
                .id(document.id())
                .doc(document),
                MyDocumentDto.class
        );
        return "Document updated";
    }

    @Override
    public String deleteDocument(String id) throws IOException {
        elasticsearchClient.delete(d -> d
                .index(INDEX_NAME)
                .id(id)
        );
        return "Document deleted";
    }

    @Override
    public void bulkCreateDocuments(List<MyDocumentDto> documents) throws IOException {
        if (documents.isEmpty()) return;

        BulkRequest.Builder br = new BulkRequest.Builder();

        for (MyDocumentDto document : documents) {
            br.operations(op -> op
                .index(idx -> idx
                    .index(INDEX_NAME)
                    .id(document.id())
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
    public List<MyDocumentDto> searchByTitle(String title) throws IOException {
        SearchResponse<MyDocumentDto> response = elasticsearchClient.search(s -> s
                .index(INDEX_NAME)
                .query(q -> q
                        .match(m -> m
                                .field("title")
                                .query(title)
                        )
                ),
                MyDocumentDto.class
        );

        List<MyDocumentDto> result = new ArrayList<>();
        for (Hit<MyDocumentDto> hit : response.hits().hits()) {
            result.add(hit.source());
        }
        return result;
    }
}
