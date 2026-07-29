package com.example.newsmanagementsystem.googleauth.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.newsmanagementsystem.GoogleAuth.Controller.GoogleAuthController;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

class GoogleAuthControllerTest {

  @Test
  void explainsHowToOpenTheLoginEndpoint() {
    // Arrange
    GoogleAuthController controller = new GoogleAuthController();

    // Act
    Map<String, String> response = controller.home();

    // Assert
    assertThat(response).containsExactly(
        Map.entry("message", "Open /api/me to log in with Google")
    );
  }

  @Test
  void returnsTheCurrentGoogleUsersDetails() {
    // Arrange
    OidcUser oidcUser = mock(OidcUser.class);
    when(oidcUser.getSubject()).thenReturn("google-subject-123");
    when(oidcUser.getFullName()).thenReturn("Hammad Tallat");
    when(oidcUser.getEmail()).thenReturn("hammad@example.com");
    when(oidcUser.getEmailVerified()).thenReturn(true);
    when(oidcUser.getPicture()).thenReturn("https://example.com/picture.png");
    Authentication authentication = new TestingAuthenticationToken(
        "hammad",
        null,
        List.of(new SimpleGrantedAuthority("ROLE_USER"))
    );
    GoogleAuthController controller = new GoogleAuthController();

    // Act
    Map<String, Object> response = controller.currentUser(oidcUser, authentication);

    // Assert
    assertThat(response).containsExactly(
        Map.entry("googleSubject", "google-subject-123"),
        Map.entry("name", "Hammad Tallat"),
        Map.entry("email", "hammad@example.com"),
        Map.entry("emailVerified", true),
        Map.entry("picture", "https://example.com/picture.png"),
        Map.entry("authenticationName", "hammad"),
        Map.entry("authorities", List.of("ROLE_USER"))
    );
  }
}
