package com.db.daip.rag;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * Lightweight record for document chunk rows stored in PostgreSQL/pgvector.
 */
@Data
@Builder
public class DocumentChunkRecord {

    private Long id;
    private Long documentId;
    private int chunkIndex;
    private String content;
    private Integer tokenCount;
    private String fileName;
    private String domainCode;
    private Double similarity;
    private Instant createdAt;
}
