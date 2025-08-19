package org.chapchap.be.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 회원가입 응답 DTO")
public record TokenResponse(
        @Schema(description = "Access Token (JWT)")
        String accessToken,

        @Schema(description = "Refresh Token (JWT)")
        String refreshToken
) {}
