package cloud.catfish.admin.controller;

import cloud.catfish.common.api.R;
import cloud.catfish.elasticsearch9.model.MyDocument;
import cloud.catfish.elasticsearch9.service.MyDocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/es/document")
@Tag(name = "MyDocumentController", description = "Elasticsearch Document Management")
public class MyDocumentController {

    @Autowired
    private MyDocumentService myDocumentService;

    @Operation(summary = "Create Index")
    @PostMapping("/index/create")
    public R<String> createIndex() {
        try {
            String result = myDocumentService.createIndex();
            return R.ok(result);
        } catch (IOException e) {
            return R.failed("Failed to create index: " + e.getMessage());
        }
    }

    @Operation(summary = "Delete Index")
    @PostMapping("/index/delete")
    public R<String> deleteIndex() {
        try {
            String result = myDocumentService.deleteIndex();
            return R.ok(result);
        } catch (IOException e) {
            return R.failed("Failed to delete index: " + e.getMessage());
        }
    }

    @Operation(summary = "Create Document")
    @PostMapping("/create")
    public R<String> createDocument(@RequestBody MyDocument document) {
        try {
            String result = myDocumentService.createDocument(document);
            return R.ok(result);
        } catch (IOException e) {
            return R.failed("Failed to create document: " + e.getMessage());
        }
    }

    @Operation(summary = "Get Document by ID")
    @GetMapping("/{id}")
    public R<MyDocument> getDocument(@PathVariable String id) {
        try {
            MyDocument document = myDocumentService.getDocument(id);
            return R.ok(document);
        } catch (IOException e) {
            return R.failed("Failed to get document: " + e.getMessage());
        }
    }

    @Operation(summary = "Update Document")
    @PostMapping("/update")
    public R<String> updateDocument(@RequestBody MyDocument document) {
        try {
            String result = myDocumentService.updateDocument(document);
            return R.ok(result);
        } catch (IOException e) {
            return R.failed("Failed to update document: " + e.getMessage());
        }
    }

    @Operation(summary = "Delete Document")
    @PostMapping("/delete/{id}")
    public R<String> deleteDocument(@PathVariable String id) {
        try {
            String result = myDocumentService.deleteDocument(id);
            return R.ok(result);
        } catch (IOException e) {
            return R.failed("Failed to delete document: " + e.getMessage());
        }
    }

    @Operation(summary = "Search Documents by Title")
    @GetMapping("/search")
    public R<List<MyDocument>> searchByTitle(@RequestParam String title) {
        try {
            List<MyDocument> results = myDocumentService.searchByTitle(title);
            return R.ok(results);
        } catch (IOException e) {
            return R.failed("Failed to search documents: " + e.getMessage());
        }
    }
}
