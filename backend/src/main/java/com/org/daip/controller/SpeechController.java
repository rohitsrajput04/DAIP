package com.db.daip.controller;

import com.db.daip.dto.ApiError;
import com.db.daip.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/speech")
@RequiredArgsConstructor
@Tag(name = "Speech Services", description = "Speech-to-Text and Text-to-Speech services")
@SecurityRequirement(name = "bearerAuth")
public class SpeechController {

    @PostMapping("/stt")
    @Operation(summary = "Convert speech audio to text (Speech-to-Text)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Text transcribed successfully",
            content = @Content(schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "400", description = "Invalid audio file",
            content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<Map<String, String>> speechToText(
            @RequestParam("audio") MultipartFile audioFile) {
        try {
            // Validate file
            if (audioFile.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Audio file is empty"));
            }

            // Note: In production, you would integrate with a speech-to-text service like:
            // - Google Cloud Speech-to-Text
            // - AWS Transcribe
            // - Azure Speech Services
            // - OpenAI Whisper API
            
            // For now, return a placeholder response
            // The frontend will use Web Speech API as the primary method
            Map<String, String> response = new HashMap<>();
            response.put("text", "Backend speech-to-text requires integration with a speech service provider. Please use the browser's built-in speech recognition.");
            response.put("status", "placeholder");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to process audio: " + e.getMessage()));
        }
    }

    @PostMapping("/tts")
    @Operation(summary = "Convert text to speech audio (Text-to-Speech)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Audio generated successfully",
            content = @Content(schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "400", description = "Invalid text",
            content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<Map<String, String>> textToSpeech(@RequestBody Map<String, String> request) {
        try {
            String text = request.get("text");
            
            if (text == null || text.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Text is required"));
            }

            if (text.length() > 5000) {
                return ResponseEntity.badRequest().body(Map.of("error", "Text exceeds maximum length of 5000 characters"));
            }

            // Note: In production, you would integrate with a text-to-speech service like:
            // - Google Cloud Text-to-Speech
            // - AWS Polly
            // - Azure Speech Services
            // - ElevenLabs
            
            // For now, return a placeholder response
            // The frontend will use Web Speech API as the primary method
            Map<String, String> response = new HashMap<>();
            response.put("message", "Backend text-to-speech requires integration with a speech service provider. Please use the browser's built-in speech synthesis.");
            response.put("status", "placeholder");
            response.put("text", text);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to generate speech: " + e.getMessage()));
        }
    }

    @GetMapping("/voices")
    @Operation(summary = "Get available TTS voices (browser-side)")
    public ResponseEntity<Map<String, Object>> getVoices() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Voice listing is handled client-side using the Web Speech API");
        response.put("status", "client-side");
        return ResponseEntity.ok(response);
    }
}