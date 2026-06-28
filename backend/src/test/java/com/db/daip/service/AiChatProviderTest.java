package com.db.daip.service;

import com.db.daip.ai.AiChatProvider;
import com.db.daip.dto.RagQueryResponse;
import com.db.daip.rag.RagPipelineService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiChatProviderTest {

    @Mock
    private RagPipelineService ragPipelineService;

    @InjectMocks
    private AiChatProvider provider;

    @Test
    void shouldReturnResponseFromRagPipeline() {
        when(ragPipelineService.query(anyString(), anyString())).thenReturn(
                RagQueryResponse.builder()
                        .answer("AML reporting is required within 24 hours.")
                        .searchMode("KEYWORD")
                        .sources(List.of())
                        .build()
        );

        String response = provider.generateResponse("What is the AML reporting deadline?", "COMPLIANCE");
        assertTrue(response.contains("DAIP"));
    }

    @Test
    void shouldReturnFallbackWhenNoSourcesFound() {
        when(ragPipelineService.query(anyString(), anyString())).thenReturn(
                RagQueryResponse.builder()
                        .answer("No indexed documents matched.")
                        .sources(List.of())
                        .searchMode("KEYWORD")
                        .build()
        );

        String response = provider.generateResponse("Hello", "CORE");
        assertNotNull(response);
        assertFalse(response.isBlank());
    }
}
