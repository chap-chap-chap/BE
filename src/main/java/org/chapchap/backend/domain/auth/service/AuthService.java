package org.chapchap.backend.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chapchap.backend.domain.auth.dto.SignupRequest;
import org.chapchap.backend.domain.auth.dto.TokenResponse;
import org.chapchap.backend.domain.auth.jwt.JwtProvider;
import org.chapchap.backend.domain.user.entity.User;
import org.chapchap.backend.domain.user.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    public void signup(SignupRequest req) {
        User saved = userService.register(req.email(), req.password(), req.name());

        log.info("[SIGNUP] success: userId={}, email={}", saved.getId(), saved.getEmail());
    }

    public TokenResponse login(String email, String password) {
        // 스프링 시큐리티에서 인증 - 비밀번호 검증
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));

        // 인증 성공 기준으로 유저 조회
        User user = userService.getByEmail(email);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 마지막 로그인 기록
        userService.markLogin(user.getId());

        // 토큰 발급
        String access = jwtProvider.createAccessToken(user.getId());
        String refresh = jwtProvider.createRefreshToken(user.getId());

        log.info("[LOGIN] success: userId={}, email={}", user.getId(), email);

        return new TokenResponse(access, refresh);
    }
}