package cloud.catfish.elasticsearch9.service;

import cloud.catfish.elasticsearch9.dto.NetworkLogDto;
import cloud.catfish.elasticsearch9.dto.NetworkLogStatDto;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface NetworkLogService {
    String createIndex() throws IOException;
    String deleteIndex() throws IOException;
    void createDocument(NetworkLogDto document) throws IOException;

    List<NetworkLogStatDto> getStatistics(String field) throws IOException;
}
