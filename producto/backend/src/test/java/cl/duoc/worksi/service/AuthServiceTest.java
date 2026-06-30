package cl.duoc.worksi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cl.duoc.worksi.dto.auth.LoginResponse;
import cl.duoc.worksi.entity.User;
import cl.duoc.worksi.entity.enums.UserRole;
import cl.duoc.worksi.repository.RecruiterProfileRepository;
import cl.duoc.worksi.repository.UserRepository;
import cl.duoc.worksi.security.JwtService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  private static final String EMAIL = "auth@test.cl";
  private static final String PASSWORD = "Aa!1234567890";

  @Mock private UserRepository userRepository;
  @Mock private RecruiterProfileRepository recruiterProfileRepository;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private JwtService jwtService;

  private AuthService authService;

  @BeforeEach
  void setUp() {
    authService =
        new AuthService(
            userRepository, recruiterProfileRepository, passwordEncoder, jwtService);
    ReflectionTestUtils.setField(authService, "expirationSeconds", 3600);
    ReflectionTestUtils.setField(authService, "maxFailedAttempts", 4);
    ReflectionTestUtils.setField(authService, "lockDurationMinutes", 30);
  }

  @Test
  void loginReturnsTokenForValidCredentials() {
    User user = activeUser(0);
    when(userRepository.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(PASSWORD, user.getPasswordHash())).thenReturn(true);
    when(jwtService.createToken(user)).thenReturn("jwt-token");
    when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

    ResponseEntity<?> response = authService.login(EMAIL, PASSWORD);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    LoginResponse body = (LoginResponse) response.getBody();
    assertEquals("jwt-token", body.getAccessToken());
    assertEquals("CANDIDATE", body.getUser().getRole());
  }

  @Test
  void locksAccountAfterMaxFailedAttempts() {
    User user = activeUser(3);
    when(userRepository.findByEmailIgnoreCase(EMAIL)).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("wrong", user.getPasswordHash())).thenReturn(false);
    when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

    ResponseEntity<?> response = authService.login(EMAIL, "wrong");

    assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
    assertEquals(4, user.getFailedLoginAttempts());
    assertNotNull(user.getLockUntil());
    verify(userRepository).save(user);
  }

  private static User activeUser(int failedAttempts) {
    User user = new User();
    ReflectionTestUtils.setField(user, "id", 10L);
    user.setRole(UserRole.CANDIDATE);
    user.setEmail(EMAIL);
    user.setPasswordHash("hash");
    user.setActive(true);
    user.setPasswordResetRequired(false);
    user.setFailedLoginAttempts(failedAttempts);
    return user;
  }
}
