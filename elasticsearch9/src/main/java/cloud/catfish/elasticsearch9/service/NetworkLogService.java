package cloud.catfish.elasticsearch9.service;

import cloud.catfish.elasticsearch9.model.NetworkLogDocument;
import java.io.IOException;

public interface NetworkLogService {
    String createIndex() throws IOException;
    String deleteIndex() throws IOException;
    void createDocument(NetworkLogDocument document) throws IOException;
}
