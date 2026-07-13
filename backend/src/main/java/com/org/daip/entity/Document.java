package com.db.daip.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Uploaded document metadata. Binary content stored on filesystem; Phase 2 adds extraction/RAG.
 */
@Entity
@Table(name = "documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String fileName;

    @Column(nullable = false, length = 100)
    private String contentType;

    @Column(nullable = false)
    private Long fileSize;

    @Column(nullable = false, length = 500)
    private String storagePath;

    @Column(length = 50)
    private String domainCode;

    @Column(length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by_id", nullable = false)
    private User uploadedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DocumentStatus status;

    /** Number of text chunks indexed for RAG (Phase 2). */
    @Column(nullable = false)
    @Builder.Default
    private Integer chunkCount = 0;

    /** Error message when processing fails. */
    @Column(length = 1000)
    private String processingError;

    @Column(nullable = false, updatable = false)
    private Instant uploadedAt;

    @PrePersist
    protected void onCreate() {
        uploadedAt = Instant.now();
        if (status == null) {
            status = DocumentStatus.UPLOADED;
        }
    }
}
