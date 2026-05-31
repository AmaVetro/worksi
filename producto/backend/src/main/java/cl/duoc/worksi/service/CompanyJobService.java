package cl.duoc.worksi.service;

import cl.duoc.worksi.dto.PageResponse;
import cl.duoc.worksi.dto.company.CompanyJobCreateRequest;
import cl.duoc.worksi.dto.company.CompanyJobCreatedResponse;
import cl.duoc.worksi.dto.company.CompanyJobDetailResponse;
import cl.duoc.worksi.dto.company.CompanyJobListItemResponse;
import cl.duoc.worksi.dto.company.CompanyJobSkillItemResponse;
import cl.duoc.worksi.dto.company.CompanyProfileImagePatchRequest;
import cl.duoc.worksi.dto.company.CompanyProfileImageResponse;
import cl.duoc.worksi.dto.company.RecruiterCompanyProfileResponse;
import cl.duoc.worksi.entity.Company;
import cl.duoc.worksi.entity.Job;
import cl.duoc.worksi.entity.JobSkill;
import cl.duoc.worksi.entity.Skill;
import cl.duoc.worksi.entity.enums.JobStatus;
import cl.duoc.worksi.entity.enums.Modality;
import cl.duoc.worksi.entity.enums.Workload;
import cl.duoc.worksi.repository.CommuneRepository;
import cl.duoc.worksi.repository.CompanyRepository;
import cl.duoc.worksi.repository.JobRepository;
import cl.duoc.worksi.repository.JobSkillRepository;
import cl.duoc.worksi.repository.RecruiterProfileRepository;
import cl.duoc.worksi.repository.RegionRepository;
import cl.duoc.worksi.repository.SkillRepository;
import cl.duoc.worksi.util.JobSemanticText;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CompanyJobService {
  private static final Set<String> ALLOWED_JOB_IMAGE_TYPES = Set.of("image/png", "image/jpeg");

  private final RecruiterProfileRepository recruiterProfileRepository;
  private final CompanyRepository companyRepository;
  private final JobRepository jobRepository;
  private final JobSkillRepository jobSkillRepository;
  private final RegionRepository regionRepository;
  private final CommuneRepository communeRepository;
  private final SkillRepository skillRepository;
  private final Path companyImageBaseDir;
  private final Path jobImageBaseDir;
  private final ObjectMapper objectMapper;

  public CompanyJobService(
      RecruiterProfileRepository recruiterProfileRepository,
      CompanyRepository companyRepository,
      JobRepository jobRepository,
      JobSkillRepository jobSkillRepository,
      RegionRepository regionRepository,
      CommuneRepository communeRepository,
      SkillRepository skillRepository,
      ObjectMapper objectMapper,
      @Value("${worksi.storage.company-images}") String companyImagesDir,
      @Value("${worksi.storage.job-images}") String jobImagesDir) {
    this.recruiterProfileRepository = recruiterProfileRepository;
    this.companyRepository = companyRepository;
    this.jobRepository = jobRepository;
    this.jobSkillRepository = jobSkillRepository;
    this.regionRepository = regionRepository;
    this.communeRepository = communeRepository;
    this.skillRepository = skillRepository;
    this.objectMapper = objectMapper;
    this.companyImageBaseDir = Path.of(companyImagesDir);
    this.jobImageBaseDir = Path.of(jobImagesDir);
  }

  public ResponseEntity<?> getRecruiterCompanyProfile(long recruiterUserId) {
    var profile =
        recruiterProfileRepository
            .findById(recruiterUserId)
            .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(HttpStatus.FORBIDDEN));
    Company company =
        companyRepository
            .findById(profile.getCompanyId())
            .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(HttpStatus.NOT_FOUND));
    String raw = company.getImageUrl();
    String trimmed = raw == null ? "" : raw.trim();
    String external = null;
    boolean hasProtected = false;
    if (!trimmed.isEmpty()) {
      String lower = trimmed.toLowerCase(Locale.ROOT);
      if (lower.startsWith("http://") || lower.startsWith("https://")) {
        external = trimmed;
      } else {
        hasProtected = true;
      }
    }
    return ResponseEntity.ok(
        new RecruiterCompanyProfileResponse(
            company.getId(),
            company.getCommercialName(),
            company.getCorporateEmail(),
            external,
            hasProtected));
  }

  public ResponseEntity<?> getRecruiterCompanyProfileImage(long recruiterUserId) {
    var profile =
        recruiterProfileRepository
            .findById(recruiterUserId)
            .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(HttpStatus.FORBIDDEN));
    Company company =
        companyRepository
            .findById(profile.getCompanyId())
            .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(HttpStatus.NOT_FOUND));
    String raw = company.getImageUrl();
    if (raw == null || raw.isBlank()) {
      return err(HttpStatus.NOT_FOUND, "NOT_FOUND", "Imagen no disponible");
    }
    String trimmed = raw.trim();
    String lower = trimmed.toLowerCase(Locale.ROOT);
    if (lower.startsWith("http://") || lower.startsWith("https://")) {
      return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Imagen externa no descargable por este endpoint");
    }
    Path base = companyImageBaseDir.toAbsolutePath().normalize();
    Path file = Path.of(trimmed).toAbsolutePath().normalize();
    if (!file.startsWith(base)) {
      return err(HttpStatus.FORBIDDEN, "FORBIDDEN", "Ruta de imagen no permitida");
    }
    if (!Files.isRegularFile(file)) {
      return err(HttpStatus.NOT_FOUND, "NOT_FOUND", "Archivo de imagen no encontrado");
    }
    byte[] bytes;
    try {
      bytes = Files.readAllBytes(file);
    } catch (IOException e) {
      return err(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "No se pudo leer la imagen");
    }
    String name = file.getFileName().toString().toLowerCase(Locale.ROOT);
    MediaType mt =
        name.endsWith(".png")
            ? MediaType.IMAGE_PNG
            : (name.endsWith(".jpg") || name.endsWith(".jpeg"))
                ? MediaType.IMAGE_JPEG
                : MediaType.APPLICATION_OCTET_STREAM;
    return ResponseEntity.ok().contentType(mt).body(bytes);
  }

  @Transactional
  public ResponseEntity<?> patchCompanyImage(long recruiterUserId, CompanyProfileImagePatchRequest body) {
    var profile =
        recruiterProfileRepository
            .findById(recruiterUserId)
            .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(HttpStatus.FORBIDDEN));
    Company company =
        companyRepository
            .findById(profile.getCompanyId())
            .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(HttpStatus.NOT_FOUND));
    String url = body.getImageUrl();
    if (url != null && url.isBlank()) {
      url = null;
    }
    company.setImageUrl(url);
    companyRepository.save(company);
    return ResponseEntity.ok(new CompanyProfileImageResponse(company.getId(), company.getImageUrl()));
  }

  @Transactional
  public ResponseEntity<?> createJob(long recruiterUserId, String dataJson, MultipartFile image) {
    if (dataJson == null || dataJson.isBlank()) {
      return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Parte data obligatoria");
    }
    CompanyJobCreateRequest req;
    try {
      req = objectMapper.readValue(dataJson, CompanyJobCreateRequest.class);
    } catch (IOException e) {
      return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "JSON data invalido");
    }
    if (image != null && !image.isEmpty()) {
      String ct = image.getContentType();
      if (ct == null || !ALLOWED_JOB_IMAGE_TYPES.contains(ct.toLowerCase(Locale.ROOT))) {
        return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Imagen debe ser PNG o JPEG");
      }
    }
    var profile =
        recruiterProfileRepository
            .findById(recruiterUserId)
            .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(HttpStatus.FORBIDDEN));
    ResponseEntity<?> v = validateJobPayload(req);
    if (v != null) {
      return v;
    }
    Modality modality;
    Workload workload;
    try {
      modality = Modality.valueOf(req.getModality().trim());
      workload = Workload.valueOf(req.getWorkload().trim());
    } catch (IllegalArgumentException e) {
      return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "modality o workload invalido");
    }
    LocalDateTime publishedAt = LocalDateTime.now(ZoneOffset.UTC);
    Job job = new Job();
    job.setCompanyId(profile.getCompanyId());
    job.setPublishedByUserId(recruiterUserId);
    job.setCompanyCommercialName(req.getCompanyCommercialName().trim());
    job.setTitle(req.getTitle().trim());
    job.setDescription(req.getDescription().trim());
    job.setRegionId(req.getRegionId());
    job.setCommuneId(req.getCommuneId());
    job.setSalaryOffered(req.getSalaryOffered());
    job.setYearsExperienceRequired(req.getYearsExperienceRequired());
    job.setModality(modality);
    job.setWorkload(workload);
    job.setImageUrl(trimNull(req.getImageUrl()));
    job.setStatus(JobStatus.ACTIVE);
    job.setPublishedAt(publishedAt);
    String semantic =
        JobSemanticText.build(job.getTitle(), job.getDescription(), modality, workload);
    if (semantic.isBlank()) {
      return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Texto semantico de oferta vacio");
    }
    job = jobRepository.saveAndFlush(job);
    long jobId = job.getId();
    for (Long sid : new LinkedHashSet<>(req.getSkillsIds())) {
      jobSkillRepository.save(new JobSkill(jobId, sid));
    }
    if (image != null && !image.isEmpty()) {
      try {
        Files.createDirectories(jobImageBaseDir);
        String ext =
            image.getContentType().toLowerCase(Locale.ROOT).contains("png") ? ".png" : ".jpg";
        String filename = jobId + "_" + UUID.randomUUID() + ext;
        Path target = jobImageBaseDir.resolve(filename);
        try (InputStream in = image.getInputStream()) {
          Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }
        job.setImageUrl(target.toAbsolutePath().normalize().toString());
        jobRepository.save(job);
      } catch (IOException e) {
        return err(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "No se pudo guardar la imagen");
      }
    }
    Instant publishedInstant = publishedAt.atZone(ZoneOffset.UTC).toInstant();
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new CompanyJobCreatedResponse(jobId, JobStatus.ACTIVE.name(), publishedInstant));
  }

  @Transactional
  public ResponseEntity<?> updateJob(
      long recruiterUserId, long jobId, String dataJson, MultipartFile image) {
    if (dataJson == null || dataJson.isBlank()) {
      return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Parte data obligatoria");
    }
    CompanyJobCreateRequest req;
    try {
      req = objectMapper.readValue(dataJson, CompanyJobCreateRequest.class);
    } catch (IOException e) {
      return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "JSON data invalido");
    }
    if (image != null && !image.isEmpty()) {
      String ct = image.getContentType();
      if (ct == null || !ALLOWED_JOB_IMAGE_TYPES.contains(ct.toLowerCase(Locale.ROOT))) {
        return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Imagen debe ser PNG o JPEG");
      }
    }
    Optional<Job> opt = jobRepository.findById(jobId);
    if (opt.isEmpty()) {
      return err(HttpStatus.NOT_FOUND, "NOT_FOUND", "Oferta no encontrada");
    }
    Job job = opt.get();
    if (job.getPublishedByUserId() == null || !job.getPublishedByUserId().equals(recruiterUserId)) {
      return err(HttpStatus.NOT_FOUND, "NOT_FOUND", "Oferta no encontrada");
    }
    ResponseEntity<?> v = validateJobPayload(req);
    if (v != null) {
      return v;
    }
    Modality modality;
    Workload workload;
    try {
      modality = Modality.valueOf(req.getModality().trim());
      workload = Workload.valueOf(req.getWorkload().trim());
    } catch (IllegalArgumentException e) {
      return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "modality o workload invalido");
    }
    job.setCompanyCommercialName(req.getCompanyCommercialName().trim());
    job.setTitle(req.getTitle().trim());
    job.setDescription(req.getDescription().trim());
    job.setRegionId(req.getRegionId());
    job.setCommuneId(req.getCommuneId());
    job.setSalaryOffered(req.getSalaryOffered());
    job.setYearsExperienceRequired(req.getYearsExperienceRequired());
    job.setModality(modality);
    job.setWorkload(workload);
    if (image == null || image.isEmpty()) {
      String urlFromJson = trimNull(req.getImageUrl());
      if (urlFromJson != null) {
        job.setImageUrl(urlFromJson);
      }
    }
    jobRepository.saveAndFlush(job);
    jobSkillRepository.deleteAllByJobId(jobId);
    for (Long sid : new LinkedHashSet<>(req.getSkillsIds())) {
      jobSkillRepository.save(new JobSkill(jobId, sid));
    }
    if (image != null && !image.isEmpty()) {
      try {
        Files.createDirectories(jobImageBaseDir);
        String ext =
            image.getContentType().toLowerCase(Locale.ROOT).contains("png") ? ".png" : ".jpg";
        String filename = jobId + "_" + UUID.randomUUID() + ext;
        Path target = jobImageBaseDir.resolve(filename);
        try (InputStream in = image.getInputStream()) {
          Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }
        job.setImageUrl(target.toAbsolutePath().normalize().toString());
        jobRepository.save(job);
      } catch (IOException e) {
        return err(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "No se pudo guardar la imagen");
      }
    }
    return ResponseEntity.ok(toDetail(jobRepository.findById(jobId).orElse(job)));
  }

  public ResponseEntity<?> listMyJobs(long recruiterUserId, int page, int size, String sort) {
    if (!recruiterProfileRepository.existsById(recruiterUserId)) {
      return err(HttpStatus.FORBIDDEN, "FORBIDDEN", "Sesion no autorizada como reclutador");
    }
    int p = Math.max(1, page);
    int sz = Math.min(100, Math.max(1, size));
    Pageable pageable = PageRequest.of(p - 1, sz, parseSort(sort));
    Page<Job> result =
        jobRepository.findByStatusAndPublishedByUserId(JobStatus.ACTIVE, recruiterUserId, pageable);
    List<CompanyJobListItemResponse> items =
        result.getContent().stream().map(this::toListItem).toList();
    PageResponse<CompanyJobListItemResponse> body =
        new PageResponse<>(
            items, p, result.getSize(), result.getTotalElements(), result.getTotalPages());
    return ResponseEntity.ok(body);
  }

  public ResponseEntity<?> getMyJob(long recruiterUserId, long jobId) {
    Optional<Job> opt = jobRepository.findById(jobId);
    if (opt.isEmpty()) {
      return err(HttpStatus.NOT_FOUND, "NOT_FOUND", "Oferta no encontrada");
    }
    Job job = opt.get();
    if (job.getPublishedByUserId() == null || !job.getPublishedByUserId().equals(recruiterUserId)) {
      return err(HttpStatus.NOT_FOUND, "NOT_FOUND", "Oferta no encontrada");
    }
    return ResponseEntity.ok(toDetail(job));
  }

  public ResponseEntity<?> getMyJobImage(long recruiterUserId, long jobId) {
    Optional<Job> opt = jobRepository.findById(jobId);
    if (opt.isEmpty()) {
      return err(HttpStatus.NOT_FOUND, "NOT_FOUND", "Oferta no encontrada");
    }
    Job job = opt.get();
    if (job.getPublishedByUserId() == null || !job.getPublishedByUserId().equals(recruiterUserId)) {
      return err(HttpStatus.NOT_FOUND, "NOT_FOUND", "Oferta no encontrada");
    }
    return serveStoredJobImageBytes(job);
  }

  public ResponseEntity<?> getActiveJobImageForCandidate(long jobId) {
    Optional<Job> opt = jobRepository.findById(jobId);
    if (opt.isEmpty() || opt.get().getStatus() != JobStatus.ACTIVE) {
      return err(HttpStatus.NOT_FOUND, "NOT_FOUND", "Oferta no encontrada");
    }
    return serveStoredJobImageBytes(opt.get());
  }

  private ResponseEntity<?> serveStoredJobImageBytes(Job job) {
    String raw = job.getImageUrl();
    if (raw == null || raw.isBlank()) {
      return err(HttpStatus.NOT_FOUND, "NOT_FOUND", "Imagen no disponible");
    }
    String trimmed = raw.trim();
    String lower = trimmed.toLowerCase(Locale.ROOT);
    if (lower.startsWith("http://") || lower.startsWith("https://")) {
      return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Imagen externa no descargable por este endpoint");
    }
    Path base = jobImageBaseDir.toAbsolutePath().normalize();
    Path file = Path.of(trimmed).toAbsolutePath().normalize();
    if (!file.startsWith(base)) {
      return err(HttpStatus.FORBIDDEN, "FORBIDDEN", "Ruta de imagen no permitida");
    }
    if (!Files.isRegularFile(file)) {
      return err(HttpStatus.NOT_FOUND, "NOT_FOUND", "Archivo de imagen no encontrado");
    }
    byte[] bytes;
    try {
      bytes = Files.readAllBytes(file);
    } catch (IOException e) {
      return err(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "No se pudo leer la imagen");
    }
    String name = file.getFileName().toString().toLowerCase(Locale.ROOT);
    MediaType mt =
        name.endsWith(".png")
            ? MediaType.IMAGE_PNG
            : (name.endsWith(".jpg") || name.endsWith(".jpeg"))
                ? MediaType.IMAGE_JPEG
                : MediaType.APPLICATION_OCTET_STREAM;
    return ResponseEntity.ok().contentType(mt).body(bytes);
  }

  private CompanyJobListItemResponse toListItem(Job job) {
    Instant published =
        job.getPublishedAt() != null
            ? job.getPublishedAt().atZone(ZoneOffset.UTC).toInstant()
            : Instant.EPOCH;
    Instant created =
        job.getCreatedAt() != null
            ? job.getCreatedAt().atZone(ZoneOffset.UTC).toInstant()
            : published;
    return new CompanyJobListItemResponse(
        job.getId(),
        job.getTitle(),
        job.getCompanyCommercialName(),
        job.getSalaryOffered(),
        job.getModality().name(),
        job.getStatus().name(),
        published,
        created);
  }

  private CompanyJobDetailResponse toDetail(Job job) {
    List<Long> skillIdsOrdered =
        jobSkillRepository.findAllByJobIdOrderBySkillName(job.getId()).stream()
            .map(js -> js.getId().getSkillId())
            .toList();
    List<Skill> skRows =
        skillIdsOrdered.isEmpty()
            ? List.of()
            : skillRepository.findAllActiveByIdIn(skillIdsOrdered);
    Map<Long, String> idToName = new LinkedHashMap<>();
    for (Skill s : skRows) {
      idToName.put(s.getId(), s.getName());
    }
    List<CompanyJobSkillItemResponse> skillsOut =
        skillIdsOrdered.stream()
            .map(sid -> new CompanyJobSkillItemResponse(sid, idToName.getOrDefault(sid, "")))
            .toList();
    String regionName =
        regionRepository.findById(job.getRegionId()).map(r -> r.getName()).orElse("");
    String communeName =
        communeRepository.findById(job.getCommuneId()).map(c -> c.getName()).orElse("");
    String rawImg = job.getImageUrl();
    String extImg = null;
    boolean prot = false;
    if (rawImg != null && !rawImg.isBlank()) {
      String t = rawImg.trim();
      String lower = t.toLowerCase(Locale.ROOT);
      if (lower.startsWith("http://") || lower.startsWith("https://")) {
        extImg = t;
      } else {
        prot = true;
      }
    }
    Instant published =
        job.getPublishedAt() != null
            ? job.getPublishedAt().atZone(ZoneOffset.UTC).toInstant()
            : Instant.EPOCH;
    return new CompanyJobDetailResponse(
        job.getId(),
        job.getCompanyCommercialName(),
        job.getTitle(),
        job.getDescription(),
        job.getRegionId(),
        job.getCommuneId(),
        regionName,
        communeName,
        job.getSalaryOffered(),
        job.getYearsExperienceRequired(),
        job.getModality().name(),
        job.getWorkload().name(),
        job.getImageUrl(),
        extImg,
        prot,
        job.getStatus().name(),
        published,
        skillIdsOrdered,
        skillsOut);
  }

  private ResponseEntity<?> validateJobPayload(CompanyJobCreateRequest req) {
    if (req.getCompanyCommercialName() == null || req.getCompanyCommercialName().isBlank()) {
      return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "company_commercial_name obligatorio");
    }
    if (req.getTitle() == null || req.getTitle().isBlank()) {
      return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "title obligatorio");
    }
    if (req.getDescription() == null || req.getDescription().isBlank()) {
      return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "description obligatorio");
    }
    if (req.getSalaryOffered() == null || req.getSalaryOffered() < 1) {
      return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "salary_offered invalido");
    }
    if (req.getYearsExperienceRequired() == null
        || req.getYearsExperienceRequired() < 0
        || req.getYearsExperienceRequired() > 80) {
      return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "years_experience_required invalido");
    }
    if (req.getRegionId() == null || req.getCommuneId() == null) {
      return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "region_id y commune_id obligatorios");
    }
    if (!regionRepository.existsById(req.getRegionId())) {
      return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "region_id no existe");
    }
    if (!communeRepository.existsByIdAndRegionId(req.getCommuneId(), req.getRegionId())) {
      return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "commune_id no coincide con region");
    }
    List<Long> skillIds = req.getSkillsIds();
    if (skillIds == null || skillIds.size() < 3 || skillIds.size() > 8) {
      return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "skills_ids debe tener entre 3 y 8");
    }
    if (skillIds.size() != new LinkedHashSet<>(skillIds).size()) {
      return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "skills_ids duplicados");
    }
    List<cl.duoc.worksi.entity.Skill> skills = skillRepository.findAllActiveByIdIn(skillIds);
    if (skills.size() != skillIds.size()) {
      return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "skill_id invalido o inactivo");
    }
    return null;
  }

  private static String trimNull(String s) {
    if (s == null) {
      return null;
    }
    String t = s.trim();
    return t.isEmpty() ? null : t;
  }

  private static Sort parseSort(String raw) {
    if (raw == null || raw.isBlank()) {
      return Sort.by(Sort.Direction.DESC, "createdAt");
    }
    String[] parts = raw.split(",");
    if (parts.length < 2) {
      return Sort.by(Sort.Direction.DESC, "createdAt");
    }
    String field = parts[0].trim();
    if (field.equals("created_at")) {
      field = "createdAt";
    }
    Sort.Direction dir =
        "asc".equalsIgnoreCase(parts[1].trim()) ? Sort.Direction.ASC : Sort.Direction.DESC;
    return Sort.by(dir, field);
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
