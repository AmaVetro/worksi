package cl.duoc.worksi.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import cl.duoc.worksi.entity.User;
import cl.duoc.worksi.entity.enums.UserRole;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class JwtServiceTest {

  private JwtService jwtService;

  @BeforeEach
  void setUp() {
    jwtService =
        new JwtService(
            "worksi-dev-jwt-secret-key-min-32-chars-long-change-in-prod-ok", 3600, 900);
  }

  @Test
  void createTokenIncludesRoleAndEmailClaims() {
    User user = new User();
    ReflectionTestUtils.setField(user, "id", 99L);
    user.setEmail("candidato@worksi.test");
    user.setRole(UserRole.CANDIDATE);

    String token = jwtService.createToken(user);
    Claims claims = jwtService.parseAndValidate(token);

    assertEquals("99", claims.getSubject());
    assertEquals("CANDIDATE", claims.get("role", String.class));
    assertEquals("candidato@worksi.test", claims.get("email", String.class));
    assertNotNull(claims.getExpiration());
  }

  @Test
  void passwordRecoveryTokenIncludesPurposeClaim() {
    User user = new User();
    ReflectionTestUtils.setField(user, "id", 7L);
    user.setEmail("admin@worksi.test");
    user.setRole(UserRole.ADMIN);

    String token = jwtService.createPasswordRecoveryToken(user);
    Claims claims = jwtService.parseAndValidate(token);

    assertEquals("PASSWORD_RECOVERY", claims.get("purpose", String.class));
    assertEquals("admin@worksi.test", claims.get("email", String.class));
  }
}
