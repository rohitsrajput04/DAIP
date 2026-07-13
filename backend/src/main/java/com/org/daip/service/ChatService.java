package com.db.daip.service;

import com.db.daip.ai.AiChatProvider;
import com.db.daip.dto.ChatMessageResponse;
import com.db.daip.dto.ChatRequest;
import com.db.daip.dto.ChatSessionResponse;
import com.db.daip.entity.*;
import com.db.daip.exception.ResourceNotFoundException;
import com.db.daip.mapper.EntityMapper;
import com.db.daip.repository.ChatMessageRepository;
import com.db.daip.repository.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AI chat session management. LangGraph-ready architecture for Phase 3 agent orchestration.
 */
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final AiChatProvider aiChatProvider;
    private final EntityMapper entityMapper;

    @Transactional
    public ChatSessionResponse sendMessage(ChatRequest request, User user) {
        ChatSession session = resolveSession(request, user);

        ChatMessage userMessage = ChatMessage.builder()
                .session(session)
                .role(MessageRole.USER)
                .content(request.getMessage())
                .build();
        chatMessageRepository.save(userMessage);

        String aiResponse = aiChatProvider.generateResponse(request.getMessage(), request.getDomainCode());

        ChatMessage assistantMessage = ChatMessage.builder()
                .session(session)
                .role(MessageRole.ASSISTANT)
                .content(aiResponse)
                .build();
        chatMessageRepository.save(assistantMessage);

        List<ChatMessage> messages = chatMessageRepository.findBySessionIdOrderByCreatedAtAsc(Long.valueOf(session.getId()));
        return entityMapper.toChatSessionResponse(session, messages);
    }

    @Transactional(readOnly = true)
    public List<ChatSessionResponse> getUserSessions(User user) {
        return chatSessionRepository.findByUser_IdOrderByUpdatedAtDesc((user.getId())).stream()
                .map(session -> {
                    List<ChatMessage> messages = chatMessageRepository.findBySessionIdOrderByCreatedAtAsc(Long.valueOf(session.getId()));
                    return entityMapper.toChatSessionResponse(session, messages);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ChatSessionResponse getSession(Long sessionId, User user) {
        ChatSession session = chatSessionRepository.findById((sessionId))
                .orElseThrow(() -> new ResourceNotFoundException("Chat session not found: " + sessionId));

        if (!session.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Chat session not found: " + sessionId);
        }

        List<ChatMessage> messages = chatMessageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
        return entityMapper.toChatSessionResponse(session, messages);
    }

    private ChatSession resolveSession(ChatRequest request, User user) {
        if (request.getSessionId() != null) {
            ChatSession session = chatSessionRepository.findById((request.getSessionId()))
                    .orElseThrow(() -> new ResourceNotFoundException("Chat session not found: " + request.getSessionId()));
            if (!session.getUser().getId().equals(user.getId())) {
                throw new ResourceNotFoundException("Chat session not found: " + request.getSessionId());
            }
            return session;
        }

        String title = request.getMessage().length() > 50
                ? request.getMessage().substring(0, 50) + "..."
                : request.getMessage();

        ChatSession newSession = ChatSession.builder()
                .title(title)
                .user(user)
                .domainCode(request.getDomainCode() != null ? request.getDomainCode() : "CORE")
                .build();

        return chatSessionRepository.save(newSession);
    }
}
