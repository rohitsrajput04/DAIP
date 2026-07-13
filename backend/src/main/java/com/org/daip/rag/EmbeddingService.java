package com.db.daip.rag;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Generates vector embeddings via OpenAI. Falls back gracefully when API key is not configured.
 */
@Service
@Slf4j
public class EmbeddingService {

    private final Optional<EmbeddingModel> embeddingModel;

    public EmbeddingService(@Value("${openai.api-key:}") String apiKey,
                            @Value("${daip.rag.embedding-model}") String modelName) {
        if (apiKey != null && !apiKey.isBlank()) {
            this.embeddingModel = Optional.of(
                    OpenAiEmbeddingModel.builder()
                            .apiKey(apiKey)
                            .modelName(modelName)
                            .build()
            );
            log.info("OpenAI embedding model '{}' enabled for DAIP RAG", modelName);
        } else {
            this.embeddingModel = Optional.empty();
            log.warn("OPENAI_API_KEY not set — DAIP will use keyword search fallback for RAG");
        }
    }

    public boolean isAvailable() {
        return embeddingModel.isPresent();
    }

    public List<float[]> embedAll(List<String> texts) {
        if (texts.isEmpty()) {
            return List.of();
        }
        if (embeddingModel.isEmpty()) {
            return List.of();
        }

        List<TextSegment> segments = texts.stream().map(TextSegment::from).toList();
        List<Embedding> embeddings = embeddingModel.get().embedAll(segments).content();

        List<float[]> vectors = new ArrayList<>(embeddings.size());
        for (Embedding embedding : embeddings) {
            vectors.add(embedding.vector());
        }
        return vectors;
    }

    public Optional<float[]> embed(String text) {
        if (embeddingModel.isEmpty() || text == null || text.isBlank()) {
            return Optional.empty();
        }
        Embedding embedding = embeddingModel.get().embed(text).content();
        return Optional.of(embedding.vector());
    }
}
