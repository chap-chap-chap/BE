package org.chapchap.be.global.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {

    private CookieUtil() {}

    public static void addHttpOnlyCookie(HttpServletResponse res, String name, String value, int maxAgeSec) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);        // prod만 true, dev/local은 false
        cookie.setPath("/");
        cookie.setMaxAge(maxAgeSec);
        res.addCookie(cookie);
    }
}