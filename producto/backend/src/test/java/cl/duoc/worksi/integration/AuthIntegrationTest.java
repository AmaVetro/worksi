package cl.duoc.worksi.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cl.duoc.worksi.dto.auth.LoginRequest;
import cl.duoc.worksi.dto.auth.LoginResponse;
import cl.duoc.worksi.entity.User;
import cl.duoc.worksi.entity.enums.UserRole;
import cl.duoc.worksi.repository.UserRepository;
import cl.duoc.worksi.service.AuthService;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Tag("integration")
@EnabledIf("cl.duoc.worksi.integration.IntegrationTestSupport#isEnabled")
class AuthIntegrationTest {

  @Container
  static final MySQLContainer<?> MYSQL =
      new MySQLContainer<>("mysql:8.0")
          .withDatabaseName("worksi")
          .withUsername("worksi")
          .withPassword("worksi");

  @LocalServerPort int port;

  @Autowired TestRestTemplate restTemplate;
  @Autowired UserRepository userRepository;
  @Autowired PasswordEncoder passwordEncoder;
  @Autowired AuthService authService;

  String baseUrl;

  @DynamicPropertySource
  static void registerProps(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
    registry.add("spring.datasource.username", MYSQL::getUsername);
    registry.add("spring.datasource.password", MYSQL::getPassword);
    registry.add("worksi.ai.enabled", () -> "false");
  }

  @BeforeEach
  void baseUrl() {
    baseUrl = "http://localhost:" + port;
    restTemplate
        .getRestTemplate()
        .setErrorHandler(
            new DefaultResponseErrorHandler() {
              @Override
              public boolean hasError(ClientHttpResponse response) {
                return false;
              }
            });
  }

  @Test
  void loginReturnsJwtWithExpectedRole() {
    String email = "auth-ok-" + UUID.randomUUID() + "@worksi.test";
    saveUser(email, UserRole.CANDIDATE, IntegrationFixtures.VALID_PASSWORD, 0);

    ResponseEntity<LoginResponse> response = postLogin(email, IntegrationFixtures.VALID_PASSWORD);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertNotNull(response.getBody().getAccessToken());
    assertEquals("CANDIDATE", response.getBody().getUser().getRole());
    assertEquals(email, response.getBody().getUser().getEmail());
  }

  @Test
  void recruiterCannotAccessAdminEndpoints() {
    String email = "auth-rec-" + UUID.randomUUID() + "@worksi.test";
    saveUser(email, UserRole.RECRUITER, IntegrationFixtures.VALID_PASSWORD, 0);

    LoginResponse login = postLogin(email, IntegrationFixtures.VALID_PASSWORD).getBody();
    assertNotNull(login);

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(login.getAccessToken());
    ResponseEntity<String> response =
        restTemplate.exchange(
            baseUrl + "/api/v1/admin/companies?page=1&size=5",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            String.class);

    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
  }

  @Test
  void accountLocksAfterMaxFailedAttempts() {
    String email = "auth-lock-" + UUID.randomUUID() + "@worksi.test";
    saveUser(email, UserRole.CANDIDATE, IntegrationFixtures.VALID_PASSWORD, 0);

    assertEquals(HttpStatus.UNAUTHORIZED, authService.login(email, "wrong-pass-1").getStatusCode());
    assertEquals(HttpStatus.UNAUTHORIZED, authService.login(email, "wrong-pass-2").getStatusCode());
    assertEquals(HttpStatus.UNAUTHORIZED, authService.login(email, "wrong-pass-3").getStatusCode());

    ResponseEntity<?> fourth = authService.login(email, "wrong-pass-4");
    assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, fourth.getStatusCode());
    assertTrue(fourth.getBody() instanceof Map);
    Map<?, ?> body = (Map<?, ?>) fourth.getBody();
    Map<?, ?> error = (Map<?, ?>) body.get("error");
    assertNotNull(error);
    assertEquals("BUSINESS_RULE_VIOLATION", error.get("code"));

    ResponseEntity<?> whileLocked =
        authService.login(email, IntegrationFixtures.VALID_PASSWORD);
    assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, whileLocked.getStatusCode());
  }

  private void saveUser(String email, UserRole role, String password, int failedAttempts) {
    User user = new User();
    user.setRole(role);
    user.setEmail(email);
    user.setPasswordHash(passwordEncoder.encode(password));
    user.setActive(true);
    user.setPasswordResetRequired(false);
    user.setFailedLoginAttempts(failedAttempts);
    userRepository.saveAndFlush(user);
  }

  private ResponseEntity<LoginResponse> postLogin(String email, String password) {
    LoginRequest request = new LoginRequest();
    request.setEmail(email);
    request.setPassword(password);
    return restTemplate.postForEntity(baseUrl + "/api/v1/auth/login", request, LoginResponse.class);
  }
}
