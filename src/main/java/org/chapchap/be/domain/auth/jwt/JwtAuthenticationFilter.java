package org.chapchap.be.domain.auth.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chapchap.be.domain.user.entity.User;
import org.chapchap.be.domain.user.service.UserService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserService userService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain)
            throws ServletException, IOException {

        // 이미 인증돼 있으면 패스
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            chain.doFilter(request, response);
            return;
        }

        log.debug("Cookie header: {}", request.getHeader("Cookie"));
        String token = extractAccessToken(request);
        log.debug("Resolved token? {}", token != null);

        // 토큰이 없으면 다음 필터로 (익명)
        if (token == null || token.isBlank()) {
            chain.doFilter(request, response);
            return;
        }

        try {
            // 만료 체크(만료면 BadCredentialsException으로 흘려보냄 → 401 JSON)
            if (jwtProvider.isExpired(token)) {
                request.setAttribute("auth_error_msg", "토큰이 만료되었습니다.");
                throw new BadCredentialsException("Expired token");
            }

            Long userId = jwtProvider.parseUserId(token);
            User user = userService.getById(userId);

            UserDetails principal = org.springframework.security.core.userdetails.User.builder()
                    .username(user.getEmail())
                    .password("") // 사용 안 함
                    .authorities("ROLE_" + user.getRole().name())
                    .build();

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(auth);
            chain.doFilter(request, response);

        } catch (BadCredentialsException e) {
            // 위에서 직접 던진 케이스(만료 등)
            throw e;
        } catch (Exception e) {
            // 서명 오류/형식 오류 등
            request.setAttribute("auth_error_msg", "유효하지 않은 토큰입니다.");
            throw new BadCredentialsException("Invalid token", e);
        }
    }

    private String extractAccessToken(HttpServletRequest request) {
        // 1) HttpOnly 쿠키 우선
        if (request.getCookies() != null) {
            String fromCookie = Arrays.stream(request.getCookies())
                    .filter(c -> "ACCESS_TOKEN".equals(c.getName()))
                    .map(Cookie::getValue)
                    .findFirst().orElse(null);
            if (fromCookie != null && !fromCookie.isBlank()) return fromCookie;
        }
        // 2) Authorization 헤더(Bearer)도 허용(테스트/백오피스용)
        String authz = request.getHeader("Authorization");
        if (authz != null && authz.startsWith("Bearer ")) {
            return authz.substring(7);
        }
        return null;
    }
}
