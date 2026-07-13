package com.db.daip.config;

import com.db.daip.entity.Document;
import com.db.daip.entity.DocumentStatus;
import com.db.daip.entity.User;
import com.db.daip.rag.DocumentProcessingService;
import com.db.daip.repository.DocumentRepository;
import com.db.daip.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Seeds a sample compliance policy document for DAIP RAG demonstration.
 */
@Component
@Order(3)
@RequiredArgsConstructor
@Slf4j
public class SampleDocumentLoader implements ApplicationRunner {

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final DocumentProcessingService documentProcessingService;

    @Value("${daip.upload.dir}")
    private String uploadDir;

    private static final String SAMPLE_CONTENT = """
            DAIP Compliance Policy — Anti-Money Laundering (AML)
            
            1. Purpose
            This policy defines AML requirements for the DB AI Decision Intelligence Platform (DAIP).
            
            2. Customer Due Diligence
            All relationship managers must perform enhanced due diligence for high-risk customers.
            Suspicious transactions above EUR 10,000 must be reported within 24 hours.
            
            3. Risk Assessment
            Credit risk exposure must be reviewed quarterly. Market risk limits are set by the Risk Committee.
            
            4. Regulatory Reporting
            Compliance officers must submit regulatory reports to BaFin and ECB per the approved schedule.
            
            5. ESG Requirements
            ESG screening is mandatory for all corporate lending above EUR 5 million.
            """;

    @Override
    public void run(ApplicationArguments args) {
        if (documentRepository.count() > 0) {
            return;
        }

        try {
            User admin = userRepository.findByUsername("admin").orElse(null);
            if (admin == null) {
                return;
            }

            Path uploadPath = Paths.get(uploadDir);
            Files.createDirectories(uploadPath);
            Path sampleFile = uploadPath.resolve("sample_aml_policy.txt");
            Files.writeString(sampleFile, SAMPLE_CONTENT);

            Document document = Document.builder()
                    .fileName("sample_aml_policy.txt")
                    .contentType("text/plain")
                    .fileSize((long) SAMPLE_CONTENT.length())
                    .storagePath(sampleFile.toString())
                    .domainCode("COMPLIANCE")
                    .description("Sample AML policy for DAIP RAG demonstration")
                    .uploadedBy(admin)
                    .status(DocumentStatus.UPLOADED)
                    .chunkCount(0)
                    .build();

            document = documentRepository.save(document);
            documentProcessingService.processDocument(document.getId());
            log.info("DAIP sample document indexed for RAG demo: {}", document.getFileName());

        } catch (Exception ex) {
            log.warn("Could not load sample document: {}", ex.getMessage());
        }
    }
}
