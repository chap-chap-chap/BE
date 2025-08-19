package org.chapchap.be.global.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chapchap.be.global.response.ResponseMessage;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.OffsetDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final ObjectMapper om = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest req,
                         HttpServletResponse res,
                         AuthenticationException e) throws IOException {

        // JwtAuthenticationFilter에서 넣어줄 사용자 친화 메시지(토큰 만료 등)
        String attrMsg = (String) req.getAttribute("auth_error_msg");

        String message;
        if (attrMsg != null) {
            message = attrMsg; // 필터가 세팅한 구체 메시지 우선
        } else if (e instanceof BadCredentialsException) {
            message = "이메일 또는 비밀번호가 올바르지 않습니다.";
        } else {
            message = "인증이 필요합니다.";
        }

        log.warn("[401] path={}, reason={}", req.getRequestURI(), e.getClass().getSimpleName());

        ResponseMessage body = new ResponseMessage(
                401,
                message,
                req.getRequestURI(),
                OffsetDateTime.now().toString()
        );

        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.setCharacterEncoding("UTF-8");
        om.writeValue(res.getWriter(), body);
    }
}
