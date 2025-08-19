package org.chapchap.be.global.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;

public class CookieUtil {

    private CookieUtil() {}

    @Value("${app.web.cookie-domain:}")
    private static String cookieDomain;

    /**
     * 개발(HTTP, same-origin): SameSite=Lax, Secure=false
     * 운영(HTTPS, cross-site): SameSite=None, Secure=true
     *
     * @param crossSite  프론트가 다른 도메인에서 요청하면 true (예: app.example.com -> api.example.com)
     */
    public static void addAccessTokenCookie(HttpServletRequest req, HttpServletResponse res,
                                            String name, String value, int maxAgeSec, boolean crossSite) {
        boolean https = isSecure(req); // X-Forwarded-Proto 고려
        ResponseCookie.ResponseCookieBuilder b = ResponseCookie.from(name, value)
                .httpOnly(true)
                .path("/")
                .maxAge(maxAgeSec);

        if (crossSite) {
            // 크로스 오리진은 반드시 None + Secure(브라우저 정책)
            b.sameSite("None").secure(true);
            if (cookieDomain != null && !cookieDomain.isBlank()) {
                b.domain(cookieDomain);
            }
        } else {
            // 같은 오리진이면 Lax + (HTTP면 false, HTTPS면 true)
            b.sameSite("Lax").secure(https);
        }

        res.addHeader("Set-Cookie", b.build().toString());
    }

    private static boolean isSecure(HttpServletRequest req) {
        // 리버스 프록시(예: nginx) 뒤에서 HTTPS를 쓰면 여기선 http로 보일 수 있음 → 헤더로 판단
        String forwardedProto = req.getHeader("X-Forwarded-Proto");
        if (forwardedProto != null) {
            return "https".equalsIgnoreCase(forwardedProto);
        }
        return req.isSecure();
    }
}