package cl.duoc.worksi.service;

import cl.duoc.worksi.client.AiMatchClient;
import cl.duoc.worksi.dto.MatchBreakdownResponse;
import cl.duoc.worksi.dto.ai.MatchApiResponse;
import cl.duoc.worksi.entity.Application;
import cl.duoc.worksi.entity.CandidateCv;
import cl.duoc.worksi.entity.CandidatePreferredModality;
import cl.duoc.worksi.entity.CandidatePreferredWorkload;
import cl.duoc.worksi.entity.CandidateProfile;
import cl.duoc.worksi.entity.Job;
import cl.duoc.worksi.repository.CandidateCvRepository;
import cl.duoc.worksi.repository.CandidatePreferredModalityRepository;
import cl.duoc.worksi.repository.CandidatePreferredWorkloadRepository;
import cl.duoc.worksi.repository.CandidateProfileRepository;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProductMatchService {
  private static final String NO_CV = "Sin CV disponible para calcular compatibilidad.";
  private static final String NO_TEXT = "CV sin texto extraido util. Sube un PDF con texto seleccionable.";
  private static final String IA_DOWN = "Servicio de matching no disponible.";

  private final CandidateCvRepository candidateCvRepository;
  private final CandidateProfileRepository candidateProfileRepository;
  private final CandidatePreferredModalityRepository candidatePreferredModalityRepository;
  private final CandidatePreferredWorkloadRepository candidatePreferredWorkloadRepository;
  private final CvTextExtractionService cvTextExtractionService;
  private final AiMatchClient aiMatchClient;

  public ProductMatchService(
      CandidateCvRepository candidateCvRepository,
      CandidateProfileRepository candidateProfileRepository,
      CandidatePreferredModalityRepository candidatePreferredModalityRepository,
      CandidatePreferredWorkloadRepository candidatePreferredWorkloadRepository,
      CvTextExtractionService cvTextExtractionService,
      AiMatchClient aiMatchClient) {
    this.candidateCvRepository = candidateCvRepository;
    this.candidateProfileRepository = candidateProfileRepository;
    this.candidatePreferredModalityRepository = candidatePreferredModalityRepository;
    this.candidatePreferredWorkloadRepository = candidatePreferredWorkloadRepository;
    this.cvTextExtractionService = cvTextExtractionService;
    this.aiMatchClient = aiMatchClient;
  }

  public ProductMatchResult compute(long candidateUserId, Job job) {
    Optional<CandidateCv> cvOpt =
        candidateCvRepository.findTopByCandidateUserIdAndCurrentIsTrueOrderByUploadedAtDesc(
            candidateUserId);
    if (cvOpt.isEmpty()) {
      return new ProductMatchResult(null, NO_CV, NO_CV, null);
    }
    CandidateCv cv = cvOpt.get();
    String norm = cv.getNormalizedText();
    if (norm == null || norm.isBlank()) {
      try {
        cvTextExtractionService.extractAndPersist(cv.getId());
      } catch (ResponseStatusException ex) {
        return new ProductMatchResult(null, NO_TEXT, NO_TEXT, null);
      }
      cv =
          candidateCvRepository
              .findById(cv.getId())
              .orElseThrow(() -> new IllegalStateException("CV inconsistente"));
      norm = cv.getNormalizedText();
    }
    if (norm == null || norm.isBlank()) {
      return new ProductMatchResult(null, NO_TEXT, NO_TEXT, null);
    }

    String descriptionText = ensureMinAiText(job.getDescription());
    String titleText = ensureMinAiText(job.getTitle());
    Optional<MatchApiResponse> descAi = aiMatchClient.match(norm, descriptionText);
    Optional<MatchApiResponse> titleAi = aiMatchClient.match(norm, titleText);
    if (descAi.isEmpty() || titleAi.isEmpty()) {
      return new ProductMatchResult(null, IA_DOWN, IA_DOWN, null);
    }

    double descriptionScore = toDimensionScore(descAi.get().getScore());
    double titleScore = toDimensionScore(titleAi.get().getScore());
    double modalityScore = preferenceDimensionScore(candidateUserId, job.getModality().name(), true);
    double workloadScore = preferenceDimensionScore(candidateUserId, job.getWorkload().name(), false);
    int candidateYears = resolveCandidateYears(candidateUserId);
    double experienceScore =
        ExperienceScoreUtil.compute(candidateYears, job.getYearsExperienceRequired());

    double finalScore =
        round2(
            clamp(
                (descriptionScore
                        + titleScore
                        + modalityScore
                        + workloadScore
                        + experienceScore)
                    / 5.0,
                0.0,
                100.0));

    MatchBreakdownResponse breakdown =
        new MatchBreakdownResponse(
            finalScore,
            descriptionScore,
            titleScore,
            modalityScore,
            workloadScore,
            experienceScore);

    String sem = safeExplanation(descAi.get().getExplanation());
    String extra = buildDimensionExplanation(modalityScore, workloadScore, experienceScore);
    String full = (sem + extra).trim();
    return new ProductMatchResult(finalScore, truncate(full, 140), full, breakdown);
  }

  public MatchBreakdownResponse breakdownFromApplication(Application application) {
    if (application.getMatchScore() == null) {
      return null;
    }
    if (application.getDescriptionScore() != null
        && application.getTitleScore() != null
        && application.getModalityScore() != null
        && application.getWorkloadScore() != null
        && application.getExperienceScore() != null) {
      return new MatchBreakdownResponse(
          toDouble(application.getMatchScore()),
          toDouble(application.getDescriptionScore()),
          toDouble(application.getTitleScore()),
          toDouble(application.getModalityScore()),
          toDouble(application.getWorkloadScore()),
          toDouble(application.getExperienceScore()));
    }
    return null;
  }

  public MatchBreakdownResponse breakdownFromResult(ProductMatchResult result) {
    return result == null ? null : result.breakdown();
  }

  private int resolveCandidateYears(long candidateUserId) {
    return candidateProfileRepository
        .findById(candidateUserId)
        .map(CandidateProfile::getYearsExperience)
        .orElse(0);
  }

  private double preferenceDimensionScore(long candidateUserId, String jobValue, boolean modality) {
    Set<String> prefs = new HashSet<>();
    if (modality) {
      for (CandidatePreferredModality m :
          candidatePreferredModalityRepository.findByCandidateUserId(candidateUserId)) {
        prefs.add(m.getModality().name());
      }
    } else {
      for (CandidatePreferredWorkload w :
          candidatePreferredWorkloadRepository.findByCandidateUserId(candidateUserId)) {
        prefs.add(w.getWorkload().name());
      }
    }
    return prefs.contains(jobValue) ? 100.0 : 0.0;
  }

  private static double toDimensionScore(double raw01) {
    return round2(clamp(raw01 * 100.0, 0.0, 100.0));
  }

  private static Double toDouble(BigDecimal value) {
    return value == null ? null : value.doubleValue();
  }

  private static String ensureMinAiText(String primary) {
    String t = primary == null ? "" : primary.trim();
    if (t.length() >= 8) {
      return t;
    }
    return (t + " oferta laboral profesional").trim();
  }

  private static String buildDimensionExplanation(
      double modalityScore, double workloadScore, double experienceScore) {
    StringBuilder sb = new StringBuilder();
    if (modalityScore >= 100.0) {
      sb.append(" Modalidad acorde a tus preferencias.");
    }
    if (workloadScore >= 100.0) {
      sb.append(" Jornada acorde a tus preferencias.");
    }
    if (experienceScore >= 100.0) {
      sb.append(" Experiencia acorde a lo requerido.");
    } else if (experienceScore >= 75.0) {
      sb.append(" Experiencia cercana a lo requerido.");
    } else if (experienceScore > 0.0) {
      sb.append(" Experiencia parcial respecto a lo requerido.");
    }
    return sb.toString();
  }

  private static double clamp(double v, double lo, double hi) {
    return Math.max(lo, Math.min(hi, v));
  }

  private static double round2(double v) {
    return Math.round(v * 100.0) / 100.0;
  }

  private static String truncate(String s, int maxChars) {
    if (s.length() <= maxChars) {
      return s;
    }
    return s.substring(0, Math.max(0, maxChars - 1)) + "…";
  }

  private static String safeExplanation(String raw) {
    if (raw == null || raw.isBlank()) {
      return "Coincidencia semántica entre tu CV y el texto de la oferta.";
    }
    return raw.trim();
  }

  public record ProductMatchResult(
      Double score,
      String explanationShort,
      String explanationFull,
      MatchBreakdownResponse breakdown) {}
}
