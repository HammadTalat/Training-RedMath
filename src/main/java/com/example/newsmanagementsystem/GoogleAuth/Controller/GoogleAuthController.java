package com.example.newsmanagementsystem.GoogleAuth.Controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation
        .AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GoogleAuthController {

    @GetMapping("/")
    public Map<String, String> home() {
        return Map.of(
                "message",
                "Open /api/me to log in with Google"
        );
    }

    @GetMapping("/api/me")
    public Map<String, Object> currentUser(
            @AuthenticationPrincipal OidcUser oidcUser,
            Authentication authentication
    ) {
        Map<String, Object> response =
                new LinkedHashMap<>();

        /*
         * Google's permanent unique identifier for this
         * user within the Google identity system.
         */
        response.put(
                "googleSubject",
                oidcUser.getSubject()
        );

        response.put(
                "name",
                oidcUser.getFullName()
        );

        response.put(
                "email",
                oidcUser.getEmail()
        );

        response.put(
                "emailVerified",
                oidcUser.getEmailVerified()
        );

        response.put(
                "picture",
                oidcUser.getPicture()
        );

        response.put(
                "authenticationName",
                authentication.getName()
        );

        response.put(
                "authorities",
                authentication.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList()
        );
        //Testing

        return response;
    }
}