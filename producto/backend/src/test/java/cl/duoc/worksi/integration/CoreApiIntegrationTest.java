package cl.duoc.worksi.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cl.duoc.worksi.dto.PageResponse;
import cl.duoc.worksi.dto.candidate.CandidateApplicationCreatedResponse;
import cl.duoc.worksi.dto.candidate.CandidateApplicationRequest;
import cl.duoc.worksi.dto.company.CompanyJobApplicationItemResponse;
import cl.duoc.worksi.dto.messaging.CreateConversationRequest;
import cl.duoc.worksi.dto.messaging.CreateConversationResponse;
import cl.duoc.worksi.dto.messaging.MessageItemResponse;
import cl.duoc.worksi.dto.messaging.SendMessageRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import cl.duoc.worksi.repository.CandidateCvRepository;
import cl.duoc.worksi.repository.CandidatePreferredModalityRepository;
import cl.duoc.worksi.repository.CandidatePreferredWorkloadRepository;
import cl.duoc.worksi.repository.CandidateProfileRepository;
import cl.duoc.worksi.repository.CandidateSkillRepository;
import cl.duoc.worksi.repository.CompanyRepository;
import cl.duoc.worksi.repository.CommuneRepository;
import cl.duoc.worksi.repository.JobRepository;
import cl.duoc.worksi.repository.JobSkillRepository;
import cl.duoc.worksi.repository.RecruiterProfileRepository;
import cl.duoc.worksi.repository.RegionRepository;
import cl.duoc.worksi.repository.UserRepository;
import cl.duoc.worksi.security.JwtService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Tag("integration")
@EnabledIf("cl.duoc.worksi.integration.IntegrationTestSupport#isEnabled")
class CoreApiIntegrationTest {

  @Container
  static final MySQLContainer<?> MYSQL =
      new MySQLContainer<>("mysql:8.0")
          .withDatabaseName("worksi")
          .withUsername("worksi")
          .withPassword("worksi");

  static Path cvStorageDir;

  @LocalServerPort int port;

  @Autowired TestRestTemplate restTemplate;
  @Autowired UserRepository userRepository;
  @Autowired CandidateProfileRepository candidateProfileRepository;
  @Autowired CandidatePreferredModalityRepository candidatePreferredModalityRepository;
  @Autowired CandidatePreferredWorkloadRepository candidatePreferredWorkloadRepository;
  @Autowired CandidateCvRepository candidateCvRepository;
  @Autowired CandidateSkillRepository candidateSkillRepository;
  @Autowired CompanyRepository companyRepository;
  @Autowired RecruiterProfileRepository recruiterProfileRepository;
  @Autowired JobRepository jobRepository;
  @Autowired JobSkillRepository jobSkillRepository;
  @Autowired RegionRepository regionRepository;
  @Autowired CommuneRepository communeRepository;
  @Autowired JwtService jwtService;
  @Autowired PasswordEncoder passwordEncoder;

  String baseUrl;
  IntegrationFixtures.SeededCandidate candidate;
  IntegrationFixtures.SeededRecruiter recruiter;
  long jobId;

  @BeforeAll
  static void startInfrastructure() throws IOException {
    cvStorageDir = Files.createTempDirectory("worksi-it-core");
    IntegrationTestSupport.startAiContainerIfNeeded();
  }

  @AfterAll
  static void stopInfrastructure() {
    IntegrationTestSupport.stopAiContainerIfStarted();
  }

  @DynamicPropertySource
  static void registerProps(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
    registry.add("spring.datasource.username", MYSQL::getUsername);
    registry.add("spring.datasource.password", MYSQL::getPassword);
    registry.add("worksi.storage.candidate-cvs", () -> cvStorageDir.toString());
    registry.add("worksi.ai.enabled", () -> "true");
    registry.add("worksi.ai.base-url", IntegrationTestSupport::resolveAiBaseUrl);
  }

