package cl.duoc.worksi.service;

import cl.duoc.worksi.client.AiMatchClient;
import cl.duoc.worksi.dto.ai.MatchApiResponse;
import cl.duoc.worksi.entity.CandidateCv;
import cl.duoc.worksi.entity.CandidatePreferredModality;
import cl.duoc.worksi.entity.CandidatePreferredWorkload;
import cl.duoc.worksi.entity.Job;
import cl.duoc.worksi.repository.CandidateCvRepository;
import cl.duoc.worksi.repository.CandidatePreferredModalityRepository;
import cl.duoc.worksi.repository.CandidatePreferredWorkloadRepository;
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
  private final CandidatePreferredModalityRepository candidatePreferredModalityRepository;
  private final CandidatePreferredWorkloadRepository candidatePreferredWorkloadRepository;
  private final CvTextExtractionService cvTextExtractionService;
  private final JobTextService jobTextService;
  private final AiMatchClient aiMatchClient;

  public ProductMatchService(
      CandidateCvRepository candidateCvRepository,
      CandidatePreferredModalityRepository candidatePreferredModalityRepository,
      CandidatePreferredWorkloadRepository candidatePreferredWorkloadRepository,
      CvTextExtractionService cvTextExtractionService,
      JobTextService jobTextService,
      AiMatchClient aiMatchClient) {
    this.candidateCvRepository = candidateCvRepository;
    this.candidatePreferredModalityRepository = candidatePreferredModalityRepository;
    this.candidatePreferredWorkloadRepository = candidatePreferredWorkloadRepository;
    this.cvTextExtractionService = cvTextExtractionService;
    this.jobTextService = jobTextService;
    this.aiMatchClient = aiMatchClient;
  }

  public ProductMatchResult compute(long candidateUserId, Job job) {
    Optional<CandidateCv> cvOpt =
        candidateCvRepository.findTopByCandidateUserIdAndCurrentIsTrueOrderByUploadedAtDesc(
            candidateUserId);
    if (cvOpt.isEmpty()) {
      return new ProductMatchResult(null, NO_CV, NO_CV);
    }
    CandidateCv cv = cvOpt.get();
    String norm = cv.getNormalizedText();
    if (norm == null || norm.isBlank()) {
      try {
        cvTextExtractionService.extractAndPersist(cv.getId());
      } catch (ResponseStatusException ex) {
        if (ex.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
          return new ProductMatchResult(null, NO_TEXT, NO_TEXT);
        }
        return new ProductMatchResult(null, NO_TEXT, NO_TEXT);
      }
      cv =
          candidateCvRepository
              .findById(cv.getId())
              .orElseThrow(() -> new IllegalStateException("CV inconsistente"));
      norm = cv.getNormalizedText();
    }
    if (norm == null || norm.isBlank()) {
      return new ProductMatchResult(null, NO_TEXT, NO_TEXT);
    }
    String jobText = jobTextService.buildJobText(job);
    Optional<MatchApiResponse> ai = aiMatchClient.match(norm, jobText);
    if (ai.isEmpty()) {
      return new ProductMatchResult(null, IA_DOWN, IA_DOWN);
    }
    double base01 = clamp01(ai.get().getScore());
    double base100 = base01 * 100.0;
    int bonus = preferenceBonus(candidateUserId, job);
    double combined = base100 * 0.8 + bonus;
    double finalScore = round2(clamp(combined, 0.0, 100.0));
    String sem = safeExplanation(ai.get().getExplanation());
    String extra = bonus > 0 ? " Coincide modalidad y/o jornada con tus preferencias." : "";
    String full = (sem + extra).trim();
    return new ProductMatchResult(finalScore, truncate(full, 140), full);
  }

  private int preferenceBonus(long candidateUserId, Job job) {
    Set<String> mods = new HashSet<>();
    for (CandidatePreferredModality m :
        candidatePreferredModalityRepository.findByCandidateUserId(candidateUserId)) {
      mods.add(m.getModality().name());
    }
    Set<String> loads = new HashSet<>();
    for (CandidatePreferredWorkload w :
        candidatePreferredWorkloadRepository.findByCandidateUserId(candidateUserId)) {
      loads.add(w.getWorkload().name());
    }
    int b = 0;
    if (mods.contains(job.getModality().name())) {
      b += 10;
    }
    if (loads.contains(job.getWorkload().name())) {
      b += 10;
    }
    return b;
  }

  private static double clamp01(double v) {
    if (v < 0.0) {
      return 0.0;
    }
    if (v > 1.0) {
      return 1.0;
    }
    return v;
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

  public record ProductMatchResult(Double score, String explanationShort, String explanationFull) {}
}
