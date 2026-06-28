package com.db.daip.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Ensures pgvector chunk table exists after Hibernate creates the documents table.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Order(1)
public class PgVectorSchemaInitializer implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        try {
            jdbcTemplate.execute("CREATE EXTENSION IF NOT EXISTS vector");
            jdbcTemplate.execute("""
                    CREATE TABLE IF NOT EXISTS document_chunks (
                        id           BIGSERIAL PRIMARY KEY,
                        document_id  BIGINT NOT NULL,
                        chunk_index  INT NOT NULL,
                        content      TEXT NOT NULL,
                        token_count  INT,
                        embedding    vector(1536),
                        created_at   TIMESTAMP NOT NULL DEFAULT NOW(),
                        CONSTRAINT fk_document_chunks_document
                            FOREIGN KEY (document_id) REFERENCES documents(id) ON DELETE CASCADE
                    )
                    """);
            jdbcTemplate.execute("""
                    CREATE INDEX IF NOT EXISTS idx_document_chunks_document_id
                        ON document_chunks(document_id)
                    """);
            log.info("DAIP pgvector document_chunks schema initialized");
        } catch (Exception ex) {
            log.warn("PgVector schema init skipped (expected in H2 test profile): {}", ex.getMessage());
        }
    }
}
