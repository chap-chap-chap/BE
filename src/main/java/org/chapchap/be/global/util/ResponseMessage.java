package org.chapchap.be.global.util;

public record ResponseMessage(
        int status,          // HTTP status code
        String message,      // 설명
        String path,         // 요청 URI
        String timestamp     // ISO-8601
) {}
