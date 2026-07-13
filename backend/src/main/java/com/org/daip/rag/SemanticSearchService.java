package com.db.daip.rag;

import com.db.daip.dto.SearchRequest;
import com.db.daip.dto.SearchResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Semantic and keyword search over indexed document chunks.
 */
@Service
@RequiredArgsConstructor
public class SemanticSearchService {

    private final EmbeddingService embeddingService;
    private final PgVectorStore pgVectorStore;

    @Value("${daip.rag.top-k}")
    private int defaultTopK;

    @Value("${daip.rag.min-similarity}")
    private double minSimilarity;

    public List<SearchResultResponse> search(SearchRequest request) {
        int topK = request.getTopK() != null ? request.getTopK() : defaultTopK;
        String domainCode = request.getDomainCode();

        List<DocumentChunkRecord> results;
        if (embeddingService.isAvailable()) {
            float[] queryVector = embeddingService.embed(request.getQuery())
                    .orElseThrow(() -> new IllegalStateException("Failed to embed query"));
            results = pgVectorStore.semanticSearch(queryVector, topK, minSimilarity, domainCode);
        } else {
            results = pgVectorStore.keywordSearch(request.getQuery(), topK, domainCode);
        }

        return results.stream().map(this::toResponse).collect(Collectors.toList());
    }

    private SearchResultResponse toResponse(DocumentChunkRecord record) {
        return SearchResultResponse.builder()
                .chunkId(record.getId())
                .documentId(record.getDocumentId())
                .fileName(record.getFileName())
                .domainCode(record.getDomainCode())
                .content(record.getContent())
                .similarity(record.getSimilarity())
                .chunkIndex(record.getChunkIndex())
                .build();
    }
}
