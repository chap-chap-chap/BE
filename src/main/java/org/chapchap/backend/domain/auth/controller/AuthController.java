package org.chapchap.backend.domain.auth.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.chapchap.backend.domain.auth.dto.LoginRequest;
import org.chapchap.backend.domain.auth.dto.SignupRequest;
import org.chapchap.backend.domain.auth.dto.TokenResponse;
import org.chapchap.backend.domain.auth.jwt.CookieUtil;
import org.chapchap.backend.domain.auth.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest req,
                                       HttpServletResponse res) {
        authService.signup(req);

        // 자동 로그인 후 토큰 발급
        TokenResponse token = authService.login(req.email(), req.password());
        CookieUtil.addHttpOnlyCookie(res, "ACCESS_TOKEN", token.accessToken(), 60 * 15);
        CookieUtil.addHttpOnlyCookie(res, "REFRESH_TOKEN", token.refreshToken(), 60 * 60 * 24 * 14);

        // ! 배포 시 변경 - 임시로 Authorization 헤더(Bearer) 허용
        // return ResponseEntity.ok("회원가입에 성공했습니다.");
        return ResponseEntity.ok(token);
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req,
                                      HttpServletResponse res) {
        TokenResponse token = authService.login(req.email(), req.password());
        CookieUtil.addHttpOnlyCookie(res, "ACCESS_TOKEN", token.accessToken(), 60 * 15);
        CookieUtil.addHttpOnlyCookie(res, "REFRESH_TOKEN", token.refreshToken(), 60 * 60 * 24 * 14);

        // ! 배포 시 변경 - 임시로 Authorization 헤더(Bearer) 허용
        // return ResponseEntity.ok("로그인에 성공했습니다.");
        return ResponseEntity.ok(token);
    }
}
