package cl.duoc.worksi.service;

import cl.duoc.worksi.dto.PageResponse;
import cl.duoc.worksi.dto.candidate.CandidateApplicationDetailResponse;
import cl.duoc.worksi.dto.candidate.CandidateApplicationListItemResponse;
import cl.duoc.worksi.entity.Application;
import cl.duoc.worksi.entity.Commune;
import cl.duoc.worksi.entity.Job;
import cl.duoc.worksi.entity.enums.ApplicationStatus;
import cl.duoc.worksi.repository.ApplicationRepository;
import cl.duoc.worksi.repository.CommuneRepository;
import cl.duoc.worksi.repository.JobRepository;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CandidateApplicationsQueryService {
  private static final List<ApplicationStatus> VISIBLE =
      List.of(ApplicationStatus.APPLIED, ApplicationStatus.VIEWED);

  private final ApplicationRepository applicationRepository;
  private final JobRepository jobRepository;
  private final CommuneRepository communeRepository;

  public CandidateApplicationsQueryService(
      ApplicationRepository applicationRepository,
      JobRepository jobRepository,
      CommuneRepository communeRepository) {
    this.applicationRepository = applicationRepository;
    this.jobRepository = jobRepository;
    this.communeRepository = communeRepository;
  }

  public ResponseEntity<PageResponse<CandidateApplicationListItemResponse>> listMine(
      long candidateUserId, int page, int size, String sort) {
    int p = Math.max(1, page);
    int sz = Math.min(100, Math.max(1, size));
    Pageable pageable = PageRequest.of(p - 1, sz, parseSort(sort));
    Page<Application> result =
        applicationRepository.findByCandidateUserIdAndStatusIn(
            candidateUserId, VISIBLE, pageable);
    Map<Long, Job> jobsById = loadJobs(result.getContent());
    List<CandidateApplicationListItemResponse> items =
        result.getContent().stream().map(a -> toListItem(a, jobsById)).toList();
    return ResponseEntity.ok(
        new PageResponse<>(
            items, p, result.getSize(), result.getTotalElements(), result.getTotalPages()));
  }

  public ResponseEntity<CandidateApplicationDetailResponse> getMine(
      long candidateUserId, long applicationId) {
    Application app =
        applicationRepository
            .findByIdAndCandidateUserId(applicationId, candidateUserId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Postulacion no encontrada"));
    if (app.getStatus() == ApplicationStatus.CANCELLED) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Postulacion no encontrada");
    }
    Job job =
        jobRepository
            .findById(app.getJobId())
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Oferta no encontrada"));
    return ResponseEntity.ok(toDetail(app, job));
  }

  private CandidateApplicationListItemResponse toListItem(
      Application app, Map<Long, Job> jobsById) {
    Job job = jobsById.get(app.getJobId());
    String title = job != null ? job.getTitle() : "";
    String company = job != null ? job.getCompanyCommercialName() : "";
    int salary = job != null ? job.getSalaryOffered() : 0;
    Double score = app.getMatchScore() != null ? app.getMatchScore().doubleValue() : null;
    Instant applied =
        app.getAppliedAt() != null
            ? app.getAppliedAt().atZone(ZoneOffset.UTC).toInstant()
            : Instant.EPOCH;
    return new CandidateApplicationListItemResponse(
        app.getId(),
        app.getJobId(),
        title,
        company,
        salary,
        app.getStatus().name(),
        applied,
        score);
  }

  private CandidateApplicationDetailResponse toDetail(Application app, Job job) {
    String communeName =
        communeRepository
            .findById(job.getCommuneId())
            .filter(c -> c.getRegionId() == job.getRegionId())
            .map(Commune::getName)
            .orElse("");
    Instant applied =
        app.getAppliedAt() != null
            ? app.getAppliedAt().atZone(ZoneOffset.UTC).toInstant()
            : Instant.EPOCH;
    Instant viewed =
        app.getViewedAt() != null ? app.getViewedAt().atZone(ZoneOffset.UTC).toInstant() : null;
    Double score = app.getMatchScore() != null ? app.getMatchScore().doubleValue() : null;
    String description = job.getDescription() == null ? "" : job.getDescription();
    if (description.length() > 200) {
      description = description.substring(0, 200) + "…";
    }
    return new CandidateApplicationDetailResponse(
        app.getId(),
        app.getJobId(),
        app.getStatus().name(),
        applied,
        viewed,
        job.getTitle(),
        job.getCompanyCommercialName(),
        job.getSalaryOffered(),
        job.getModality().name(),
        job.getYearsExperienceRequired(),
        communeName,
        description,
        score);
  }

  private Map<Long, Job> loadJobs(List<Application> apps) {
    Set<Long> ids = apps.stream().map(Application::getJobId).collect(Collectors.toSet());
    if (ids.isEmpty()) {
      return Map.of();
    }
    return jobRepository.findAllById(ids).stream()
        .collect(Collectors.toMap(Job::getId, Function.identity()));
  }

  private static Sort parseSort(String raw) {
    if (raw == null || raw.isBlank()) {
      return Sort.by(Sort.Direction.DESC, "appliedAt");
    }
    String[] parts = raw.split(",");
    if (parts.length < 2) {
      return Sort.by(Sort.Direction.DESC, "appliedAt");
    }
    String field = parts[0].trim();
    if (field.equals("applied_at")) {
      field = "appliedAt";
    }
    Sort.Direction dir =
        "asc".equalsIgnoreCase(parts[1].trim()) ? Sort.Direction.ASC : Sort.Direction.DESC;
    return Sort.by(dir, field);
  }
}
