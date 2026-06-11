package cl.duoc.worksi.service;

import cl.duoc.worksi.dto.MatchBreakdownResponse;
import cl.duoc.worksi.dto.PageResponse;
import cl.duoc.worksi.dto.candidate.CandidateCvCurrentResponse;
import cl.duoc.worksi.dto.candidate.CandidateProfileSkillResponse;
import cl.duoc.worksi.entity.CandidateCv;
import cl.duoc.worksi.dto.company.CompanyApplicationCandidatePreviewResponse;
import cl.duoc.worksi.dto.company.CompanyApplicationViewedResponse;
import cl.duoc.worksi.dto.company.CompanyCandidateProfileResponse;
import cl.duoc.worksi.dto.company.CompanyJobApplicationItemResponse;
import cl.duoc.worksi.entity.Application;
import cl.duoc.worksi.entity.CandidatePreferredModality;
import cl.duoc.worksi.entity.CandidatePreferredWorkload;
import cl.duoc.worksi.entity.CandidateProfile;
import cl.duoc.worksi.entity.CandidateSkill;
import cl.duoc.worksi.entity.Commune;
import cl.duoc.worksi.entity.Job;
import cl.duoc.worksi.entity.Region;
import cl.duoc.worksi.entity.Sector;
import cl.duoc.worksi.entity.Skill;
import cl.duoc.worksi.entity.User;
import cl.duoc.worksi.entity.enums.ApplicationStatus;
import cl.duoc.worksi.entity.enums.JobStatus;
import cl.duoc.worksi.repository.ApplicationRepository;
import cl.duoc.worksi.repository.CandidatePreferredModalityRepository;
import cl.duoc.worksi.repository.CandidatePreferredWorkloadRepository;
import cl.duoc.worksi.repository.CandidateProfileRepository;
import cl.duoc.worksi.repository.CandidateSkillRepository;
import cl.duoc.worksi.repository.CommuneRepository;
import cl.duoc.worksi.repository.JobRepository;
import cl.duoc.worksi.repository.RegionRepository;
import cl.duoc.worksi.repository.SectorRepository;
import cl.duoc.worksi.repository.SkillRepository;
import cl.duoc.worksi.repository.UserRepository;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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
import org.springframework.transaction.annotation.Transactional;
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
  private final UserRepository userRepository;
  private final RegionRepository regionRepository;
  private final CommuneRepository communeRepository;
  private final CandidateSkillRepository candidateSkillRepository;
  private final CandidatePreferredModalityRepository candidatePreferredModalityRepository;
  private final CandidatePreferredWorkloadRepository candidatePreferredWorkloadRepository;
  private final SkillRepository skillRepository;
  private final CandidateCvService candidateCvService;

  public CompanyApplicationsService(
      JobRepository jobRepository,
      ApplicationRepository applicationRepository,
      CandidateProfileRepository candidateProfileRepository,
      SectorRepository sectorRepository,
      ProductMatchService productMatchService,
      UserRepository userRepository,
      RegionRepository regionRepository,
      CommuneRepository communeRepository,
      CandidateSkillRepository candidateSkillRepository,
      CandidatePreferredModalityRepository candidatePreferredModalityRepository,
      CandidatePreferredWorkloadRepository candidatePreferredWorkloadRepository,
      SkillRepository skillRepository,
      CandidateCvService candidateCvService) {
    this.jobRepository = jobRepository;
    this.applicationRepository = applicationRepository;
    this.candidateProfileRepository = candidateProfileRepository;
    this.sectorRepository = sectorRepository;
    this.productMatchService = productMatchService;
    this.userRepository = userRepository;
    this.regionRepository = regionRepository;
    this.communeRepository = communeRepository;
    this.candidateSkillRepository = candidateSkillRepository;
    this.candidatePreferredModalityRepository = candidatePreferredModalityRepository;
    this.candidatePreferredWorkloadRepository = candidatePreferredWorkloadRepository;
    this.skillRepository = skillRepository;
    this.candidateCvService = candidateCvService;
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

  @Transactional
  public ResponseEntity<CompanyJobApplicationItemResponse> getApplicationDetail(
      long recruiterUserId, long jobId, long applicationId) {
    Job job = requireOwnedJob(recruiterUserId, jobId);
    Application app = requireVisibleApplication(job.getId(), applicationId);
    markViewed(app);
    Map<Long, CandidateProfile> profiles =
        loadProfiles(List.of(app));
    return ResponseEntity.ok(toItem(app, profiles));
  }

  @Transactional
  public ResponseEntity<CompanyApplicationViewedResponse> markApplicationViewed(
      long recruiterUserId, long applicationId) {
    Application app =
        applicationRepository
            .findById(applicationId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Postulacion no encontrada"));
    requireOwnedJob(recruiterUserId, app.getJobId());
    if (!VISIBLE.contains(app.getStatus())) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Postulacion no encontrada");
    }
    markViewed(app);
    Instant viewed =
        app.getViewedAt() != null ? app.getViewedAt().atZone(ZoneOffset.UTC).toInstant() : null;
    return ResponseEntity.ok(
        new CompanyApplicationViewedResponse(app.getId(), app.getStatus().name(), viewed));
  }

  @Transactional
  public ResponseEntity<CompanyCandidateProfileResponse> getCandidateProfileForApplication(
      long recruiterUserId, long jobId, long applicationId) {
    Job job = requireOwnedJob(recruiterUserId, jobId);
    Application app = requireVisibleApplication(job.getId(), applicationId);
    markViewed(app);
    CandidateProfile profile =
        candidateProfileRepository
            .findById(app.getCandidateUserId())
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil no encontrado"));
    return ResponseEntity.ok(toCandidateProfileResponse(profile));
  }

  public ResponseEntity<CandidateCvCurrentResponse> getCandidateCvMetadataForApplication(
      long recruiterUserId, long jobId, long applicationId) {
    requireOwnedJob(recruiterUserId, jobId);
    Application app = requireVisibleApplication(jobId, applicationId);
    CandidateCv cv =
        candidateCvService
            .findCurrentCv(app.getCandidateUserId())
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CV no encontrado"));
    Instant uploaded =
        cv.getUploadedAt() != null ? cv.getUploadedAt().atZone(ZoneOffset.UTC).toInstant() : null;
    return ResponseEntity.ok(
        new CandidateCvCurrentResponse(
            cv.getId(),
            cv.getOriginalFilename(),
            cv.getFileSizeBytes(),
            cv.isCurrent(),
            uploaded));
  }

  public ResponseEntity<byte[]> getCandidateCvFileForApplication(
      long recruiterUserId, long jobId, long applicationId, boolean attachment) {
    requireOwnedJob(recruiterUserId, jobId);
    Application app = requireVisibleApplication(jobId, applicationId);
    CandidateCv cv =
        candidateCvService
            .findCurrentCv(app.getCandidateUserId())
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CV no encontrado"));
    return candidateCvService.serveCvFile(cv, attachment);
  }

  private Application requireVisibleApplication(long jobId, long applicationId) {
    Application app =
        applicationRepository
            .findById(applicationId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Postulacion no encontrada"));
    if (!app.getJobId().equals(jobId) || !VISIBLE.contains(app.getStatus())) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Postulacion no encontrada");
    }
    return app;
  }

  private void markViewed(Application app) {
    app.markViewedIfApplied();
    applicationRepository.save(app);
  }

  private CompanyCandidateProfileResponse toCandidateProfileResponse(CandidateProfile p) {
    User u =
        userRepository
            .findById(p.getUserId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    String regionName =
        regionRepository.findById(p.getRegionId()).map(Region::getName).orElse("");
    String communeName =
        communeRepository
            .findById(p.getCommuneId())
            .filter(cm -> cm.getRegionId().equals(p.getRegionId()))
            .map(Commune::getName)
            .orElse("");
    String sectorName = "";
    if (p.getSectorId() != null) {
      sectorName = sectorRepository.findById(p.getSectorId()).map(Sector::getName).orElse("");
    }
    List<String> modalities = new ArrayList<>();
    for (CandidatePreferredModality m :
        candidatePreferredModalityRepository.findByCandidateUserId(p.getUserId())) {
      modalities.add(m.getModality().name());
    }
    List<String> workloads = new ArrayList<>();
    for (CandidatePreferredWorkload w :
        candidatePreferredWorkloadRepository.findByCandidateUserId(p.getUserId())) {
      workloads.add(w.getWorkload().name());
    }
    List<CandidateSkill> links =
        candidateSkillRepository.findByIdCandidateUserIdOrderByIdSkillIdAsc(p.getUserId());
    List<Long> skillIds = new ArrayList<>();
    for (CandidateSkill cs : links) {
      skillIds.add(cs.getId().getSkillId());
    }
    Map<Long, String> names = new LinkedHashMap<>();
    if (!skillIds.isEmpty()) {
      for (Skill sk : skillRepository.findAllById(skillIds)) {
        names.put(sk.getId(), sk.getName());
      }
    }
    List<CandidateProfileSkillResponse> skillsOut = new ArrayList<>();
    for (Long sid : skillIds) {
      String nm = names.get(sid);
      if (nm != null) {
        skillsOut.add(new CandidateProfileSkillResponse(sid, nm));
      }
    }
    return new CompanyCandidateProfileResponse(
        p.getUserId(),
        p.getFirstName(),
        p.getMiddleName(),
        p.getLastNamePaternal(),
        p.getLastNameMaternal(),
        p.getPhone(),
        u.getEmail(),
        regionName,
        communeName,
        sectorName,
        p.getProfileSummary(),
        p.getSalaryExpectedMin(),
        p.getSalaryExpectedMax(),
        p.getYearsExperience(),
        modalities,
        workloads,
        skillsOut);
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
