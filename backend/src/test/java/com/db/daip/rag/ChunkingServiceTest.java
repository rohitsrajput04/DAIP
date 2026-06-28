package com.db.daip.rag;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChunkingServiceTest {

    @Test
    void shouldReturnSingleChunkForShortText() throws Exception {
        ChunkingService service = new ChunkingService();
        setField(service, "chunkSize", 800);
        setField(service, "chunkOverlap", 100);

        List<String> chunks = service.chunk("Short compliance policy text.");
        assertEquals(1, chunks.size());
    }

    @Test
    void shouldSplitLongTextIntoMultipleChunks() throws Exception {
        ChunkingService service = new ChunkingService();
        setField(service, "chunkSize", 100);
        setField(service, "chunkOverlap", 20);

        String longText = "AML policy requirement. ".repeat(20);
        List<String> chunks = service.chunk(longText);

        assertTrue(chunks.size() > 1);
        assertTrue(chunks.get(0).length() <= 100);
    }

    @Test
    void shouldEstimateTokenCount() {
        ChunkingService service = new ChunkingService();
        assertEquals(4, service.estimateTokenCount("one two three four"));
    }

    private void setField(Object target, String name, int value) throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }
}
