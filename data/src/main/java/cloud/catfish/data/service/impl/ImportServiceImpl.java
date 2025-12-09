package cloud.catfish.data.service.impl;

import cloud.catfish.data.dto.ImportDataDto;
import cloud.catfish.data.service.ImportService;
import cloud.catfish.elasticsearch9.model.CategoryDocument;
import cloud.catfish.elasticsearch9.model.MyDocument;
import cloud.catfish.elasticsearch9.service.CategoryDocumentService;
import cloud.catfish.elasticsearch9.service.MyDocumentService;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImportServiceImpl implements ImportService {

    private final MyDocumentService myDocumentService;
    private final CategoryDocumentService categoryDocumentService;
    private static final int BATCH_COUNT = 3000;

    @Override
    public void importExcel(MultipartFile file) throws IOException {
        EasyExcel.read(file.getInputStream(), ImportDataDto.class, new ReadListener<ImportDataDto>() {
            private List<MyDocument> cachedDataList = new ArrayList<>(BATCH_COUNT);

            @Override
            public void invoke(ImportDataDto data, AnalysisContext context) {
                MyDocument document = new MyDocument();
                document.setId(data.getId());
                document.setTitle(data.getTitle());
                document.setDescription(data.getDescription());
                cachedDataList.add(document);
                
                if (cachedDataList.size() >= BATCH_COUNT) {
                    saveData();
                }
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
                saveData();
            }

            private void saveData() {
                try {
                    myDocumentService.bulkCreateDocuments(cachedDataList);
                } catch (IOException e) {
                    log.error("Failed to batch save documents", e);
                } finally {
                    cachedDataList = new ArrayList<>(BATCH_COUNT);
                }
            }
        }).sheet().doRead();
    }

    @Override
    public void importCsv(MultipartFile file) throws IOException {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).setIgnoreHeaderCase(true).setTrim(true).build())) {

            List<MyDocument> batchDocs = new ArrayList<>(BATCH_COUNT);
            
            for (CSVRecord csvRecord : csvParser) {
                MyDocument document = new MyDocument();
                // 容错处理：确保列存在再获取，避免报错
                if (csvRecord.isMapped("ID")) document.setId(csvRecord.get("ID"));
                if (csvRecord.isMapped("Title")) document.setTitle(csvRecord.get("Title"));
                if (csvRecord.isMapped("Description")) document.setDescription(csvRecord.get("Description"));
                
                batchDocs.add(document);

                if (batchDocs.size() >= BATCH_COUNT) {
                    myDocumentService.bulkCreateDocuments(batchDocs);
                    batchDocs.clear();
                }
            }
            // 处理剩余数据
            if (!batchDocs.isEmpty()) {
                myDocumentService.bulkCreateDocuments(batchDocs);
            }
        }
    }

    @Override
    public void importCategoryCsv(MultipartFile file) throws IOException {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).setIgnoreHeaderCase(true).setTrim(true).build())) {

            List<CategoryDocument> batchDocs = new ArrayList<>(BATCH_COUNT);
            
            for (CSVRecord csvRecord : csvParser) {
                CategoryDocument document = new CategoryDocument();
                
                if (csvRecord.isMapped("类别ID")) document.setCategoryId(csvRecord.get("类别ID"));
                if (csvRecord.isMapped("类别层级")) {
                    String levelStr = csvRecord.get("类别层级");
                    if (levelStr != null && !levelStr.isBlank()) {
                        try {
                            document.setCategoryLevel(Integer.parseInt(levelStr));
                        } catch (NumberFormatException e) {
                            log.warn("Invalid category level: {}", levelStr);
                        }
                    }
                }
                if (csvRecord.isMapped("类别名称")) document.setCategoryName(csvRecord.get("类别名称"));
                if (csvRecord.isMapped("类别标签")) document.setCategoryLabel(csvRecord.get("类别标签"));
                
                if (csvRecord.isMapped("一级类别ID")) document.setLevel1Id(csvRecord.get("一级类别ID"));
                if (csvRecord.isMapped("一级类别名称")) document.setLevel1Name(csvRecord.get("一级类别名称"));
                
                if (csvRecord.isMapped("二级类别ID")) document.setLevel2Id(csvRecord.get("二级类别ID"));
                if (csvRecord.isMapped("二级类别名称")) document.setLevel2Name(csvRecord.get("二级类别名称"));
                
                if (csvRecord.isMapped("三级类别ID")) document.setLevel3Id(csvRecord.get("三级类别ID"));
                if (csvRecord.isMapped("三级类别名称")) document.setLevel3Name(csvRecord.get("三级类别名称"));
                
                if (csvRecord.isMapped("四级类别ID")) document.setLevel4Id(csvRecord.get("四级类别ID"));
                if (csvRecord.isMapped("四级类别名称")) document.setLevel4Name(csvRecord.get("四级类别名称"));
                
                if (csvRecord.isMapped("五级类别ID")) document.setLevel5Id(csvRecord.get("五级类别ID"));
                if (csvRecord.isMapped("五级类别名称")) document.setLevel5Name(csvRecord.get("五级类别名称"));
                
                if (csvRecord.isMapped("六级类别ID")) document.setLevel6Id(csvRecord.get("六级类别ID"));
                if (csvRecord.isMapped("六级类别名称")) document.setLevel6Name(csvRecord.get("六级类别名称"));

                batchDocs.add(document);

                if (batchDocs.size() >= BATCH_COUNT) {
                    categoryDocumentService.bulkCreateDocuments(batchDocs);
                    batchDocs.clear();
                }
            }
            
            if (!batchDocs.isEmpty()) {
                categoryDocumentService.bulkCreateDocuments(batchDocs);
            }
        }
    }
}
