package org.chapchap.be.domain.auth.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {}