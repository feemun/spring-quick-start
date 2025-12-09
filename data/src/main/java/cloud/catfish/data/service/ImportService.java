package cloud.catfish.data.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface ImportService {
    void importExcel(MultipartFile file) throws IOException;
    void importCsv(MultipartFile file) throws IOException;
    void importCategoryCsv(MultipartFile file) throws IOException;
    void importPlaceCsv(MultipartFile file) throws IOException;
}
