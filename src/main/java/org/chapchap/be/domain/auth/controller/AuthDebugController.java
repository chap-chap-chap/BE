package org.chapchap.be.domain.auth.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/debug")
public class AuthDebugController {
    @GetMapping("/me")
    public Map<String, Object> me(Authentication auth) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("authenticated", auth != null);
        if (auth != null) {
            map.put("name", auth.getName());
            map.put("authorities", auth.getAuthorities());
        }
        return map;
    }
}