package com.example.newsmanagementsystem.security.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.example.newsmanagementsystem.user.entity.AppUser;
import com.example.newsmanagementsystem.user.entity.Role;
import com.example.newsmanagementsystem.user.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class DatabaseUserDetailsServiceTest {

  private static final String PASSWORD_HASH = "{noop}password";
  private static final String USERNAME = "hammad";

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private DatabaseUserDetailsService userDetailsService;

  @Test
  void loadsAnEnabledUserFromTheDatabase() {
    // Arrange
    AppUser appUser = createUser(true);
    when(userRepository.findByUsernameIgnoreCase(USERNAME))
        .thenReturn(Optional.of(appUser));

    // Act
    UserDetails result = userDetailsService.loadUserByUsername(USERNAME);

    // Assert
    assertThat(result)
        .extracting(
            UserDetails::getUsername,
            UserDetails::getPassword,
            UserDetails::isEnabled
        )
        .containsExactly(USERNAME, PASSWORD_HASH, true);
  }

  @Test
  void marksDisabledDatabaseUserAsDisabled() {
    // Arrange
    AppUser appUser = createUser(false);
    when(userRepository.findByUsernameIgnoreCase(USERNAME))
        .thenReturn(Optional.of(appUser));

    // Act
    UserDetails result = userDetailsService.loadUserByUsername(USERNAME);

    // Assert
    assertThat(result.isEnabled()).isFalse();
  }

  @Test
  void rejectsAnUnknownUsername() {
    // Arrange
    when(userRepository.findByUsernameIgnoreCase(USERNAME))
        .thenReturn(Optional.empty());

    // Act and assert
    assertThatThrownBy(() -> userDetailsService.loadUserByUsername(USERNAME))
        .isInstanceOf(UsernameNotFoundException.class)
        .hasMessage("Invalid username or password");
  }

  @Test
  void savesUserWithNewAccessToken() {
    // Arrange
    AppUser appUser = createUser(true);
    AppUser savedUser = createUser(true);
    when(userRepository.findByUsernameIgnoreCase(USERNAME))
        .thenReturn(Optional.of(appUser));
    when(userRepository.save(appUser)).thenReturn(savedUser);

    // Act
    AppUser result = userDetailsService.generateToken(USERNAME);
    UUID parsedToken = UUID.fromString(appUser.getAccessToken());

    // Assert
    assertThat(new TokenOutcome(result, parsedToken.toString()))
        .isEqualTo(new TokenOutcome(savedUser, appUser.getAccessToken()));
  }

  @Test
  void cannotGenerateTokenForUnknownUsername() {
    // Arrange
    when(userRepository.findByUsernameIgnoreCase(USERNAME))
        .thenReturn(Optional.empty());

    // Act and assert
    assertThatThrownBy(() -> userDetailsService.generateToken(USERNAME))
        .isInstanceOf(UsernameNotFoundException.class)
        .hasMessage("Invalid username or password");
  }

  private static AppUser createUser(boolean enabled) {
    AppUser appUser = new AppUser();
    appUser.setUsername(USERNAME);
    appUser.setPasswordHash(PASSWORD_HASH);
    appUser.setRole(Role.USER);
    appUser.setEnabled(enabled);
    return appUser;
  }

  private record TokenOutcome(AppUser returnedUser, String accessToken) {
  }
}
