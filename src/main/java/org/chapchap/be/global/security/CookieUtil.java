package org.chapchap.be.global.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    private final String cookieDomain;

    public CookieUtil(@Value("${app.web.cookie-domain:}") String cookieDomain) {
        this.cookieDomain = cookieDomain;
    }

    /**
     * 운영(HTTPS, cross-site): SameSite=None, Secure=true
     * 동일 오리진이더라도 운영은 https 고정으로 Secure=true 권장
     */
    public void addAccessTokenCookie(HttpServletRequest req, HttpServletResponse res,
                                     String name, String value, int maxAgeSec, boolean crossSite) {
        boolean https = isSecure(req); // X-Forwarded-Proto 고려
        ResponseCookie.ResponseCookieBuilder b = ResponseCookie.from(name, value)
                .httpOnly(true)
                .path("/")
                .maxAge(maxAgeSec);

        if (crossSite) {
            // 크로스 오리진은 반드시 None + Secure
            b.sameSite("None").secure(true);
            if (cookieDomain != null && !cookieDomain.isBlank()) {
                b.domain(cookieDomain); // 예: .shallwewalk.kro.kr
            }
        } else {
            // 동일 오리진(운영 https): Lax + Secure(true)
            b.sameSite("Lax").secure(https);
        }

        res.addHeader("Set-Cookie", b.build().toString());
    }

    private static boolean isSecure(HttpServletRequest req) {
        String forwardedProto = req.getHeader("X-Forwarded-Proto");
        if (forwardedProto != null) return "https".equalsIgnoreCase(forwardedProto);
        return req.isSecure();
    }
}
