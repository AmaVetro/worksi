package cl.duoc.worksi.service;

import cl.duoc.worksi.dto.PageResponse;
import cl.duoc.worksi.dto.candidate.CandidateJobDetailMatchResponse;
import cl.duoc.worksi.dto.candidate.CandidateJobDetailResponse;
import cl.duoc.worksi.dto.candidate.CandidateJobFeedItemResponse;
import cl.duoc.worksi.dto.candidate.CandidateJobMatchResponse;
import cl.duoc.worksi.dto.candidate.CandidateJobSkillPreviewResponse;
import cl.duoc.worksi.entity.Commune;
import cl.duoc.worksi.entity.Job;
import cl.duoc.worksi.entity.JobSkill;
import cl.duoc.worksi.entity.Skill;
import cl.duoc.worksi.entity.enums.JobStatus;
import cl.duoc.worksi.repository.CandidateJobSwipeRepository;
import cl.duoc.worksi.repository.CommuneRepository;
import cl.duoc.worksi.repository.JobRepository;
import cl.duoc.worksi.repository.JobSkillRepository;
import cl.duoc.worksi.repository.SkillRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CandidateJobFeedService {
  private final JobRepository jobRepository;
  private final JobSkillRepository jobSkillRepository;
  private final SkillRepository skillRepository;
  private final CandidateJobSwipeRepository candidateJobSwipeRepository;
  private final CommuneRepository communeRepository;
  private final ProductMatchService productMatchService;

  public CandidateJobFeedService(
      JobRepository jobRepository,
      JobSkillRepository jobSkillRepository,
      SkillRepository skillRepository,
      CandidateJobSwipeRepository candidateJobSwipeRepository,
      CommuneRepository communeRepository,
      ProductMatchService productMatchService) {
    this.jobRepository = jobRepository;
    this.jobSkillRepository = jobSkillRepository;
    this.skillRepository = skillRepository;
    this.candidateJobSwipeRepository = candidateJobSwipeRepository;
    this.communeRepository = communeRepository;
    this.productMatchService = productMatchService;
  }

  public ResponseEntity<PageResponse<CandidateJobFeedItemResponse>> feed(
      long candidateUserId, int page, int size) {
    int p = Math.max(1, page);
    int sz = Math.min(100, Math.max(1, size));
    Pageable pageable = PageRequest.of(p - 1, sz, Sort.by(Sort.Direction.DESC, "createdAt"));
    List<Long> swiped = candidateJobSwipeRepository.findJobIdsByCandidateUserId(candidateUserId);
    Page<Job> result;
    if (swiped.isEmpty()) {
      result = jobRepository.findByStatusOrderByCreatedAtDesc(JobStatus.ACTIVE, pageable);
    } else {
      result = jobRepository.findByStatusAndIdNotIn(JobStatus.ACTIVE, swiped, pageable);
    }
    List<CandidateJobFeedItemResponse> items =
        result.getContent().stream().map(j -> toFeedItem(j, candidateUserId)).toList();
    PageResponse<CandidateJobFeedItemResponse> body =
        new PageResponse<>(
            items, p, result.getSize(), result.getTotalElements(), result.getTotalPages());
    return ResponseEntity.ok(body);
  }

  public ResponseEntity<?> detail(long candidateUserId, long jobId) {
    Optional<Job> opt = jobRepository.findById(jobId);
    if (opt.isEmpty() || opt.get().getStatus() != JobStatus.ACTIVE) {
      return err(HttpStatus.NOT_FOUND, "NOT_FOUND", "Oferta no encontrada");
    }
    Job job = opt.get();
    return ResponseEntity.ok(toDetailItem(job, candidateUserId));
  }

  private CandidateJobFeedItemResponse toFeedItem(Job job, long candidateUserId) {
    List<CandidateJobSkillPreviewResponse> skills = skillPreviews(job.getId(), 4);
    String communeName = communeName(job.getCommuneId(), job.getRegionId());
    ProductMatchService.ProductMatchResult m = productMatchService.compute(candidateUserId, job);
    return new CandidateJobFeedItemResponse(
        job.getId(),
        job.getTitle(),
        job.getCompanyCommercialName(),
        job.getSalaryOffered(),
        communeName,
        job.getModality().name(),
        job.getYearsExperienceRequired(),
        preview(job.getDescription(), 200),
        skills,
        externalJobImageUrl(job),
        hasProtectedJobImage(job),
        new CandidateJobMatchResponse(m.score(), m.explanationShort()));
  }

  private CandidateJobDetailResponse toDetailItem(Job job, long candidateUserId) {
    List<JobSkill> links = jobSkillRepository.findAllByJobIdOrderBySkillName(job.getId());
    List<CandidateJobSkillPreviewResponse> skills = new ArrayList<>();
    for (JobSkill js : links) {
      if (skills.size() >= 8) {
        break;
      }
      skillRepository
          .findById(js.getId().getSkillId())
          .filter(Skill::isActive)
          .ifPresent(sk -> skills.add(new CandidateJobSkillPreviewResponse(sk.getId(), sk.getName())));
    }
    String communeName = communeName(job.getCommuneId(), job.getRegionId());
    ProductMatchService.ProductMatchResult m = productMatchService.compute(candidateUserId, job);
    return new CandidateJobDetailResponse(
        job.getId(),
        job.getTitle(),
        job.getCompanyCommercialName(),
        job.getSalaryOffered(),
        communeName,
        job.getModality().name(),
        job.getYearsExperienceRequired(),
        job.getDescription(),
        job.getWorkload().name(),
        skills,
        externalJobImageUrl(job),
        hasProtectedJobImage(job),
        new CandidateJobDetailMatchResponse(m.score(), m.explanationFull()));
  }

  private List<CandidateJobSkillPreviewResponse> skillPreviews(long jobId, int max) {
    List<JobSkill> links = jobSkillRepository.findAllByJobIdOrderBySkillName(jobId);
    List<CandidateJobSkillPreviewResponse> out = new ArrayList<>();
    for (JobSkill js : links) {
      if (out.size() >= max) {
        break;
      }
      skillRepository
          .findById(js.getId().getSkillId())
          .filter(Skill::isActive)
          .ifPresent(sk -> out.add(new CandidateJobSkillPreviewResponse(sk.getId(), sk.getName())));
    }
    return out;
  }

  private String communeName(long communeId, long regionId) {
    Optional<Commune> c = communeRepository.findById(communeId);
    if (c.isEmpty() || c.get().getRegionId() != regionId) {
      return "";
    }
    return c.get().getName();
  }

  private static String preview(String text, int maxChars) {
    if (text == null) {
      return "";
    }
    String t = text.trim().replaceAll("\\s+", " ");
    if (t.length() <= maxChars) {
      return t;
    }
    return t.substring(0, maxChars) + "…";
  }

  private static String externalJobImageUrl(Job job) {
    String raw = job.getImageUrl();
    if (raw == null || raw.isBlank()) {
      return null;
    }
    String t = raw.trim();
    String lower = t.toLowerCase(Locale.ROOT);
    if (lower.startsWith("http://") || lower.startsWith("https://")) {
      return t;
    }
    return null;
  }

  private static boolean hasProtectedJobImage(Job job) {
    String raw = job.getImageUrl();
    if (raw == null || raw.isBlank()) {
      return false;
    }
    String t = raw.trim();
    String lower = t.toLowerCase(Locale.ROOT);
    return !lower.startsWith("http://") && !lower.startsWith("https://");
  }

  private static ResponseEntity<Map<String, Object>> err(
      HttpStatus status, String code, String message) {
    return ResponseEntity.status(status)
        .body(
            Map.of(
                "error",
                Map.of(
                    "code",
                    code,
                    "message",
                    message,
                    "details",
                    List.of(),
                    "trace_id",
                    "")));
  }
}
