package cloud.catfish.elasticsearch9.service;

import cloud.catfish.elasticsearch9.dto.PlaceDocumentDto;
import java.io.IOException;
import java.util.List;

public interface PlaceDocumentService {
    
    String createIndex() throws IOException;
    
    String deleteIndex() throws IOException;
    
    void bulkCreateDocuments(List<PlaceDocumentDto> documents) throws IOException;
}
