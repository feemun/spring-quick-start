package cloud.catfish.data.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class ImportDataDto {
    @ExcelProperty("ID")
    private String id;
    @ExcelProperty("Title")
    private String title;
    @ExcelProperty("Description")
    private String description;
}
