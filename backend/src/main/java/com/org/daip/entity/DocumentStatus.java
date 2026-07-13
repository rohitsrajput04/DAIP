package com.db.daip.entity;

/**
 * Document processing lifecycle. Extended in Phase 2 for chunking and embedding.
 */
public enum DocumentStatus {
    UPLOADED,
    PROCESSING,
    INDEXED,
    FAILED
}
