package cl.duoc.worksi.service;

import cl.duoc.worksi.entity.User;
import cl.duoc.worksi.repository.UserRepository;
import cl.duoc.worksi.security.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import cl.duoc.worksi.validation.PasswordRules;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PasswordRecoveryService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final int codeValidityMinutes;
  private final boolean mvpSkipCodeCheck;

  public PasswordRecoveryService(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      JwtService jwtService,
      @Value("${worksi.security.password-recovery-code-validity-minutes}")
          int codeValidityMinutes,
      @Value("${worksi.security.password-recovery-mvp-skip-code-check:true}")
          boolean mvpSkipCodeCheck) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
    this.codeValidityMinutes = codeValidityMinutes;
    this.mvpSkipCodeCheck = mvpSkipCodeCheck;
  }

  @Transactional
  public ResponseEntity<?> requestCode(String emailRaw) {
    String email = normalizeEmail(emailRaw);
    if (email.isEmpty()) {
      return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Hay campos invalidos");
    }
    Optional<User> opt = userRepository.findByEmailIgnoreCase(email);
    if (opt.isEmpty()) {
      return error(HttpStatus.NOT_FOUND, "NOT_FOUND", "No existe una cuenta con este correo");
    }
    User user = opt.get();
    if (user.getDeletedAt() != null || !user.isActive()) {
      return error(HttpStatus.NOT_FOUND, "NOT_FOUND", "No existe una cuenta con este correo");
    }

    LocalDateTime now = LocalDateTime.now();
    user.setPasswordResetRequestedAt(now);
    user.setPasswordResetCodeExpiresAt(now.plusMinutes(codeValidityMinutes));
    if (mvpSkipCodeCheck) {
      user.setPasswordResetCodeHash(null);
    } else {
      SecureRandom random = new SecureRandom();
      String plainCode = String.format("%06d", 100000 + random.nextInt(900000));
      user.setPasswordResetCodeHash(passwordEncoder.encode(plainCode));
    }
    userRepository.save(user);

    return ResponseEntity.ok(Map.of("message", "Codigo generado en modo mock", "mock_flow", true));
  }

  @Transactional
  public ResponseEntity<?> verifyCode(String emailRaw, String codeRaw) {
    String email = normalizeEmail(emailRaw);
    if (email.isEmpty() || codeRaw == null || codeRaw.isBlank()) {
      return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Hay campos invalidos");
    }
    Optional<User> opt = userRepository.findByEmailIgnoreCase(email);
    if (opt.isEmpty()) {
      return error(HttpStatus.NOT_FOUND, "NOT_FOUND", "No existe una cuenta con este correo");
    }
    User user = opt.get();
    if (user.getDeletedAt() != null || !user.isActive()) {
      return error(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Codigo invalido o vencido");
    }
    LocalDateTime exp = user.getPasswordResetCodeExpiresAt();
    LocalDateTime now = LocalDateTime.now();
    if (user.getPasswordResetRequestedAt() == null || exp == null || now.isAfter(exp)) {
      return error(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Codigo invalido o vencido");
    }
    if (!mvpSkipCodeCheck) {
      String hash = user.getPasswordResetCodeHash();
      if (hash == null || !passwordEncoder.matches(codeRaw.trim(), hash)) {
        return error(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Codigo invalido o vencido");
      }
    }
    String recoveryJwt = jwtService.createPasswordRecoveryToken(user);
    return ResponseEntity.ok(Map.of("verified", true, "recovery_token", recoveryJwt));
  }

  @Transactional
  public ResponseEntity<?> resetPassword(String emailRaw, String recoveryToken, String newPassword) {
    String email = normalizeEmail(emailRaw);
    if (email.isEmpty()
        || recoveryToken == null
        || recoveryToken.isBlank()
        || newPassword == null
        || newPassword.isEmpty()) {
      return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Hay campos invalidos");
    }
    if (!PasswordRules.matches(newPassword)) {
      return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "La contrasena no cumple la politica");
    }

    Claims claims;
    try {
      claims = jwtService.parseAndValidate(recoveryToken.trim());
    } catch (JwtException | IllegalArgumentException e) {
      return error(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Token de recuperacion invalido");
    }
    if (!"PASSWORD_RECOVERY".equals(claims.get("purpose", String.class))) {
      return error(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Token de recuperacion invalido");
    }
    String claimEmail = claims.get("email", String.class);
    if (claimEmail == null || !claimEmail.equalsIgnoreCase(email)) {
      return error(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Token de recuperacion invalido");
    }

    long userId;
    try {
      userId = Long.parseLong(claims.getSubject());
    } catch (NumberFormatException e) {
      return error(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Token de recuperacion invalido");
    }

    Optional<User> opt = userRepository.findById(userId);
    if (opt.isEmpty()) {
      return error(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Token de recuperacion invalido");
    }
    User user = opt.get();
    if (user.getDeletedAt() != null || !user.isActive()) {
      return error(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Token de recuperacion invalido");
    }
    if (!user.getEmail().equalsIgnoreCase(email)) {
      return error(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Token de recuperacion invalido");
    }

    LocalDateTime now = LocalDateTime.now();
    user.setPasswordHash(passwordEncoder.encode(newPassword));
    user.setPasswordResetCodeHash(null);
    user.setPasswordResetCodeExpiresAt(null);
    user.setPasswordResetRequestedAt(null);
    user.setPasswordChangedAt(now);
    user.setFailedLoginAttempts(0);
    user.setLockUntil(null);
    userRepository.save(user);

    return ResponseEntity.ok(Map.of("message", "Contrasena actualizada"));
  }

  private static String normalizeEmail(String emailRaw) {
    if (emailRaw == null) {
      return "";
    }
    return emailRaw.trim();
  }

  private ResponseEntity<Map<String, Object>> error(HttpStatus status, String code, String message) {
    return ResponseEntity.status(status)
        .body(
            Map.of(
                "error",
                Map.of(
                    "code",
                    code,
                    "message",
                    message,
                    "details",
                    List.of(),
                    "trace_id",
                    "")));
  }
}
