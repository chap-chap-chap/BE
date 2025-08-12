package org.chapchap.be.global.exception;

public record ErrorResponse(
        int status,          // HTTP status code
        String message,      // 설명
        String path,         // 요청 URI
        String timestamp     // ISO-8601
) {}
