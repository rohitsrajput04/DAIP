package com.db.daip.controller;

import com.db.daip.dto.DocumentResponse;
import com.db.daip.service.DocumentService;
import com.db.daip.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
@Tag(name = "Documents", description = "Document upload and management")
@SecurityRequirement(name = "bearerAuth")
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a document")
    public ResponseEntity<DocumentResponse> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "domainCode", required = false) String domainCode,
            @RequestParam(value = "description", required = false) String description) {
        DocumentResponse response = documentService.uploadDocument(
                file, domainCode, description, SecurityUtils.getCurrentUser());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "List all documents")
    public ResponseEntity<List<DocumentResponse>> getAllDocuments() {
        return ResponseEntity.ok(documentService.getAllDocuments());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get document by ID")
    public ResponseEntity<DocumentResponse> getDocument(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.getDocumentById(id));
    }

    @GetMapping("/my")
    @Operation(summary = "List documents uploaded by current user")
    public ResponseEntity<List<DocumentResponse>> getMyDocuments() {
        return ResponseEntity.ok(documentService.getDocumentsByUser(Long.valueOf(SecurityUtils.getCurrentUser().getId())));
    }

    @PostMapping("/{id}/reprocess")
    @Operation(summary = "Re-run text extraction, chunking, and embedding for a document")
    public ResponseEntity<DocumentResponse> reprocessDocument(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.reprocessDocument(id));
    }
}
