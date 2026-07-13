package com.db.daip.service;

import com.db.daip.dto.DocumentResponse;
import com.db.daip.entity.Document;
import com.db.daip.entity.DocumentStatus;
import com.db.daip.entity.User;
import com.db.daip.exception.DaipException;
import com.db.daip.exception.ResourceNotFoundException;
import com.db.daip.mapper.EntityMapper;
import com.db.daip.rag.DocumentProcessingService;
import com.db.daip.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Document upload and retrieval with automatic RAG indexing (Phase 2).
 */
@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final EntityMapper entityMapper;
    private final DocumentProcessingService documentProcessingService;

    @Value("${daip.upload.dir}")
    private String uploadDir;

    @Transactional
    public DocumentResponse uploadDocument(MultipartFile file, String domainCode, String description, User user) {
        if (file.isEmpty()) {
            throw new DaipException("File cannot be empty");
        }

        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String storedName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path targetPath = uploadPath.resolve(storedName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            Document document = Document.builder()
                    .fileName(file.getOriginalFilename())
                    .contentType(file.getContentType())
                    .fileSize(file.getSize())
                    .storagePath(targetPath.toString())
                    .domainCode(domainCode != null ? domainCode : "CORE")
                    .description(description)
                    .uploadedBy(user)
                    .status(DocumentStatus.UPLOADED)
                    .chunkCount(0)
                    .build();

            document = documentRepository.save(document);
            scheduleProcessing(document.getId());
            return entityMapper.toDocumentResponse(document);

        } catch (IOException e) {
            throw new DaipException("Failed to upload document: " + e.getMessage(), e);
        }
    }

    @Transactional
    public DocumentResponse reprocessDocument(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found: " + id));
        scheduleProcessing(document.getId());
        return entityMapper.toDocumentResponse(document);
    }

    @Transactional(readOnly = true)
    public List<DocumentResponse> getAllDocuments() {
        return documentRepository.findAll().stream()
                .map(entityMapper::toDocumentResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DocumentResponse getDocumentById(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found: " + id));
        return entityMapper.toDocumentResponse(document);
    }

    @Transactional(readOnly = true)
    public List<DocumentResponse> getDocumentsByUser(Long userId) {
        return documentRepository.findByUploadedByIdOrderByUploadedAtDesc(userId).stream()
                .map(entityMapper::toDocumentResponse)
                .collect(Collectors.toList());
    }

    private void scheduleProcessing(Long documentId) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                documentProcessingService.processDocumentAsync(documentId);
            }
        });
    }
}
