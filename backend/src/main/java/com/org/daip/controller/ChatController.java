package com.db.daip.controller;

import com.db.daip.dto.ChatRequest;
import com.db.daip.dto.ChatSessionResponse;
import com.db.daip.service.ChatService;
import com.db.daip.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
@Tag(name = "AI Chat", description = "DB AI Decision Intelligence Platform (DAIP) chat interface")
@SecurityRequirement(name = "bearerAuth")
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    @Operation(summary = "Send a message to the AI assistant")
    public ResponseEntity<ChatSessionResponse> sendMessage(@Valid @RequestBody ChatRequest request) {
        ChatSessionResponse response = chatService.sendMessage(request, SecurityUtils.getCurrentUser());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sessions")
    @Operation(summary = "List chat sessions for current user")
    public ResponseEntity<List<ChatSessionResponse>> getSessions() {
        return ResponseEntity.ok(chatService.getUserSessions(SecurityUtils.getCurrentUser()));
    }

    @GetMapping("/sessions/{sessionId}")
    @Operation(summary = "Get a specific chat session with messages")
    public ResponseEntity<ChatSessionResponse> getSession(@PathVariable Long sessionId) {
        return ResponseEntity.ok(chatService.getSession(sessionId, SecurityUtils.getCurrentUser()));
    }
}
