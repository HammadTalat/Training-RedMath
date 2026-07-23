package com.example.newsmanagementsystem.security.service;

import com.example.newsmanagementsystem.user.entity.AppUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Map;


@Service
public final class JWTConfigService {

    NimbusJwtDecoder decoder;
    NimbusJwtEncoder encoder;
    DatabaseUserDetailsService service;
    private final ObjectMapper objectMapper;

    JWTConfigService(
            DatabaseUserDetailsService service,
            ObjectMapper objectMapper
    ) throws NoSuchAlgorithmException {
        this.service = service;
        this.objectMapper = objectMapper;
        SecretKey key = KeyGenerator.getInstance("HmacSHA256").generateKey();
        encoder = NimbusJwtEncoder.withSecretKey(key).algorithm(MacAlgorithm.HS256).build();
        decoder = NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();

    }

    public void onAuthLoginForm(HttpServletRequest request, HttpServletResponse response, Authentication auth) throws IOException {
        generateToken(response, auth);
    }

    public void onAuth2Login(HttpServletRequest request, HttpServletResponse response, AppUser user) throws IOException {
        generateToken(response, user);

    }

    public void generateToken(HttpServletResponse response, Authentication auth) throws IOException {
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        AppUser user = service.generateToken(auth.getName());
        JwtClaimsSet set = JwtClaimsSet.builder().id(user.getAccessToken()).subject(user.getUsername()).
                claim("scope", user.getRole()).issuedAt(Instant.now()).expiresAt(Instant.now().plusSeconds(3000)).build();
        Jwt jwt = encoder.encode(JwtEncoderParameters.from(header, set));
        writeTokenResponse(response, jwt.getTokenValue());
    }

    public void generateToken(HttpServletResponse response, AppUser u) throws IOException {
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        AppUser user = service.generateToken(u.getUsername());
        JwtClaimsSet set = JwtClaimsSet.builder().id(user.getAccessToken()).subject(user.getUsername()).
                claim("scope", user.getRole()).issuedAt(Instant.now()).expiresAt(Instant.now().plusSeconds(3000)).build();
        Jwt jwt = encoder.encode(JwtEncoderParameters.from(header, set));
        writeTokenResponse(response, jwt.getTokenValue());
    }

    private void writeTokenResponse(HttpServletResponse response, String token) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        objectMapper.writeValue(response.getOutputStream(), Map.of("accesstoken", token));
    }

    public DefaultOAuth2AuthenticatedPrincipal verify(String token) {
        Jwt jwt = decoder.decode(token);
        String username = jwt.getSubject();
        String role = jwt.getClaimAsString("scope");
        return new DefaultOAuth2AuthenticatedPrincipal(username, Map.of("sub", username), AuthorityUtils.createAuthorityList(

                "ROLE_" + role
        ));
    }
}
