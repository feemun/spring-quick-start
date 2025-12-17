package cloud.catfish.elasticsearch9.service.impl;

import cloud.catfish.elasticsearch9.service.NetworkLogService;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NetworkLogServiceImpl implements NetworkLogService {

    private static final String INDEX_NAME = "network-log-index";
    private final ElasticsearchClient elasticsearchClient;

    @Override
    public String createIndex() throws IOException {
        elasticsearchClient.indices().create(c -> c
                .index(INDEX_NAME)
                .mappings(m -> m
                        .properties("id", p -> p.keyword(k -> k))
                        .properties("src_ip", p -> p.ip(i -> i))
                        .properties("dest_ip", p -> p.ip(i -> i))
                        .properties("tags", p -> p.keyword(k -> k))
                        .properties("src_cidr", p -> p.keyword(k -> k))
                        .properties("src_station_id", p -> p.keyword(k -> k))
                        .properties("src_station_name", p -> p.text(t -> t.analyzer("ik_max_word").searchAnalyzer("ik_smart")))
                        .properties("dest_cidr", p -> p.keyword(k -> k))
                        .properties("dest_station_id", p -> p.keyword(k -> k))
                        .properties("dest_station_name", p -> p.text(t -> t.analyzer("ik_max_word").searchAnalyzer("ik_smart")))
                )
        );
        return "Index created";
    }

    @Override
    public String deleteIndex() throws IOException {
        elasticsearchClient.indices().delete(d -> d.index(INDEX_NAME));
        return "Index deleted";
    }

    @Override
    public void createDocument(Map<String, Object> document) throws IOException {
        String id = (String) document.get("id");
        if (id == null) {
            throw new IllegalArgumentException("Document must have an 'id' field");
        }
        elasticsearchClient.index(i -> i
                .index(INDEX_NAME)
                .id(id)
                .document(document)
        );
    }
}
