package cl.duoc.worksi.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cl.duoc.worksi.dto.MatchBreakdownResponse;
import cl.duoc.worksi.dto.candidate.CandidateJobDetailResponse;
import cl.duoc.worksi.entity.CandidateCv;
import cl.duoc.worksi.entity.CandidatePreferredModality;
import cl.duoc.worksi.entity.CandidatePreferredWorkload;
import cl.duoc.worksi.entity.CandidateProfile;
import cl.duoc.worksi.entity.Company;
import cl.duoc.worksi.entity.Commune;
import cl.duoc.worksi.entity.Job;
import cl.duoc.worksi.entity.Region;
import cl.duoc.worksi.entity.User;
import cl.duoc.worksi.entity.enums.JobStatus;
import cl.duoc.worksi.entity.enums.Modality;
import cl.duoc.worksi.entity.enums.UserRole;
import cl.duoc.worksi.entity.enums.Workload;
import cl.duoc.worksi.repository.CandidateCvRepository;
import cl.duoc.worksi.repository.CandidatePreferredModalityRepository;
import cl.duoc.worksi.repository.CandidatePreferredWorkloadRepository;
import cl.duoc.worksi.repository.CandidateProfileRepository;
import cl.duoc.worksi.repository.CommuneRepository;
import cl.duoc.worksi.repository.CompanyRepository;
import cl.duoc.worksi.repository.JobRepository;
import cl.duoc.worksi.repository.RegionRepository;
import cl.duoc.worksi.repository.UserRepository;
import cl.duoc.worksi.security.JwtService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Tag("integration")
@EnabledIf("cl.duoc.worksi.integration.IntegrationTestSupport#isEnabled")
class MatchScoreIntegrationTest {

  static final String JAVA_CV_TEXT =
      "Desarrollador Java senior con Spring Boot, APIs REST, microservicios, MySQL, Git, "
          + "JUnit, Maven, cinco años de experiencia en backend empresarial y equipos ágiles.";

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
  @Autowired CompanyRepository companyRepository;
  @Autowired JobRepository jobRepository;
  @Autowired RegionRepository regionRepository;
  @Autowired CommuneRepository communeRepository;
  @Autowired JwtService jwtService;
  @Autowired PasswordEncoder passwordEncoder;

  User candidateUser;
  String candidateToken;
  long alignedJobId;
  long misalignedJobId;

