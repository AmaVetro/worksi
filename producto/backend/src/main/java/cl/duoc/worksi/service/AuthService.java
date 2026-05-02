package cl.duoc.worksi.service;

import cl.duoc.worksi.dto.auth.LockInfoResponse;
import cl.duoc.worksi.dto.auth.LoginResponse;
import cl.duoc.worksi.dto.auth.UserInfoResponse;
import cl.duoc.worksi.entity.User;
import cl.duoc.worksi.repository.UserRepository;
import cl.duoc.worksi.security.JwtService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  @Value("${worksi.jwt.expiration-seconds}")
  private int expirationSeconds;

  @Value("${worksi.security.max-failed-login-attempts}")
  private int maxFailedAttempts;

  @Value("${worksi.security.lock-duration-minutes}")
  private int lockDurationMinutes;

  public AuthService(
      UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
  }

  @Transactional
  public ResponseEntity<?> login(String emailRaw, String password) {
    String email = emailRaw == null ? "" : emailRaw.trim();
    if (email.isEmpty() || password == null || password.isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(
              Map.of(
                  "error",
                  Map.of(
                      "code",
                      "VALIDATION_ERROR",
                      "message",
                      "Hay campos invalidos",
                      "details",
                      List.of(),
                      "trace_id",
                      "")));
    }

    Optional<User> opt = userRepository.findByEmailIgnoreCase(email);
    if (opt.isEmpty()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(
              Map.of(
                  "error",
                  Map.of(
                      "code",
                      "UNAUTHORIZED",
                      "message",
                      "Credenciales invalidas",
                      "details",
                      List.of(),
                      "trace_id",
                      "")));
    }

    User user = opt.get();
    if (user.getDeletedAt() != null || !user.isActive()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(
              Map.of(
                  "error",
                  Map.of(
                      "code",
                      "UNAUTHORIZED",
                      "message",
                      "Credenciales invalidas",
                      "details",
                      List.of(),
                      "trace_id",
                      "")));
    }

    LocalDateTime now = LocalDateTime.now();
    if (user.getLockUntil() != null && user.getLockUntil().isAfter(now)) {
      return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
          .body(
              Map.of(
                  "error",
                  Map.of(
                      "code",
                      "BUSINESS_RULE_VIOLATION",
                      "message",
                      "Cuenta bloqueada. Use recuperacion de contrasena.",
                      "details",
                      List.of(),
                      "trace_id",
                      "")));
    }

    if (user.getLockUntil() != null && !user.getLockUntil().isAfter(now)) {
      user.setFailedLoginAttempts(0);
      user.setLockUntil(null);
    }

    if (passwordEncoder.matches(password, user.getPasswordHash())) {
      user.setFailedLoginAttempts(0);
      user.setLockUntil(null);
      user.setLastLoginAt(now);
      userRepository.save(user);
      String token = jwtService.createToken(user);
      LockInfoResponse lockInfo = buildLockInfo(user, maxFailedAttempts, now);
      LoginResponse body =
          new LoginResponse(
              token,
              "Bearer",
              expirationSeconds,
              new UserInfoResponse(
                  user.getId(), user.getRole().name(), user.getEmail()),
              lockInfo);
      return ResponseEntity.ok(body);
    }

    int attempts = user.getFailedLoginAttempts() + 1;
    user.setFailedLoginAttempts(attempts);
    if (attempts >= maxFailedAttempts) {
      user.setLockUntil(now.plusMinutes(lockDurationMinutes));
      userRepository.save(user);
      return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
          .body(
              Map.of(
                  "error",
                  Map.of(
                      "code",
                      "BUSINESS_RULE_VIOLATION",
                      "message",
                      "Cuenta bloqueada tras intentos fallidos",
                      "details",
                      List.of(),
                      "trace_id",
                      "")));
    }
    userRepository.save(user);
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(
            Map.of(
                "error",
                Map.of(
                    "code",
                    "UNAUTHORIZED",
                    "message",
                    "Credenciales invalidas",
                    "details",
                    List.of(),
                    "trace_id",
                    "")));
  }

  private static LockInfoResponse buildLockInfo(User user, int maxAttempts, LocalDateTime now) {
    boolean locked = user.getLockUntil() != null && user.getLockUntil().isAfter(now);
    int failed = user.getFailedLoginAttempts();
    int remaining = locked ? 0 : Math.max(0, maxAttempts - failed);
    return new LockInfoResponse(locked, failed, remaining);
  }
}
