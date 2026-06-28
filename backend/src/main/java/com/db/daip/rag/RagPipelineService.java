package com.db.daip.rag;

import com.db.daip.dto.RagQueryResponse;
import com.db.daip.dto.SearchRequest;
import com.db.daip.dto.SearchResultResponse;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * RAG pipeline: retrieve relevant chunks, augment prompt, generate grounded answer.
 */
@Service
@Slf4j
public class RagPipelineService {

    private final SemanticSearchService semanticSearchService;
    private final EmbeddingService embeddingService;
    private final Optional<ChatLanguageModel> chatModel;

    @Value("${daip.rag.top-k}")
    private int topK;

    public RagPipelineService(SemanticSearchService semanticSearchService,
                              EmbeddingService embeddingService,
                              @Value("${openai.api-key:}") String apiKey,
                              @Value("${daip.rag.chat-model}") String chatModelName) {
        this.semanticSearchService = semanticSearchService;
        this.embeddingService = embeddingService;
        if (apiKey != null && !apiKey.isBlank()) {
            this.chatModel = Optional.of(
                    OpenAiChatModel.builder()
                            .apiKey(apiKey)
                            .modelName(chatModelName)
                            .temperature(0.2)
                            .build()
            );
        } else {
            this.chatModel = Optional.empty();
        }
    }

    public RagQueryResponse query(String question, String domainCode) {
        SearchRequest searchRequest = SearchRequest.builder()
                .query(question)
                .domainCode(domainCode)
                .topK(topK)
                .build();

        List<SearchResultResponse> sources = semanticSearchService.search(searchRequest);
        String searchMode = embeddingService.isAvailable() ? "SEMANTIC" : "KEYWORD";

        if (sources.isEmpty()) {
            return RagQueryResponse.builder()
                    .answer("No indexed documents matched your query. Upload PDF, Word, or Excel files " +
                            "via the Documents page to enable RAG on the DB AI Decision Intelligence Platform (DAIP).")
                    .sources(List.of())
                    .searchMode(searchMode)
                    .build();
        }

        String context = sources.stream()
                .map(s -> "Source: " + s.getFileName() + "\n" + s.getContent())
                .collect(Collectors.joining("\n\n---\n\n"));

        String answer;
        if (chatModel.isPresent()) {
            String prompt = """
                    You are the DB AI Decision Intelligence Platform (DAIP) assistant for an investment bank.
                    Answer the question using ONLY the provided context. Cite source file names.
                    If the context is insufficient, say so clearly.

                    Context:
                    %s

                    Question: %s

                    Answer:
                    """.formatted(context, question);
            answer = chatModel.get().generate(prompt);
        } else {
            answer = buildFallbackAnswer(question, sources);
        }

        return RagQueryResponse.builder()
                .answer(answer)
                .sources(sources)
                .searchMode(searchMode)
                .build();
    }

    private String buildFallbackAnswer(String question, List<SearchResultResponse> sources) {
        StringBuilder builder = new StringBuilder();
        builder.append("[DAIP RAG — keyword mode] Top matches for: \"").append(question).append("\"\n\n");
        for (int i = 0; i < sources.size(); i++) {
            SearchResultResponse source = sources.get(i);
            builder.append(i + 1).append(". ").append(source.getFileName())
                    .append(" (").append(source.getDomainCode()).append(")\n");
            String excerpt = source.getContent().length() > 300
                    ? source.getContent().substring(0, 300) + "..."
                    : source.getContent();
            builder.append(excerpt).append("\n\n");
        }
        builder.append("Set OPENAI_API_KEY for full semantic search and LLM-generated answers.");
        return builder.toString();
    }
}
