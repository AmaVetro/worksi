package cl.duoc.worksi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cl.duoc.worksi.client.AiMatchClient;
import cl.duoc.worksi.dto.MatchBreakdownResponse;
import cl.duoc.worksi.dto.ai.MatchApiResponse;
import cl.duoc.worksi.entity.CandidateCv;
import cl.duoc.worksi.entity.CandidatePreferredModality;
import cl.duoc.worksi.entity.CandidatePreferredWorkload;
import cl.duoc.worksi.entity.CandidateProfile;
import cl.duoc.worksi.entity.Job;
import cl.duoc.worksi.entity.enums.JobStatus;
import cl.duoc.worksi.entity.enums.Modality;
import cl.duoc.worksi.entity.enums.Workload;
import cl.duoc.worksi.repository.CandidateCvRepository;
import cl.duoc.worksi.repository.CandidatePreferredModalityRepository;
import cl.duoc.worksi.repository.CandidatePreferredWorkloadRepository;
import cl.duoc.worksi.repository.CandidateProfileRepository;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class ProductMatchServiceTest {

  private static final long CANDIDATE_ID = 42L;

  @Mock private CandidateCvRepository candidateCvRepository;
  @Mock private CandidateProfileRepository candidateProfileRepository;
  @Mock private CandidatePreferredModalityRepository candidatePreferredModalityRepository;
  @Mock private CandidatePreferredWorkloadRepository candidatePreferredWorkloadRepository;
  @Mock private CvTextExtractionService cvTextExtractionService;
  @Mock private AiMatchClient aiMatchClient;

  @InjectMocks private ProductMatchService productMatchService;

  private Job job;

  @BeforeEach
  void setUp() {
    job = activeJob();
  }

  @Test
  void computesFinalScoreAsAverageOfFiveDimensions() {
    stubCvWithText("Desarrollador Java Spring Boot REST APIs MySQL Git microservicios.");
    stubPreferences(Modality.REMOTE, Workload.FULL_TIME);
    stubCandidateYears(4);
    stubAiScores(0.80, 0.60);

    ProductMatchService.ProductMatchResult result = productMatchService.compute(CANDIDATE_ID, job);

    assertNotNull(result.score());
    assertEquals(88.0, result.score(), 0.01);
    MatchBreakdownResponse breakdown = result.breakdown();
    assertNotNull(breakdown);
    assertEquals(80.0, breakdown.getDescriptionScore(), 0.01);
    assertEquals(60.0, breakdown.getTitleScore(), 0.01);
    assertEquals(100.0, breakdown.getModalityScore(), 0.01);
    assertEquals(100.0, breakdown.getWorkloadScore(), 0.01);
    assertEquals(100.0, breakdown.getExperienceScore(), 0.01);
    assertEquals(88.0, breakdown.getFinalScore(), 0.01);
  }

  @Test
  void modalityMismatchGivesZeroModalityScore() {
    stubCvWithText("Desarrollador Java Spring Boot REST APIs MySQL Git microservicios.");
    when(candidatePreferredModalityRepository.findByCandidateUserId(CANDIDATE_ID))
        .thenReturn(List.of(new CandidatePreferredModality(CANDIDATE_ID, Modality.ONSITE)));
    when(candidatePreferredWorkloadRepository.findByCandidateUserId(CANDIDATE_ID))
        .thenReturn(List.of(new CandidatePreferredWorkload(CANDIDATE_ID, Workload.FULL_TIME)));
    stubCandidateYears(4);
    stubAiScores(0.80, 0.60);

    ProductMatchService.ProductMatchResult result = productMatchService.compute(CANDIDATE_ID, job);

    assertNotNull(result.score());
    assertEquals(68.0, result.score(), 0.01);
    assertEquals(0.0, result.breakdown().getModalityScore(), 0.01);
  }

  @Test
  void workloadMismatchGivesZeroWorkloadScore() {
    stubCvWithText("Desarrollador Java Spring Boot REST APIs MySQL Git microservicios.");
    when(candidatePreferredModalityRepository.findByCandidateUserId(CANDIDATE_ID))
        .thenReturn(List.of(new CandidatePreferredModality(CANDIDATE_ID, Modality.REMOTE)));
    when(candidatePreferredWorkloadRepository.findByCandidateUserId(CANDIDATE_ID))
        .thenReturn(List.of(new CandidatePreferredWorkload(CANDIDATE_ID, Workload.PART_TIME)));
    stubCandidateYears(4);
    stubAiScores(0.80, 0.60);

    ProductMatchService.ProductMatchResult result = productMatchService.compute(CANDIDATE_ID, job);

    assertNotNull(result.score());
    assertEquals(68.0, result.score(), 0.01);
    assertEquals(0.0, result.breakdown().getWorkloadScore(), 0.01);
  }

  @Test
  void experienceScoreUsesCandidateYears() {
    stubCvWithText("Desarrollador Java Spring Boot REST APIs MySQL Git microservicios.");
    stubPreferences(Modality.REMOTE, Workload.FULL_TIME);
    stubCandidateYears(2);
    stubAiScores(0.50, 0.50);
    job.setYearsExperienceRequired(4);

    ProductMatchService.ProductMatchResult result = productMatchService.compute(CANDIDATE_ID, job);

    assertNotNull(result.score());
    assertEquals(50.0, result.breakdown().getExperienceScore(), 0.01);
    assertEquals(70.0, result.score(), 0.01);
  }

  @Test
  void returnsNullWhenNoCv() {
    when(candidateCvRepository.findTopByCandidateUserIdAndCurrentIsTrueOrderByUploadedAtDesc(
            CANDIDATE_ID))
        .thenReturn(Optional.empty());

    ProductMatchService.ProductMatchResult result = productMatchService.compute(CANDIDATE_ID, job);

    assertNull(result.score());
    assertNull(result.breakdown());
    assertEquals(
        "Sin CV disponible para calcular compatibilidad.", result.explanationShort());
  }

  @Test
  void returnsNullWhenCvHasNoUsableText() {
    CandidateCv cv = cvWithNormalizedText(null);
    ReflectionTestUtils.setField(cv, "id", 99L);
    when(candidateCvRepository.findTopByCandidateUserIdAndCurrentIsTrueOrderByUploadedAtDesc(
            CANDIDATE_ID))
        .thenReturn(Optional.of(cv));
    when(candidateCvRepository.findById(99L))
        .thenReturn(Optional.of(cvWithNormalizedText("   ")));

    ProductMatchService.ProductMatchResult result = productMatchService.compute(CANDIDATE_ID, job);

    assertNull(result.score());
    assertEquals(
        "CV sin texto extraido util. Sube un PDF con texto seleccionable.",
        result.explanationShort());
  }

  @Test
  void returnsNullWhenTextExtractionFails() {
    CandidateCv cv = cvWithNormalizedText(null);
    ReflectionTestUtils.setField(cv, "id", 99L);
    when(candidateCvRepository.findTopByCandidateUserIdAndCurrentIsTrueOrderByUploadedAtDesc(
            CANDIDATE_ID))
        .thenReturn(Optional.of(cv));
    doThrow(new ResponseStatusException(org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY))
        .when(cvTextExtractionService)
        .extractAndPersist(anyLong());

    ProductMatchService.ProductMatchResult result = productMatchService.compute(CANDIDATE_ID, job);

    assertNull(result.score());
    verify(cvTextExtractionService).extractAndPersist(anyLong());
  }

  @Test
  void returnsNullWhenAiUnavailable() {
    stubCvWithText("Desarrollador Java Spring Boot REST APIs MySQL Git microservicios.");
    when(aiMatchClient.match(anyString(), anyString())).thenReturn(Optional.empty());

    ProductMatchService.ProductMatchResult result = productMatchService.compute(CANDIDATE_ID, job);

    assertNull(result.score());
    assertEquals("Servicio de matching no disponible.", result.explanationShort());
  }

  private void stubCvWithText(String normalizedText) {
    when(candidateCvRepository.findTopByCandidateUserIdAndCurrentIsTrueOrderByUploadedAtDesc(
            CANDIDATE_ID))
        .thenReturn(Optional.of(cvWithNormalizedText(normalizedText)));
  }

  private static CandidateCv cvWithNormalizedText(String normalizedText) {
    CandidateCv cv = new CandidateCv();
    cv.setCandidateUserId(CANDIDATE_ID);
    cv.setOriginalFilename("cv.pdf");
    cv.setStoragePath("/data/cv/test/cv.pdf");
    cv.setFileSizeBytes(1024);
    cv.setMimeType("application/pdf");
    cv.setNormalizedText(normalizedText);
    cv.setCurrent(true);
    cv.setUploadedAt(LocalDateTime.now(ZoneOffset.UTC));
    return cv;
  }

  private void stubPreferences(Modality modality, Workload workload) {
    when(candidatePreferredModalityRepository.findByCandidateUserId(CANDIDATE_ID))
        .thenReturn(List.of(new CandidatePreferredModality(CANDIDATE_ID, modality)));
    when(candidatePreferredWorkloadRepository.findByCandidateUserId(CANDIDATE_ID))
        .thenReturn(List.of(new CandidatePreferredWorkload(CANDIDATE_ID, workload)));
  }

  private void stubCandidateYears(int years) {
    CandidateProfile profile = new CandidateProfile();
    profile.setYearsExperience(years);
    when(candidateProfileRepository.findById(CANDIDATE_ID)).thenReturn(Optional.of(profile));
  }

  private void stubAiScores(double descriptionRaw, double titleRaw) {
    MatchApiResponse description = new MatchApiResponse();
    description.setScore(descriptionRaw);
    description.setExplanation("Coincidencia semantica en descripcion.");
    MatchApiResponse title = new MatchApiResponse();
    title.setScore(titleRaw);
    title.setExplanation("Coincidencia semantica en titulo.");
    when(aiMatchClient.match(anyString(), anyString()))
        .thenReturn(Optional.of(description))
        .thenReturn(Optional.of(title));
  }

  private static Job activeJob() {
    Job job = new Job();
    job.setCompanyId(1L);
    job.setCompanyCommercialName("Empresa Demo");
    job.setTitle("Desarrollador Java Backend");
    job.setDescription(
        "Desarrollador Java con Spring Boot, APIs REST, MySQL y Git para backend empresarial.");
    job.setRegionId(7L);
    job.setCommuneId(1L);
    job.setSalaryOffered(900000);
    job.setYearsExperienceRequired(4);
    job.setModality(Modality.REMOTE);
    job.setWorkload(Workload.FULL_TIME);
    job.setStatus(JobStatus.ACTIVE);
    job.setPublishedAt(LocalDateTime.now(ZoneOffset.UTC));
    return job;
  }
}
