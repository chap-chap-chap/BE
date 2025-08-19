package org.chapchap.be.global.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;

public final class ResponseBuilder {
    private ResponseBuilder() {}

    public static ResponseEntity<ResponseMessage> ok(HttpServletRequest req, String message) {
        return ResponseEntity.ok(new ResponseMessage(
                HttpStatus.OK.value(),
                message,
                req.getRequestURI(),
                OffsetDateTime.now().toString()));
    }
}