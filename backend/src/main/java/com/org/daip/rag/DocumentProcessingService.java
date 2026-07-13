package com.db.daip.rag;

import com.db.daip.entity.Document;
import com.db.daip.entity.DocumentStatus;
import com.db.daip.exception.DaipException;
import com.db.daip.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.util.List;

/**
 * Orchestrates document ingestion: extract text, chunk, embed, and store in pgvector.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentProcessingService {

    private final DocumentRepository documentRepository;
    private final TextExtractionService textExtractionService;
    private final ChunkingService chunkingService;
    private final EmbeddingService embeddingService;
    private final PgVectorStore pgVectorStore;

    @Async("documentProcessingExecutor")
    @Transactional
    public void processDocumentAsync(Long documentId) {
        processDocument(documentId);
    }

    @Transactional
    public void processDocument(Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DaipException("Document not found: " + documentId));

        document.setStatus(DocumentStatus.PROCESSING);
        document.setProcessingError(null);
        documentRepository.save(document);

        try {
            pgVectorStore.deleteByDocumentId(documentId);

            String text = textExtractionService.extract(
                    Path.of(document.getStoragePath()),
                    document.getFileName(),
                    document.getContentType()
            );

            List<String> chunks = chunkingService.chunk(text);
            if (chunks.isEmpty()) {
                throw new DaipException("No extractable text found in document");
            }

            List<float[]> embeddings = embeddingService.embedAll(chunks);

            for (int i = 0; i < chunks.size(); i++) {
                float[] embedding = i < embeddings.size() ? embeddings.get(i) : null;
                pgVectorStore.insertChunk(
                        documentId,
                        i,
                        chunks.get(i),
                        chunkingService.estimateTokenCount(chunks.get(i)),
                        embedding
                );
            }

            document.setChunkCount(chunks.size());
            document.setStatus(DocumentStatus.INDEXED);
            document.setProcessingError(null);
            documentRepository.save(document);

            log.info("DAIP indexed document {} with {} chunks", document.getFileName(), chunks.size());

        } catch (Exception ex) {
            log.error("Failed to process document {}: {}", documentId, ex.getMessage());
            document.setStatus(DocumentStatus.FAILED);
            document.setProcessingError(ex.getMessage());
            document.setChunkCount(0);
            documentRepository.save(document);
        }
    }
}