  @BeforeEach
  void seedScenario() {
    baseUrl = "http://localhost:" + port;
    IntegrationFixtures.RegionCommune rc =
        IntegrationFixtures.resolveRegionCommune(regionRepository, communeRepository);
    candidate =
        IntegrationFixtures.seedCandidate(
            userRepository,
            candidateProfileRepository,
            candidatePreferredModalityRepository,
            candidatePreferredWorkloadRepository,
            candidateCvRepository,
            candidateSkillRepository,
            passwordEncoder,
            jwtService,
            rc,
            cvStorageDir,
            List.of(1L, 2L, 3L));
    recruiter =
        IntegrationFixtures.seedRecruiter(
            userRepository,
            recruiterProfileRepository,
            companyRepository,
            passwordEncoder,
            jwtService,
            rc);
    jobId =
        IntegrationFixtures.seedJob(
            jobRepository,
            jobSkillRepository,
            recruiter.company(),
            recruiter.user().getId(),
            rc,
            "Desarrollador Java Backend",
            "Buscamos desarrollador Java con Spring Boot, APIs REST, microservicios, MySQL, Git, "
                + "JUnit y experiencia en backend empresarial.",
            List.of(1L, 2L, 3L));
  }

  @Test
  void candidateCanApplyAndRecruiterListsApplicationWithScore() {
    CandidateApplicationCreatedResponse application = applyToJob(jobId);

    assertNotNull(application.applicationId());
    assertEquals("APPLIED", application.status());

    HttpHeaders headers = authHeaders(recruiter.token());
    ResponseEntity<PageResponse<CompanyJobApplicationItemResponse>> listResponse =
        restTemplate.exchange(
            baseUrl
                + "/api/v1/company/jobs/"
                + jobId
                + "/applications?page=1&size=20&sort=match_score,desc",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<PageResponse<CompanyJobApplicationItemResponse>>() {});

    assertEquals(HttpStatus.OK, listResponse.getStatusCode());
    assertNotNull(listResponse.getBody());
    assertFalse(listResponse.getBody().getItems().isEmpty());
    CompanyJobApplicationItemResponse item = listResponse.getBody().getItems().get(0);
    assertEquals(application.applicationId(), item.getApplicationId());
    assertNotNull(item.getMatchScore());
    assertTrue(item.getMatchScore() > 0.0);
    assertNotNull(item.getMatchBreakdown());
  }

  @Test
  void recruiterCanOpenConversationAndExchangeMessages() {
    CandidateApplicationCreatedResponse application = applyToJob(jobId);

    CreateConversationRequest createRequest = new CreateConversationRequest();
    createRequest.setApplicationId(application.applicationId());
    createRequest.setFirstMessage("Hola, nos interesa tu perfil para la vacante.");

    HttpHeaders recruiterHeaders = authHeaders(recruiter.token());
    ResponseEntity<CreateConversationResponse> createResponse =
        restTemplate.exchange(
            baseUrl + "/api/v1/messaging/conversations",
            HttpMethod.POST,
            new HttpEntity<>(createRequest, recruiterHeaders),
            CreateConversationResponse.class);

    assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
    assertNotNull(createResponse.getBody());
    long conversationId = createResponse.getBody().getConversationId();
    assertNotNull(createResponse.getBody().getFirstMessage());

    SendMessageRequest sendRequest = new SendMessageRequest();
    sendRequest.setBody("Gracias, quedo atento a los siguientes pasos.");
    ResponseEntity<MessageItemResponse> candidateMessage =
        restTemplate.exchange(
            baseUrl + "/api/v1/messaging/conversations/" + conversationId + "/messages",
            HttpMethod.POST,
            new HttpEntity<>(sendRequest, authHeaders(candidate.token())),
            MessageItemResponse.class);

    assertEquals(HttpStatus.CREATED, candidateMessage.getStatusCode());
    assertNotNull(candidateMessage.getBody());
    assertEquals("Gracias, quedo atento a los siguientes pasos.", candidateMessage.getBody().getBody());
  }

  private CandidateApplicationCreatedResponse applyToJob(long targetJobId) {
    CandidateApplicationRequest request = new CandidateApplicationRequest(targetJobId);
    ResponseEntity<CandidateApplicationCreatedResponse> response =
        restTemplate.exchange(
            baseUrl + "/api/v1/candidate/applications",
            HttpMethod.POST,
            new HttpEntity<>(request, authHeaders(candidate.token())),
            CandidateApplicationCreatedResponse.class);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
    return response.getBody();
  }

  private HttpHeaders authHeaders(String token) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    return headers;
  }
}
