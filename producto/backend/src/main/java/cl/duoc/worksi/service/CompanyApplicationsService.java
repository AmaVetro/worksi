package cl.duoc.worksi.service;

import cl.duoc.worksi.dto.MatchBreakdownResponse;
import cl.duoc.worksi.dto.PageResponse;
import cl.duoc.worksi.dto.company.CompanyApplicationCandidatePreviewResponse;
import cl.duoc.worksi.dto.company.CompanyJobApplicationItemResponse;
import cl.duoc.worksi.entity.Application;
import cl.duoc.worksi.entity.CandidateProfile;
import cl.duoc.worksi.entity.Job;
import cl.duoc.worksi.entity.Sector;
import cl.duoc.worksi.entity.enums.ApplicationStatus;
import cl.duoc.worksi.entity.enums.JobStatus;
import cl.duoc.worksi.repository.ApplicationRepository;
import cl.duoc.worksi.repository.CandidateProfileRepository;
import cl.duoc.worksi.repository.JobRepository;
import cl.duoc.worksi.repository.SectorRepository;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
public class CompanyApplicationsService {
  private static final List<ApplicationStatus> VISIBLE =
      List.of(ApplicationStatus.APPLIED, ApplicationStatus.VIEWED);

  private final JobRepository jobRepository;
  private final ApplicationRepository applicationRepository;
  private final CandidateProfileRepository candidateProfileRepository;
  private final SectorRepository sectorRepository;
  private final ProductMatchService productMatchService;

  public CompanyApplicationsService(
      JobRepository jobRepository,
      ApplicationRepository applicationRepository,
      CandidateProfileRepository candidateProfileRepository,
      SectorRepository sectorRepository,
      ProductMatchService productMatchService) {
    this.jobRepository = jobRepository;
    this.applicationRepository = applicationRepository;
    this.candidateProfileRepository = candidateProfileRepository;
    this.sectorRepository = sectorRepository;
    this.productMatchService = productMatchService;
  }

  public ResponseEntity<PageResponse<CompanyJobApplicationItemResponse>> listForJob(
      long recruiterUserId, long jobId, int page, int size, String sort) {
    Job job = requireOwnedJob(recruiterUserId, jobId);
    int p = Math.max(1, page);
    int sz = Math.min(100, Math.max(1, size));
    Pageable pageable = PageRequest.of(p - 1, sz, parseSort(sort));
    Page<Application> result =
        applicationRepository.findByJobIdAndStatusIn(job.getId(), VISIBLE, pageable);
    Map<Long, CandidateProfile> profiles = loadProfiles(result.getContent());
    List<CompanyJobApplicationItemResponse> items =
        result.getContent().stream().map(a -> toItem(a, profiles)).toList();
    return ResponseEntity.ok(
        new PageResponse<>(
            items, p, result.getSize(), result.getTotalElements(), result.getTotalPages()));
  }

  public long countVisibleApplications(long jobId) {
    return applicationRepository.countByJobIdAndStatusIn(jobId, VISIBLE);
  }

  private Job requireOwnedJob(long recruiterUserId, long jobId) {
    Optional<Job> opt = jobRepository.findById(jobId);
    if (opt.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Oferta no encontrada");
    }
    Job job = opt.get();
    if (job.getPublishedByUserId() == null || !job.getPublishedByUserId().equals(recruiterUserId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Oferta no encontrada");
    }
    if (job.getStatus() == JobStatus.DELETED) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Oferta no encontrada");
    }
    return job;
  }

  private CompanyJobApplicationItemResponse toItem(
      Application app, Map<Long, CandidateProfile> profiles) {
    CandidateProfile profile = profiles.get(app.getCandidateUserId());
    String sectorName = "";
    if (profile != null && profile.getSectorId() != null) {
      sectorName =
          sectorRepository.findById(profile.getSectorId()).map(Sector::getName).orElse("");
    }
    String firstName = profile != null ? profile.getFirstName() : "";
    String middleName = profile != null ? profile.getMiddleName() : "";
    String lastName = profile != null ? profile.getLastNamePaternal() : "";
    String lastNameMaternal = profile != null ? profile.getLastNameMaternal() : "";
    Instant applied =
        app.getAppliedAt() != null
            ? app.getAppliedAt().atZone(ZoneOffset.UTC).toInstant()
            : Instant.EPOCH;
    Instant viewed =
        app.getViewedAt() != null ? app.getViewedAt().atZone(ZoneOffset.UTC).toInstant() : null;
    Double score = app.getMatchScore() != null ? app.getMatchScore().doubleValue() : null;
    MatchBreakdownResponse breakdown = productMatchService.breakdownFromApplication(app);
    return new CompanyJobApplicationItemResponse(
        app.getId(),
        app.getCandidateUserId(),
        app.getStatus().name(),
        applied,
        viewed,
        score,
        app.getMatchExplanation(),
        breakdown,
        new CompanyApplicationCandidatePreviewResponse(
            firstName, middleName, lastName, lastNameMaternal, sectorName));
  }

  private Map<Long, CandidateProfile> loadProfiles(List<Application> apps) {
    Set<Long> ids = apps.stream().map(Application::getCandidateUserId).collect(Collectors.toSet());
    if (ids.isEmpty()) {
      return Map.of();
    }
    return candidateProfileRepository.findAllById(ids).stream()
        .collect(Collectors.toMap(CandidateProfile::getUserId, Function.identity()));
  }

  private static Sort parseSort(String raw) {
    if (raw == null || raw.isBlank()) {
      return Sort.by(Sort.Direction.DESC, "matchScore");
    }
    String[] parts = raw.split(",");
    if (parts.length < 2) {
      return Sort.by(Sort.Direction.DESC, "matchScore");
    }
    String field = parts[0].trim();
    if (field.equals("match_score")) {
      field = "matchScore";
    } else if (field.equals("applied_at")) {
      field = "appliedAt";
    }
    Sort.Direction dir =
        "asc".equalsIgnoreCase(parts[1].trim()) ? Sort.Direction.ASC : Sort.Direction.DESC;
    return Sort.by(dir, field);
  }
}
