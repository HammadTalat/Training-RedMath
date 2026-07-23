package com.example.newsmanagementsystem.security.service;

import com.example.newsmanagementsystem.user.entity.AppUser;
import com.example.newsmanagementsystem.user.entity.AuthProvider;
import com.example.newsmanagementsystem.user.entity.Role;
import com.example.newsmanagementsystem.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class JWTConfigServiceTests {

    @Autowired
    private JWTConfigService jwtConfigService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    void writesTokenAsJsonWithoutExposingClaimValues() throws Exception {
        String username = "\"}</script><script>alert(1)</script>";
        AppUser user = new AppUser();
        user.setAuthProvider(AuthProvider.GOOGLE);
        user.setProviderUserId("xss-response-test");
        user.setUsername(username);
        user.setPasswordHash(UUID.randomUUID().toString());
        user.setRole(Role.REPORTER);
        user.setEnabled(true);
        userRepository.saveAndFlush(user);

        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtConfigService.generateToken(
                response,
                new UsernamePasswordAuthenticationToken(username, "password")
        );

        MediaType contentType = MediaType.parseMediaType(response.getContentType());
        assertThat(contentType.isCompatibleWith(MediaType.APPLICATION_JSON)).isTrue();
        assertThat(response.getCharacterEncoding()).isEqualTo("UTF-8");

        String responseBody = response.getContentAsString();
        JsonNode json = objectMapper.readTree(responseBody);
        assertThat(json.size()).isEqualTo(1);
        JsonNode accessTokenNode = json.get("accesstoken");
        assertThat(accessTokenNode).isNotNull();

        String token = accessTokenNode.stringValue();
        assertThat(token).isNotNull();
        assertThat(token).matches(
                "[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+"
        );
        assertThat(responseBody).doesNotContain(username, "<script>");
        assertThat(jwtConfigService.verify(token).getName()).isEqualTo(username);
    }
}
