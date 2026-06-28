package com.db.daip.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RagQueryRequest {

    @NotBlank(message = "Question is required")
    private String question;

    private String domainCode;
}
