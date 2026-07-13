package com.db.daip.ai;

import com.db.daip.dto.RagQueryResponse;
import com.db.daip.rag.RagPipelineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * AI chat response provider using DAIP RAG pipeline (Phase 2) with rule-based fallback.
 */
@Component
@RequiredArgsConstructor
public class AiChatProvider {

    private final RagPipelineService ragPipelineService;

    public String generateResponse(String userMessage, String domainCode) {
        RagQueryResponse ragResponse = ragPipelineService.query(userMessage, domainCode);

        if (ragResponse.getSources() != null && !ragResponse.getSources().isEmpty()) {
            StringBuilder response = new StringBuilder(ragResponse.getAnswer());
            response.append("\n\n---\n**Sources (").append(ragResponse.getSearchMode()).append("):**\n");
            for (int i = 0; i < ragResponse.getSources().size(); i++) {
                var source = ragResponse.getSources().get(i);
                response.append(i + 1).append(". ").append(source.getFileName());
                if (source.getSimilarity() != null) {
                    response.append(String.format(" (%.0f%% match)", source.getSimilarity() * 100));
                }
                response.append("\n");
            }
            return response.toString();
        }

        String domain = domainCode != null ? domainCode : "CORE";
        return String.format(
                "[DAIP %s] I'm your DB AI Decision Intelligence Platform assistant. " +
                "Upload PDF, Word, or Excel documents to enable RAG-powered answers. %s",
                domain,
                ragResponse.getAnswer()
        );
    }
}
