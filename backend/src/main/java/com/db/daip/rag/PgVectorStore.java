package com.db.daip.rag;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Persists and queries document chunk embeddings using PostgreSQL pgvector.
 */
@Repository
@RequiredArgsConstructor
public class PgVectorStore {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<DocumentChunkRecord> CHUNK_MAPPER = (rs, rowNum) ->
            DocumentChunkRecord.builder()
                    .id(rs.getLong("id"))
                    .documentId(rs.getLong("document_id"))
                    .chunkIndex(rs.getInt("chunk_index"))
                    .content(rs.getString("content"))
                    .tokenCount(rs.getObject("token_count") != null ? rs.getInt("token_count") : null)
                    .fileName(rs.getString("file_name"))
                    .domainCode(rs.getString("domain_code"))
                    .similarity(rs.getObject("similarity") != null ? rs.getDouble("similarity") : null)
                    .createdAt(rs.getTimestamp("created_at").toInstant())
                    .build();

    /**
     * Insert a chunk with optional embedding.
     */
    public long insertChunk(Long documentId,
                            int chunkIndex,
                            String content,
                            int tokenCount,
                            float[] embedding) {

        if (embedding != null) {

            Long id = jdbcTemplate.queryForObject(
                    """
                    INSERT INTO document_chunks
                    (document_id, chunk_index, content, token_count, embedding)
                    VALUES (?, ?, ?, ?, ?::vector)
                    RETURNING id
                    """,
                    Long.class,
                    documentId,
                    chunkIndex,
                    content,
                    tokenCount,
                    toVectorLiteral(embedding)
            );

            return id != null ? id : 0L;
        }

        Long id = jdbcTemplate.queryForObject(
                """
                INSERT INTO document_chunks
                (document_id, chunk_index, content, token_count)
                VALUES (?, ?, ?, ?)
                RETURNING id
                """,
                Long.class,
                documentId,
                chunkIndex,
                content,
                tokenCount
        );

        return id != null ? id : 0L;
    }

    /**
     * Delete all chunks of a document.
     */
    public void deleteByDocumentId(Long documentId) {
        jdbcTemplate.update(
                "DELETE FROM document_chunks WHERE document_id = ?",
                documentId
        );
    }

    /**
     * Count chunks for a document.
     */
    public int countByDocumentId(Long documentId) {

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM document_chunks WHERE document_id = ?",
                Integer.class,
                documentId
        );

        return count != null ? count : 0;
    }

    /**
     * Semantic Vector Search.
     */
    public List<DocumentChunkRecord> semanticSearch(float[] queryEmbedding,
                                                    int topK,
                                                    double minSimilarity,
                                                    String domainCode) {

        String vector = toVectorLiteral(queryEmbedding);

        StringBuilder sql = new StringBuilder("""
                SELECT
                    dc.id,
                    dc.document_id,
                    dc.chunk_index,
                    dc.content,
                    dc.token_count,
                    dc.created_at,
                    d.file_name,
                    d.domain_code,
                    1 - (dc.embedding <=> ?::vector) AS similarity
                FROM document_chunks dc
                JOIN documents d
                    ON d.id = dc.document_id
                WHERE dc.embedding IS NOT NULL
                  AND (1 - (dc.embedding <=> ?::vector)) >= ?
                """);

        List<Object> params = new ArrayList<>();

        params.add(vector);
        params.add(vector);
        params.add(minSimilarity);

        if (domainCode != null && !domainCode.isBlank()) {
            sql.append(" AND d.domain_code = ? ");
            params.add(domainCode);
        }

        sql.append("""
                ORDER BY dc.embedding <=> ?::vector
                LIMIT ?
                """);

        params.add(vector);
        params.add(topK);

        System.out.println("Semantic Search SQL:");
        System.out.println(sql);

        return jdbcTemplate.query(
                sql.toString(),
                CHUNK_MAPPER,
                params.toArray()
        );
    }

    /**
     * Keyword Search.
     */
    public List<DocumentChunkRecord> keywordSearch(String query,
                                                   int topK,
                                                   String domainCode) {

        String pattern = "%" + query.toLowerCase() + "%";

        StringBuilder sql = new StringBuilder("""
                SELECT
                    dc.id,
                    dc.document_id,
                    dc.chunk_index,
                    dc.content,
                    dc.token_count,
                    dc.created_at,
                    d.file_name,
                    d.domain_code,
                    0.5 AS similarity
                FROM document_chunks dc
                JOIN documents d
                    ON d.id = dc.document_id
                WHERE LOWER(dc.content) LIKE ?
                """);

        List<Object> params = new ArrayList<>();

        params.add(pattern);

        if (domainCode != null && !domainCode.isBlank()) {
            sql.append(" AND d.domain_code = ? ");
            params.add(domainCode);
        }

        sql.append("""
                ORDER BY dc.created_at DESC
                LIMIT ?
                """);

        params.add(topK);

        System.out.println("Keyword Search SQL:");
        System.out.println(sql);

        return jdbcTemplate.query(
                sql.toString(),
                CHUNK_MAPPER,
                params.toArray()
        );
    }

    /**
     * Convert float[] into PostgreSQL pgvector literal.
     */
    private String toVectorLiteral(float[] vector) {

        if (vector == null || vector.length == 0) {
            return "[]";
        }

        return java.util.stream.IntStream.range(0, vector.length)
                .mapToObj(i -> Float.toString(vector[i]))
                .collect(Collectors.joining(",", "[", "]"));
    }
}