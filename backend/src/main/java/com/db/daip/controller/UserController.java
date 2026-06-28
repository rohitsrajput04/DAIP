package com.db.daip.controller;

import com.db.daip.dto.UserResponse;
import com.db.daip.mapper.EntityMapper;
import com.db.daip.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User profile endpoints")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final EntityMapper entityMapper;

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user profile")
    public ResponseEntity<UserResponse> getCurrentUser() {
        return ResponseEntity.ok(entityMapper.toUserResponse(SecurityUtils.getCurrentUser()));
    }
}
