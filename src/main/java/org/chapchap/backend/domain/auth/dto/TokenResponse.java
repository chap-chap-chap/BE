package org.chapchap.backend.domain.auth.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {}