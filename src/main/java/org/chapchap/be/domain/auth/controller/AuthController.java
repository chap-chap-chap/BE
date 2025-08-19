package org.chapchap.be.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.chapchap.be.domain.auth.dto.LoginRequest;
import org.chapchap.be.domain.auth.dto.SignupRequest;
import org.chapchap.be.domain.auth.dto.TokenResponse;
import org.chapchap.be.global.security.CookieUtil;
import org.chapchap.be.domain.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Value("${app.web.cross-site:false}")
    private boolean crossSite; // ! 환경에 따라 전환

    @Operation(summary = "회원가입", security = {})
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest reqBody,
                                    HttpServletRequest req, HttpServletResponse res) {
        authService.signup(reqBody);
        TokenResponse token = authService.login(reqBody.email(), reqBody.password());

//        // 자동 로그인 후 토큰 발급
//        CookieUtil.addAccessTokenCookie(req, res, "ACCESS_TOKEN", token.accessToken(), 60 * 15, crossSite);
//        CookieUtil.addAccessTokenCookie(req, res, "REFRESH_TOKEN", token.refreshToken(), 60 * 60 * 24 * 14, crossSite);

        // ! 배포 시 변경 - 임시로 Authorization 헤더(Bearer) 허용
        // return ResponseEntity.ok("회원가입에 성공했습니다.");
        return ResponseEntity.ok(token);
    }

    @Operation(summary = "로그인", security = {})
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest reqBody,
                                   HttpServletRequest req, HttpServletResponse res) {
        TokenResponse token = authService.login(reqBody.email(), reqBody.password());

        // 자동 로그인 후 토큰 발급
        CookieUtil.addAccessTokenCookie(req, res, "ACCESS_TOKEN", token.accessToken(), 60 * 15, crossSite);
        CookieUtil.addAccessTokenCookie(req, res, "REFRESH_TOKEN", token.refreshToken(), 60 * 60 * 24 * 14, crossSite);

        // ! 배포 시 변경 - 임시로 Authorization 헤더(Bearer) 허용
        // return ResponseEntity.ok("로그인에 성공했습니다.");
        return ResponseEntity.ok(token);
    }
}
