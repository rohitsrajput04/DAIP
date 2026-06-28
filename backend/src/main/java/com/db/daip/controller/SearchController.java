package com.db.daip.controller;

import com.db.daip.dto.RagQueryRequest;
import com.db.daip.dto.RagQueryResponse;
import com.db.daip.dto.SearchRequest;
import com.db.daip.dto.SearchResultResponse;
import com.db.daip.rag.RagPipelineService;
import com.db.daip.rag.SemanticSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
@Tag(name = "Semantic Search", description = "DB AI Decision Intelligence Platform (DAIP) RAG search")
@SecurityRequirement(name = "bearerAuth")
public class SearchController {

    private final SemanticSearchService semanticSearchService;

    @PostMapping
    @Operation(summary = "Semantic search over indexed document chunks")
    public ResponseEntity<List<SearchResultResponse>> search(@Valid @RequestBody SearchRequest request) {
        return ResponseEntity.ok(semanticSearchService.search(request));
    }

    @GetMapping
    @Operation(summary = "Semantic search via query parameters")
    public ResponseEntity<List<SearchResultResponse>> searchGet(
            @RequestParam String query,
            @RequestParam(required = false) String domainCode,
            @RequestParam(required = false) Integer topK) {
        SearchRequest request = SearchRequest.builder()
                .query(query)
                .domainCode(domainCode)
                .topK(topK)
                .build();
        return ResponseEntity.ok(semanticSearchService.search(request));
    }
}
