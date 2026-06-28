package com.db.daip.controller;

import com.db.daip.dto.RagQueryRequest;
import com.db.daip.dto.RagQueryResponse;
import com.db.daip.rag.RagPipelineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rag")
@RequiredArgsConstructor
@Tag(name = "RAG Pipeline", description = "Retrieval-Augmented Generation for DAIP")
@SecurityRequirement(name = "bearerAuth")
public class RagController {

    private final RagPipelineService ragPipelineService;

    @PostMapping("/query")
    @Operation(summary = "Ask a question using RAG over indexed documents")
    public ResponseEntity<RagQueryResponse> query(@Valid @RequestBody RagQueryRequest request) {
        RagQueryResponse response = ragPipelineService.query(request.getQuestion(), request.getDomainCode());
        return ResponseEntity.ok(response);
    }
}
