package cloud.catfish.elasticsearch9.service;

import cloud.catfish.elasticsearch9.model.MyDocument;
import java.io.IOException;
import java.util.List;

public interface MyDocumentService {
    
    String createIndex() throws IOException;
    
    String deleteIndex() throws IOException;
    
    String createDocument(MyDocument document) throws IOException;
    
    MyDocument getDocument(String id) throws IOException;
    
    String updateDocument(MyDocument document) throws IOException;
    
    String deleteDocument(String id) throws IOException;
    
    List<MyDocument> searchByTitle(String title) throws IOException;
}