  @BeforeAll
  static void startInfrastructure() throws IOException {
    cvStorageDir = Files.createTempDirectory("worksi-it-cv");
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
  void seedMatchScenario() {
    String unique = UUID.randomUUID().toString().replace("-", "").substring(0, 7);
    Region region =
        regionRepository.findAll().stream()
            .filter(r -> "CL-RM".equals(r.getCode()))
            .findFirst()
            .orElseThrow();
    Commune commune =
        communeRepository.findByRegionIdAndActiveIsTrueOrderByNameAsc(region.getId()).stream()
            .filter(c -> "Santiago".equals(c.getName()))
            .findFirst()
            .orElseThrow();

    User user = new User();
    user.setRole(UserRole.CANDIDATE);
    user.setEmail("it-candidate-" + UUID.randomUUID() + "@worksi.test");
    user.setPasswordHash(passwordEncoder.encode("Aa!1234567890"));
    user.setActive(true);
    user.setPasswordResetRequired(false);
    user.setFailedLoginAttempts(0);
    candidateUser = userRepository.saveAndFlush(user);

    CandidateProfile profile = new CandidateProfile();
    profile.setUserId(candidateUser.getId());
    profile.setFirstName("Ana");
    profile.setLastNamePaternal("Rojas");
    profile.setLastNameMaternal("Perez");
    profile.setPhone("+56912345678");
    profile.setRut("1234567" + unique.charAt(0) + "-5");
    profile.setDocumentNumber("1234567" + unique.charAt(0));
    profile.setRegionId(region.getId());
    profile.setCommuneId(commune.getId());
    profile.setSectorId(1L);
    profile.setYearsExperience(5);
    profile.setConsentGiven(true);
    profile.setConsentGivenAt(LocalDateTime.now(ZoneOffset.UTC));
    candidateProfileRepository.saveAndFlush(profile);

    candidatePreferredModalityRepository.save(
        new CandidatePreferredModality(candidateUser.getId(), Modality.REMOTE));
    candidatePreferredWorkloadRepository.save(
        new CandidatePreferredWorkload(candidateUser.getId(), Workload.FULL_TIME));

    CandidateCv cv = new CandidateCv();
    cv.setCandidateUserId(candidateUser.getId());
    cv.setOriginalFilename("cv.pdf");
    cv.setStoragePath(cvStorageDir.resolve("cv.pdf").toString());
    cv.setFileSizeBytes(2048);
    cv.setMimeType("application/pdf");
    cv.setNormalizedText(JAVA_CV_TEXT);
    cv.setCurrent(true);
    cv.setUploadedAt(LocalDateTime.now(ZoneOffset.UTC));
    candidateCvRepository.saveAndFlush(cv);

    Company company = new Company();
    company.setPhone("+56987654321");
    company.setCommercialName("Empresa IT Test");
    company.setLegalName("Empresa IT Test SpA");
    company.setRut("7612345" + unique.charAt(1) + "-7");
    company.setRegionId(region.getId());
    company.setCommuneId(commune.getId());
    company.setAddress("Av. Providencia 123");
    company.setCorporateEmail("contacto@empresait.test");
    company.setSectorId(1L);
    company.setWorkerCountApprox(25);
    company = companyRepository.saveAndFlush(company);

    alignedJobId =
        saveJob(
            company,
            region,
            commune,
            "Desarrollador Java Backend",
            "Buscamos desarrollador Java con Spring Boot, APIs REST, microservicios, MySQL, Git, "
                + "JUnit y experiencia en backend empresarial.",
            3);
    misalignedJobId =
        saveJob(
            company,
            region,
            commune,
            "Chef ejecutivo de cocina",
            "Buscamos chef con experiencia en cocina francesa, pasteleria fina, menu degustacion, "
                + "gestion de brigada y normas HACCP en restaurante gourmet.",
            3);

    candidateToken = jwtService.createToken(candidateUser);
  }

  @Test
  void jobDetailReturnsScoreWithFiveDimensionsAndConsistentFinal() {
    CandidateJobDetailResponse body = fetchJobDetail(alignedJobId);

    assertNotNull(body.getMatch());
    assertNotNull(body.getMatch().getScore());
    MatchBreakdownResponse breakdown = body.getMatch().getMatchBreakdown();
    assertNotNull(breakdown);
    assertNotNull(breakdown.getDescriptionScore());
    assertNotNull(breakdown.getTitleScore());
    assertNotNull(breakdown.getModalityScore());
    assertNotNull(breakdown.getWorkloadScore());
    assertNotNull(breakdown.getExperienceScore());
    assertNotNull(breakdown.getFinalScore());

    double expectedAverage =
        (breakdown.getDescriptionScore()
                + breakdown.getTitleScore()
                + breakdown.getModalityScore()
                + breakdown.getWorkloadScore()
                + breakdown.getExperienceScore())
            / 5.0;
    assertEquals(expectedAverage, breakdown.getFinalScore(), 0.05);
    assertEquals(breakdown.getFinalScore(), body.getMatch().getScore(), 0.05);
    assertTrue(body.getMatch().getScore() > 0.0);
  }

  @Test
  void alignedJobScoresHigherThanMisalignedJobForSameCandidate() {
    CandidateJobDetailResponse aligned = fetchJobDetail(alignedJobId);
    CandidateJobDetailResponse misaligned = fetchJobDetail(misalignedJobId);

    assertNotNull(aligned.getMatch().getScore());
    assertNotNull(misaligned.getMatch().getScore());
    assertTrue(
        aligned.getMatch().getScore() > misaligned.getMatch().getScore(),
        "La oferta alineada deberia superar a la no relacionada");
  }

  private CandidateJobDetailResponse fetchJobDetail(long jobId) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(candidateToken);
    ResponseEntity<CandidateJobDetailResponse> response =
        restTemplate.exchange(
            "http://localhost:" + port + "/api/v1/candidate/jobs/" + jobId,
            HttpMethod.GET,
            new HttpEntity<>(headers),
            CandidateJobDetailResponse.class);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    return response.getBody();
  }

  private long saveJob(
      Company company,
      Region region,
      Commune commune,
      String title,
      String description,
      int yearsRequired) {
    Job job = new Job();
    job.setCompanyId(company.getId());
    job.setCompanyCommercialName(company.getCommercialName());
    job.setTitle(title);
    job.setDescription(description);
    job.setRegionId(region.getId());
    job.setCommuneId(commune.getId());
    job.setSalaryOffered(850000);
    job.setYearsExperienceRequired(yearsRequired);
    job.setModality(Modality.REMOTE);
    job.setWorkload(Workload.FULL_TIME);
    job.setStatus(JobStatus.ACTIVE);
    job.setPublishedAt(LocalDateTime.now(ZoneOffset.UTC));
    return jobRepository.saveAndFlush(job).getId();
  }
}
