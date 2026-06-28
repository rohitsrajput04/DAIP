package com.db.daip.service;

import com.db.daip.dto.DashboardResponse;
import com.db.daip.repository.ChatSessionRepository;
import com.db.daip.repository.DocumentRepository;
import com.db.daip.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

/**
 * Aggregates platform metrics for the executive dashboard.
 */
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DocumentRepository documentRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public DashboardResponse getDashboardMetrics() {
        Instant startOfDay = LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC);

        return DashboardResponse.builder()
                .totalDocuments(documentRepository.count())
                .totalChatSessions(chatSessionRepository.count())
                .totalUsers(userRepository.count())
                .documentsUploadedToday(documentRepository.countUploadedSince(startOfDay))
                .platformStatus("OPERATIONAL")
                .activeDomain("CORE")
                .build();
    }
}
