package cl.duoc.worksi.service;

import cl.duoc.worksi.dto.candidate.CandidateApplicationCreatedResponse;
import cl.duoc.worksi.dto.candidate.CandidateApplicationRequest;
import cl.duoc.worksi.dto.MatchBreakdownResponse;
import cl.duoc.worksi.entity.Application;
import cl.duoc.worksi.entity.CandidateJobSwipe;
import cl.duoc.worksi.entity.Job;
import cl.duoc.worksi.entity.enums.ApplicationStatus;
import cl.duoc.worksi.entity.enums.JobStatus;
import cl.duoc.worksi.entity.enums.SwipeAction;
import cl.duoc.worksi.repository.ApplicationRepository;
import cl.duoc.worksi.repository.CandidateJobSwipeRepository;
import cl.duoc.worksi.repository.JobRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CandidateApplicationService {
  private final JobRepository jobRepository;
  private final ApplicationRepository applicationRepository;
  private final CandidateJobSwipeRepository candidateJobSwipeRepository;
  private final ProductMatchService productMatchService;

  public CandidateApplicationService(
      JobRepository jobRepository,
      ApplicationRepository applicationRepository,
      CandidateJobSwipeRepository candidateJobSwipeRepository,
      ProductMatchService productMatchService) {
    this.jobRepository = jobRepository;
    this.applicationRepository = applicationRepository;
    this.candidateJobSwipeRepository = candidateJobSwipeRepository;
    this.productMatchService = productMatchService;
  }

  @Transactional
  public ResponseEntity<CandidateApplicationCreatedResponse> apply(
      long candidateUserId, CandidateApplicationRequest request) {
    long jobId = request.jobId();
    if (applicationRepository.existsByCandidateUserIdAndJobId(candidateUserId, jobId)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya postulaste a esta oferta");
    }
    Optional<Job> opt = jobRepository.findById(jobId);
    if (opt.isEmpty() || opt.get().getStatus() != JobStatus.ACTIVE) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Oferta no encontrada");
    }
    Job job = opt.get();
    ProductMatchService.ProductMatchResult match = productMatchService.compute(candidateUserId, job);
    BigDecimal score = null;
    BigDecimal descriptionScore = null;
    BigDecimal titleScore = null;
    BigDecimal modalityScore = null;
    BigDecimal workloadScore = null;
    BigDecimal experienceScore = null;
    if (match.score() != null) {
      score = BigDecimal.valueOf(match.score());
      MatchBreakdownResponse bd = match.breakdown();
      if (bd != null) {
        descriptionScore = bd.getDescriptionScore() != null ? BigDecimal.valueOf(bd.getDescriptionScore()) : null;
        titleScore = bd.getTitleScore() != null ? BigDecimal.valueOf(bd.getTitleScore()) : null;
        modalityScore = bd.getModalityScore() != null ? BigDecimal.valueOf(bd.getModalityScore()) : null;
        workloadScore = bd.getWorkloadScore() != null ? BigDecimal.valueOf(bd.getWorkloadScore()) : null;
        experienceScore = bd.getExperienceScore() != null ? BigDecimal.valueOf(bd.getExperienceScore()) : null;
      }
    }
    String explanation = match.explanationFull();
    if (explanation != null && explanation.isBlank()) {
      explanation = match.explanationShort();
    }
    Application app =
        Application.createApplied(
            candidateUserId,
            jobId,
            score,
            explanation,
            descriptionScore,
            titleScore,
            modalityScore,
            workloadScore,
            experienceScore);
    Application saved = applicationRepository.save(app);
    if (!candidateJobSwipeRepository.existsByCandidateUserIdAndJobId(candidateUserId, jobId)) {
      candidateJobSwipeRepository.save(
          CandidateJobSwipe.create(candidateUserId, jobId, SwipeAction.APPLY));
    }
    Instant appliedAt =
        saved.getAppliedAt() == null
            ? Instant.now()
            : saved.getAppliedAt().atZone(ZoneOffset.UTC).toInstant();
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            new CandidateApplicationCreatedResponse(
                saved.getId(), saved.getStatus().name(), appliedAt));
  }

  @Transactional
  public ResponseEntity<Void> cancel(long candidateUserId, long applicationId) {
    Application app =
        applicationRepository
            .findByIdAndCandidateUserId(applicationId, candidateUserId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Postulacion no encontrada"));
    if (app.getStatus() == ApplicationStatus.CANCELLED) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Postulacion ya cancelada");
    }
    try {
      app.cancel();
    } catch (IllegalStateException e) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
    }
    applicationRepository.save(app);
    return ResponseEntity.noContent().build();
  }
}
