package cloud.catfish.elasticsearch9.service;

import cloud.catfish.elasticsearch9.dto.CategoryDocumentDto;
import java.io.IOException;
import java.util.List;

public interface CategoryDocumentService {
    
    String createIndex() throws IOException;
    
    String deleteIndex() throws IOException;
    
    void bulkCreateDocuments(List<CategoryDocumentDto> documents) throws IOException;

    List<CategoryDocumentDto> searchByCategoryName(String categoryName) throws IOException;
}
