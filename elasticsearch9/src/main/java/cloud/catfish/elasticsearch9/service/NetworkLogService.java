package cloud.catfish.elasticsearch9.service;

import java.io.IOException;
import java.util.Map;

public interface NetworkLogService {
    String createIndex() throws IOException;
    String deleteIndex() throws IOException;
    void createDocument(Map<String, Object> document) throws IOException;
}
