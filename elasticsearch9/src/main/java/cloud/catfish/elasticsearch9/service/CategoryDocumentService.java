package cloud.catfish.elasticsearch9.service;

import cloud.catfish.elasticsearch9.model.CategoryDocument;
import java.io.IOException;
import java.util.List;

public interface CategoryDocumentService {
    
    String createIndex() throws IOException;
    
    String deleteIndex() throws IOException;
    
    void bulkCreateDocuments(List<CategoryDocument> documents) throws IOException;
}
