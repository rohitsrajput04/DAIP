package com.db.daip.dto;

import com.db.daip.entity.DocumentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponse {

    private Long id;
    private String fileName;
    private String contentType;
    private Long fileSize;
    private String domainCode;
    private String description;
    private DocumentStatus status;
    private Integer chunkCount;
    private String processingError;
    private String uploadedBy;
    private Instant uploadedAt;
}
