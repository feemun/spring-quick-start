package cloud.catfish.elasticsearch9.service;

import cloud.catfish.elasticsearch9.dto.MyDocumentDto;
import java.io.IOException;
import java.util.List;

public interface MyDocumentService {
    
    String createIndex() throws IOException;
    
    String deleteIndex() throws IOException;
    
    String createDocument(MyDocumentDto document) throws IOException;
    
    MyDocumentDto getDocument(String id) throws IOException;
    
    String updateDocument(MyDocumentDto document) throws IOException;
    
    String deleteDocument(String id) throws IOException;
    
    void bulkCreateDocuments(List<MyDocumentDto> documents) throws IOException;

    List<MyDocumentDto> searchByTitle(String title) throws IOException;
}
