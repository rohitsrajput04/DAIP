-- Enable pgvector extension for Phase 2 RAG embeddings
CREATE EXTENSION IF NOT EXISTS vector;

-- Domain registry table for modular business domains (ESG, AML, Risk, etc.)
CREATE TABLE IF NOT EXISTS domain_registry (
    id          BIGSERIAL PRIMARY KEY,
    code        VARCHAR(50) NOT NULL UNIQUE,
    name        VARCHAR(100) NOT NULL,
    description TEXT,
    active      BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

INSERT INTO domain_registry (code, name, description) VALUES
    ('CORE', 'Core Platform', 'Core DAIP platform services'),
    ('RISK', 'Risk Management', 'Credit, market, and operational risk'),
    ('COMPLIANCE', 'Compliance', 'Regulatory compliance and policy management'),
    ('ESG', 'ESG', 'Environmental, Social, and Governance'),
    ('AML', 'Anti-Money Laundering', 'AML detection and reporting')
ON CONFLICT (code) DO NOTHING;

-- Chat sessions table
CREATE TABLE IF NOT EXISTS chat_sessions (
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(200) NOT NULL,
    user_id     BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    domain_code VARCHAR(50),
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_chat_sessions_user_id ON chat_sessions(user_id);
CREATE INDEX IF NOT EXISTS idx_chat_sessions_updated_at ON chat_sessions(updated_at DESC);

-- Chat messages table
CREATE TABLE IF NOT EXISTS chat_messages (
    id          BIGSERIAL PRIMARY KEY,
    session_id  BIGINT NOT NULL REFERENCES chat_sessions(id) ON DELETE CASCADE,
    role        VARCHAR(20) NOT NULL,
    content     TEXT NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_chat_messages_session_id ON chat_messages(session_id);
CREATE INDEX IF NOT EXISTS idx_chat_messages_created_at ON chat_messages(created_at);

-- Document chunks table for RAG
CREATE TABLE IF NOT EXISTS document_chunks (
    id            BIGSERIAL PRIMARY KEY,
    document_id   BIGINT NOT NULL REFERENCES documents(id) ON DELETE CASCADE,
    chunk_index   INT NOT NULL,
    content       TEXT NOT NULL,
    token_count   INT,
    embedding     vector(1536),
    file_name     VARCHAR(255) NOT NULL,
    domain_code   VARCHAR(50),
    created_at    TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_document_chunks_document_id ON document_chunks(document_id);
CREATE INDEX IF NOT EXISTS idx_document_chunks_domain_code ON document_chunks(domain_code);
