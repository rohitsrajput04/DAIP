package com.db.daip.mapper;

import com.db.daip.dto.*;
import com.db.daip.entity.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Maps entities to DTOs. Keeps API layer decoupled from persistence layer.
 */
@Component
public class EntityMapper {

    public UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(Long.valueOf(user.getId()))
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .department(user.getDepartment())
                .role(user.getRole())
                .build();
    }

    public DocumentResponse toDocumentResponse(Document document) {
        return DocumentResponse.builder()
                .id(document.getId())
                .fileName(document.getFileName())
                .contentType(document.getContentType())
                .fileSize(document.getFileSize())
                .domainCode(document.getDomainCode())
                .description(document.getDescription())
                .status(document.getStatus())
                .chunkCount(document.getChunkCount())
                .processingError(document.getProcessingError())
                .uploadedBy(document.getUploadedBy().getFullName())
                .uploadedAt(document.getUploadedAt())
                .build();
    }

    public ChatMessageResponse toChatMessageResponse(ChatMessage message) {
        return ChatMessageResponse.builder()
                .id(message.getId())
                .role(message.getRole().name())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .build();
    }

    public ChatSessionResponse toChatSessionResponse(ChatSession session, List<ChatMessage> messages) {
        return ChatSessionResponse.builder()
                .id(Long.valueOf(session.getId()))
                .title(session.getTitle())
                .domainCode(session.getDomainCode())
                .createdAt(session.getCreatedAt())
                .messages(messages.stream().map(this::toChatMessageResponse).collect(Collectors.toList()))
                .build();
    }
}
