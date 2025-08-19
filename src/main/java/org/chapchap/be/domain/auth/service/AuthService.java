package org.chapchap.be.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chapchap.be.domain.auth.dto.SignupRequest;
import org.chapchap.be.domain.auth.dto.TokenResponse;
import org.chapchap.be.global.security.JwtProvider;
import org.chapchap.be.domain.user.entity.User;
import org.chapchap.be.domain.user.entity.UserProfile;
import org.chapchap.be.domain.user.repository.UserProfileRepository;
import org.chapchap.be.domain.user.service.UserService;
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
    private final UserProfileRepository userProfileRepository;

    public void signup(SignupRequest req) {
        // 사용자 생성
        User user = userService.register(req.email(), req.password(), req.name());

        // 사용자 프로필 생성
        var p = req.profile();
        if (p == null) {
            throw new IllegalArgumentException("프로필 정보가 없습니다.");
        }

        UserProfile.Sex sex = parseSexSafe(p.sex());

        UserProfile profile = UserProfile.builder()
                .user(user)
                .humanWeightKg(p.weightKg())
                .humanHeightCm(p.heightCm())
                .humanAge(p.age())
                .humanSex(sex)
                .build();
        userProfileRepository.save(profile);

        log.info("[SIGNUP] success: userId={}, email={}", user.getId(), user.getEmail());
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

    private UserProfile.Sex parseSexSafe(String raw) {
        if (raw == null || raw.isBlank()) return null;
        try { return UserProfile.Sex.valueOf(raw.trim().toUpperCase()); }
        catch (Exception ignore) { return null; }
    }
}
