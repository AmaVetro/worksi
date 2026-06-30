package cl.duoc.worksi.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import cl.duoc.worksi.dto.candidate.CandidateJobDetailResponse;
import cl.duoc.worksi.entity.CandidateSkill;
import cl.duoc.worksi.entity.JobSkill;
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
class MatchInvariantIntegrationTest {

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
  String candidateToken;
  long candidateUserId;
  long jobId;

  @BeforeAll
  static void startInfrastructure() throws IOException {
    cvStorageDir = Files.createTempDirectory("worksi-it-invariant");
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
    IntegrationFixtures.SeededRecruiter recruiter =
        IntegrationFixtures.seedRecruiter(
            userRepository,
            recruiterProfileRepository,
            companyRepository,
            passwordEncoder,
            jwtService,
            rc);
    IntegrationFixtures.SeededCandidate candidate =
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
    candidateToken = candidate.token();
    candidateUserId = candidate.user().getId();
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
  void scoreUnchangedWhenOnlyCandidateProfileSkillsChange() {
    double baseline = fetchMatchScore();

    candidateSkillRepository.deleteAll(
        candidateSkillRepository.findByIdCandidateUserIdOrderByIdSkillIdAsc(candidateUserId));
    candidateSkillRepository.saveAndFlush(new CandidateSkill(candidateUserId, 4L));
    candidateSkillRepository.saveAndFlush(new CandidateSkill(candidateUserId, 5L));
    candidateSkillRepository.saveAndFlush(new CandidateSkill(candidateUserId, 6L));

    double afterSkillChange = fetchMatchScore();

    assertEquals(baseline, afterSkillChange, 0.05);
  }

  @Test
  void scoreUnchangedWhenOnlyJobSkillsChange() {
    double baseline = fetchMatchScore();

    jobSkillRepository.deleteAll(jobSkillRepository.findAllByJobIdOrderBySkillName(jobId));
    jobSkillRepository.saveAndFlush(new JobSkill(jobId, 7L));
    jobSkillRepository.saveAndFlush(new JobSkill(jobId, 8L));
    jobSkillRepository.saveAndFlush(new JobSkill(jobId, 9L));

    double afterJobSkillsChange = fetchMatchScore();

    assertEquals(baseline, afterJobSkillsChange, 0.05);
  }

  private double fetchMatchScore() {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(candidateToken);
    ResponseEntity<CandidateJobDetailResponse> response =
        restTemplate.exchange(
            baseUrl + "/api/v1/candidate/jobs/" + jobId,
            HttpMethod.GET,
            new HttpEntity<>(headers),
            CandidateJobDetailResponse.class);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertNotNull(response.getBody().getMatch());
    assertNotNull(response.getBody().getMatch().getScore());
    return response.getBody().getMatch().getScore();
  }
}
