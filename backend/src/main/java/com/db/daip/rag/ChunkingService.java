package com.db.daip.rag;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Splits extracted text into overlapping chunks suitable for embedding and RAG retrieval.
 */
@Service
public class ChunkingService {

    @Value("${daip.rag.chunk-size}")
    private int chunkSize;

    @Value("${daip.rag.chunk-overlap}")
    private int chunkOverlap;

    public List<String> chunk(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        String normalized = text.replaceAll("\\s+", " ").trim();
        List<String> chunks = new ArrayList<>();

        if (normalized.length() <= chunkSize) {
            chunks.add(normalized);
            return chunks;
        }

        int start = 0;
        while (start < normalized.length()) {
            int end = Math.min(start + chunkSize, normalized.length());
            chunks.add(normalized.substring(start, end).trim());

            if (end >= normalized.length()) {
                break;
            }
            start = Math.max(end - chunkOverlap, start + 1);
        }

        return chunks;
    }

    public int estimateTokenCount(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }
        return text.trim().split("\\s+").length;
    }
}
