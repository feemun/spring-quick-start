package cloud.catfish.elasticsearch9.service.impl;

import cloud.catfish.elasticsearch9.dto.NetworkLogDto;
import cloud.catfish.elasticsearch9.dto.NetworkLogStatDto;
import cloud.catfish.elasticsearch9.service.NetworkLogService;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

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
                        .properties("create_time", p -> p.date(d -> d.format("yyyy-MM-dd HH:mm:ss||strict_date_optional_time||epoch_millis")))
                        .properties("bytes", p -> p.long_(l -> l))
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
    public void createDocument(NetworkLogDto document) throws IOException {
        String id = document.getId();
        if (id == null) {
            throw new IllegalArgumentException("Document must have an 'id' field");
        }

        if (document.getCreateTime() == null) {
            log.warn("create_time is missing, packet discarded");
            return;
        }

        elasticsearchClient.index(i -> i
                .index(INDEX_NAME)
                .id(id)
                .document(document)
        );
    }

    @Override
    public List<NetworkLogStatDto> getStatistics(String field) throws IOException {
        String finalField = field;
        if ("srcIp".equals(field)) {
            finalField = "src_ip";
        } else if ("destIp".equals(field)) {
            finalField = "dest_ip";
        }

        String searchField = finalField;
        SearchResponse<Void> response = elasticsearchClient.search(s -> s
                        .index(INDEX_NAME)
                        .size(0)
                        .query(q -> q
                                .range(r -> r
                                        .date(d -> d
                                                .field("create_time")
                                                .gt("now-1000d/d")
                                        )
                                )
                        )
                        .aggregations("stats", a -> a
                                .terms(t -> t.field(searchField))
                                .aggregations("total_bytes", sub -> sub.sum(m -> m.field("bytes")))
                                .aggregations("first_created", sub -> sub.min(m -> m.field("create_time")))
                                .aggregations("last_created", sub -> sub.max(m -> m.field("create_time")))
                        )
                , Void.class
        );

        return extractStats(response.aggregations().get("stats").sterms().buckets().array());
    }

    private List<NetworkLogStatDto> extractStats(List<StringTermsBucket> buckets) {
        List<NetworkLogStatDto> stats = new ArrayList<>();
        for (StringTermsBucket bucket : buckets) {
            long firstMillis = (long) bucket.aggregations().get("first_created").min().value();
            long lastMillis = (long) bucket.aggregations().get("last_created").max().value();

            NetworkLogStatDto dto = NetworkLogStatDto.builder()
                    .key(bucket.key().stringValue())
                    .count(bucket.docCount())
                    .totalBytes(bucket.aggregations().get("total_bytes").sum().value())
                    .firstCreated(LocalDateTime.ofInstant(Instant.ofEpochMilli(firstMillis), ZoneId.systemDefault()))
                    .lastCreated(LocalDateTime.ofInstant(Instant.ofEpochMilli(lastMillis), ZoneId.systemDefault()))
                    .build();
            stats.add(dto);
        }
        return stats;
    }
}
