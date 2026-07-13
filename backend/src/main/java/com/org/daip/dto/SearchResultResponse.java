package com.db.daip.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResultResponse {

    private Long chunkId;
    private Long documentId;
    private String fileName;
    private String domainCode;
    private String content;
    private Double similarity;
    private Integer chunkIndex;
}
