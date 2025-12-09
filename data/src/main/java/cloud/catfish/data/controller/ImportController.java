package cloud.catfish.data.controller;

import cloud.catfish.common.api.R;
import cloud.catfish.data.service.ImportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "ImportController", description = "数据导入管理")
@RestController
@RequestMapping("/import")
@RequiredArgsConstructor
public class ImportController {

    private final ImportService importService;

    @Operation(summary = "导入Excel数据到ES")
    @PostMapping("/excel")
    public R<String> importExcel(@RequestParam("file") MultipartFile file) throws IOException {
        importService.importExcel(file);
        return R.ok("Excel导入成功");
    }

    @Operation(summary = "导入CSV数据到ES")
    @PostMapping("/csv")
    public R<String> importCsv(@RequestParam("file") MultipartFile file) throws IOException {
        importService.importCsv(file);
        return R.ok("CSV导入成功");
    }

    @Operation(summary = "导入类别CSV数据到ES")
    @PostMapping("/category/csv")
    public R<String> importCategoryCsv(@RequestParam("file") MultipartFile file) throws IOException {
        importService.importCategoryCsv(file);
        return R.ok("类别CSV导入成功");
    }

    @Operation(summary = "导入地点CSV数据到ES")
    @PostMapping("/place/csv")
    public R<String> importPlaceCsv(@RequestParam("file") MultipartFile file) throws IOException {
        importService.importPlaceCsv(file);
        return R.ok("地点CSV导入成功");
    }
}
