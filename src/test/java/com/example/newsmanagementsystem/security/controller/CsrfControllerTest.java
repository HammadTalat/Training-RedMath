package com.example.newsmanagementsystem.security.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.springframework.security.web.csrf.CsrfToken;

class CsrfControllerTest {

  @Test
  void returnsTheTokenProvidedBySpringSecurity() {
    // Arrange
    CsrfToken token = mock(CsrfToken.class);
    CsrfController controller = new CsrfController();

    // Act
    CsrfToken response = controller.getCsrfToken(token);

    // Assert
    assertThat(response).isSameAs(token);
  }
}
