package cl.duoc.worksi.security;

import cl.duoc.worksi.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
  private final SecretKey key;
  private final int expirationSeconds;
  private final int recoveryExpirationSeconds;

  public JwtService(
      @Value("${worksi.jwt.secret}") String secret,
      @Value("${worksi.jwt.expiration-seconds}") int expirationSeconds,
      @Value("${worksi.jwt.recovery-expiration-seconds}") int recoveryExpirationSeconds) {
    this.key = Keys.hmacShaKeyFor(sha256(secret));
    this.expirationSeconds = expirationSeconds;
    this.recoveryExpirationSeconds = recoveryExpirationSeconds;
  }

  private static byte[] sha256(String secret) {
    try {
      return MessageDigest.getInstance("SHA-256").digest(secret.getBytes(StandardCharsets.UTF_8));
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException(e);
    }
  }

  public String createToken(User user) {
    Instant now = Instant.now();
    Instant exp = now.plusSeconds(expirationSeconds);
    return Jwts.builder()
        .subject(String.valueOf(user.getId()))
        .claim("email", user.getEmail())
        .claim("role", user.getRole().name())
        .issuedAt(Date.from(now))
        .expiration(Date.from(exp))
        .signWith(key)
        .compact();
  }

  public Claims parseAndValidate(String token) {
    return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
  }

  public String createPasswordRecoveryToken(User user) {
    Instant now = Instant.now();
    Instant exp = now.plusSeconds(recoveryExpirationSeconds);
    return Jwts.builder()
        .subject(String.valueOf(user.getId()))
        .claim("email", user.getEmail())
        .claim("purpose", "PASSWORD_RECOVERY")
        .issuedAt(Date.from(now))
        .expiration(Date.from(exp))
        .signWith(key)
        .compact();
  }
}
