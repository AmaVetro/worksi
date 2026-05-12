package cl.duoc.worksi.service;

import cl.duoc.worksi.dto.candidate.CandidateRegisterDataRequest;
import cl.duoc.worksi.dto.candidate.CandidateRegisterResponse;
import cl.duoc.worksi.entity.CandidateCv;
import cl.duoc.worksi.entity.CandidatePreferredModality;
import cl.duoc.worksi.entity.CandidatePreferredWorkload;
import cl.duoc.worksi.entity.CandidateProfile;
import cl.duoc.worksi.entity.CandidateSkill;
import cl.duoc.worksi.entity.Skill;
import cl.duoc.worksi.entity.User;
import cl.duoc.worksi.entity.enums.Modality;
import cl.duoc.worksi.entity.enums.UserRole;
import cl.duoc.worksi.entity.enums.Workload;
import cl.duoc.worksi.repository.CandidateCvRepository;
import cl.duoc.worksi.repository.CandidatePreferredModalityRepository;
import cl.duoc.worksi.repository.CandidatePreferredWorkloadRepository;
import cl.duoc.worksi.repository.CandidateProfileRepository;
import cl.duoc.worksi.repository.CandidateSkillRepository;
import cl.duoc.worksi.repository.CommuneRepository;
import cl.duoc.worksi.repository.RegionRepository;
import cl.duoc.worksi.repository.SectorRepository;
import cl.duoc.worksi.repository.SkillRepository;
import cl.duoc.worksi.repository.UserRepository;
import cl.duoc.worksi.security.JwtService;
import cl.duoc.worksi.validation.PasswordRules;
import cl.duoc.worksi.validation.RutRules;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CandidateRegistrationService {
  private static final int MAX_CV_BYTES = 1048576;
  private static final Pattern EMAIL_PATTERN =
      Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9][A-Za-z0-9.-]*\\.[A-Za-z]{2,}$");

  private final UserRepository userRepository;
  private final CandidateProfileRepository candidateProfileRepository;
  private final CandidateSkillRepository candidateSkillRepository;
  private final CandidatePreferredModalityRepository candidatePreferredModalityRepository;
  private final CandidatePreferredWorkloadRepository candidatePreferredWorkloadRepository;
  private final CandidateCvRepository candidateCvRepository;
  private final RegionRepository regionRepository;
  private final CommuneRepository communeRepository;
  private final SectorRepository sectorRepository;
  private final SkillRepository skillRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final ObjectMapper objectMapper;
  private final Path cvBaseDir;
  private final int jwtExpirationSeconds;
  private final CvTextExtractionService cvTextExtractionService;

  public CandidateRegistrationService(
      UserRepository userRepository,
      CandidateProfileRepository candidateProfileRepository,
      CandidateSkillRepository candidateSkillRepository,
      CandidatePreferredModalityRepository candidatePreferredModalityRepository,
      CandidatePreferredWorkloadRepository candidatePreferredWorkloadRepository,
      CandidateCvRepository candidateCvRepository,
      RegionRepository regionRepository,
      CommuneRepository communeRepository,
      SectorRepository sectorRepository,
      SkillRepository skillRepository,
      PasswordEncoder passwordEncoder,
      JwtService jwtService,
      ObjectMapper objectMapper,
      @Value("${worksi.storage.candidate-cvs}") String candidateCvsDir,
      @Value("${worksi.jwt.expiration-seconds}") int jwtExpirationSeconds,
      CvTextExtractionService cvTextExtractionService) {
    this.userRepository = userRepository;
    this.candidateProfileRepository = candidateProfileRepository;
    this.candidateSkillRepository = candidateSkillRepository;
    this.candidatePreferredModalityRepository = candidatePreferredModalityRepository;
    this.candidatePreferredWorkloadRepository = candidatePreferredWorkloadRepository;
    this.candidateCvRepository = candidateCvRepository;
    this.regionRepository = regionRepository;
    this.communeRepository = communeRepository;
    this.sectorRepository = sectorRepository;
    this.skillRepository = skillRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
    this.objectMapper = objectMapper;
    this.cvBaseDir = Path.of(candidateCvsDir);
    this.jwtExpirationSeconds = jwtExpirationSeconds;
    this.cvTextExtractionService = cvTextExtractionService;
  }

  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<?> register(String dataJson, MultipartFile file) {
    if (dataJson == null || dataJson.isBlank()) {
      return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Parte data obligatoria");
    }
    CandidateRegisterDataRequest data;
    try {
      data = objectMapper.readValue(dataJson, CandidateRegisterDataRequest.class);
    } catch (IOException e) {
      return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "JSON data invalido");
    }

    ResponseEntity<?> v = validatePayload(data);
    if (v != null) {
      return v;
    }

    if (file == null || file.isEmpty()) {
      return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Archivo CV obligatorio");
    }
    if (file.getSize() > MAX_CV_BYTES) {
      return err(HttpStatus.PAYLOAD_TOO_LARGE, "PAYLOAD_TOO_LARGE", "CV supera 1 MB");
    }
    String ct = file.getContentType();
    if (ct == null || !ct.equalsIgnoreCase("application/pdf")) {
      return err(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "UNSUPPORTED_MEDIA_TYPE", "Solo PDF permitido");
    }
    String fn = file.getOriginalFilename();
    if (fn == null || !fn.toLowerCase().endsWith(".pdf")) {
      return err(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "UNSUPPORTED_MEDIA_TYPE", "Solo PDF permitido");
    }

    final byte[] pdfBytes;
    try {
      pdfBytes = file.getBytes();
    } catch (IOException e) {
      return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "No se pudo leer el CV");
    }
    if (pdfBytes.length < 4
        || pdfBytes[0] != '%'
        || pdfBytes[1] != 'P'
        || pdfBytes[2] != 'D'
        || pdfBytes[3] != 'F') {
      return err(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "UNSUPPORTED_MEDIA_TYPE", "Archivo no es PDF valido");
    }

    String email = data.getEmail().trim().toLowerCase();
    String rutNorm = RutRules.normalize(data.getRut());
    if (userRepository.existsByEmailIgnoreCase(email)) {
      return err(HttpStatus.CONFLICT, "CONFLICT", "Email ya registrado");
    }
    if (candidateProfileRepository.existsByRut(rutNorm)) {
      return err(HttpStatus.CONFLICT, "CONFLICT", "RUT ya registrado");
    }

    LocalDateTime consentAt = LocalDateTime.now(ZoneOffset.UTC);

    User user = new User();
    user.setRole(UserRole.CANDIDATE);
    user.setEmail(email);
    user.setPasswordHash(passwordEncoder.encode(data.getPassword()));
    user.setActive(true);
    user.setFailedLoginAttempts(0);
    user.setPasswordResetRequired(false);
    try {
      user = userRepository.saveAndFlush(user);
    } catch (DataIntegrityViolationException e) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Email ya registrado");
    }

    long uid = user.getId();

    CandidateProfile profile = new CandidateProfile();
    profile.setUserId(uid);
    profile.setFirstName(trimReq(data.getFirstName()));
    profile.setMiddleName(trimNull(data.getMiddleName()));
    profile.setLastNamePaternal(trimReq(data.getLastNamePaternal()));
    profile.setLastNameMaternal(trimReq(data.getLastNameMaternal()));
    profile.setPhone(data.getPhone().trim());
    profile.setRut(rutNorm);
    profile.setDocumentNumber(data.getDocumentNumber().trim());
    profile.setStreet(trimNull(data.getStreet()));
    profile.setRegionId(data.getRegionId());
    profile.setCommuneId(data.getCommuneId());
    profile.setSectorId(data.getSectorId());
    profile.setProfileSummary(trimNull(data.getProfileSummary()));
    profile.setSalaryExpectedMin(data.getSalaryExpectedMin());
    profile.setSalaryExpectedMax(data.getSalaryExpectedMax());
    profile.setConsentGiven(true);
    profile.setConsentGivenAt(consentAt);
    try {
      candidateProfileRepository.saveAndFlush(profile);
    } catch (DataIntegrityViolationException e) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "RUT ya registrado");
    }

    for (Long sid : new LinkedHashSet<>(data.getSkillsIds())) {
      candidateSkillRepository.save(new CandidateSkill(uid, sid));
    }

    for (String m : data.getPreferredModalities()) {
      candidatePreferredModalityRepository.save(
          new CandidatePreferredModality(uid, Modality.valueOf(m)));
    }
    for (String w : data.getPreferredWorkloads()) {
      candidatePreferredWorkloadRepository.save(
          new CandidatePreferredWorkload(uid, Workload.valueOf(w)));
    }

    Path userDir = cvBaseDir.resolve("candidates").resolve(String.valueOf(uid));
    try {
      Files.createDirectories(userDir);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
    String storedName = java.util.UUID.randomUUID() + ".pdf";
    Path absolutePath = userDir.resolve(storedName);
    try {
      Files.write(absolutePath, pdfBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }

    CandidateCv cv = new CandidateCv();
    cv.setCandidateUserId(uid);
    cv.setOriginalFilename(fn);
    cv.setStoragePath(absolutePath.toAbsolutePath().toString());
    cv.setFileSizeBytes(pdfBytes.length);
    cv.setMimeType("application/pdf");
    cv.setCurrent(true);
    cv.setUploadedAt(LocalDateTime.now(ZoneOffset.UTC));
    candidateCvRepository.save(cv);

    cvTextExtractionService.extractAndPersist(cv.getId());

    String token = jwtService.createToken(user);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            new CandidateRegisterResponse(
                uid, UserRole.CANDIDATE.name(), token, "Bearer", jwtExpirationSeconds));
  }

  private ResponseEntity<?> validatePayload(CandidateRegisterDataRequest d) {
    if (d.getConsentGiven() == null || !d.getConsentGiven()) {
      return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "consent_given debe ser true");
    }
    if (d.getEmail() == null || d.getEmail().isBlank()) {
      return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "email obligatorio");
    }
    String em = d.getEmail().trim();
    if (!EMAIL_PATTERN.matcher(em).matches()) {
      return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "email invalido");
    }
    if (d.getPassword() == null || !PasswordRules.matches(d.getPassword())) {
      return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Contrasena no cumple politica");
    }
    if (d.getFirstName() == null || d.getFirstName().isBlank()) {
      return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "first_name obligatorio");
    }
    if (d.getLastNamePaternal() == null || d.getLastNamePaternal().isBlank()) {
      return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "last_name_paternal obligatorio");
    }
    if (d.getLastNameMaternal() == null || d.getLastNameMaternal().isBlank()) {
      return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "last_name_maternal obligatorio");
    }
    if (d.getPhone() == null || d.getPhone().isBlank()) {
      return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "phone obligatorio");
    }
    if (d.getRut() == null || !RutRules.isValidChileRut(d.getRut())) {
      return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "RUT invalido");
    }
    if (d.getDocumentNumber() == null || d.getDocumentNumber().isBlank()) {
      return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "document_number obligatorio");
    }
    if (d.getRegionId() == null || d.getCommuneId() == null || d.getSectorId() == null) {
      return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Hay campos invalidos");
    }
    if (!regionRepository.existsById(d.getRegionId())) {
      return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "region_id no existe");
    }
    if (!sectorRepository.existsByIdAndActiveIsTrue(d.getSectorId())) {
      return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "sector_id invalido");
    }
    if (!communeRepository.existsByIdAndRegionId(d.getCommuneId(), d.getRegionId())) {
      return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "commune_id no coincide con region");
    }

    Integer smin = d.getSalaryExpectedMin();
    Integer smax = d.getSalaryExpectedMax();
    if ((smin == null) != (smax == null)) {
      return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Rango de sueldo incompleto");
    }
    if (smin != null && smax != null && smin > smax) {
      return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Rango de sueldo incoherente");
    }

    List<Long> skillIds = d.getSkillsIds();
    if (skillIds == null || skillIds.size() < 3 || skillIds.size() > 12) {
      return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "skills_ids debe tener entre 3 y 12");
    }
    if (skillIds.size() != new HashSet<>(skillIds).size()) {
      return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "skills_ids duplicados");
    }
    List<Skill> allowed = skillRepository.findActiveBySectorId(d.getSectorId());
    Set<Long> allowedSet = new HashSet<>();
    for (Skill s : allowed) {
      allowedSet.add(s.getId());
    }
    for (Long sid : skillIds) {
      if (!allowedSet.contains(sid)) {
        return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "skill no pertenece al rubro");
      }
    }

    List<String> mods = d.getPreferredModalities();
    List<String> loads = d.getPreferredWorkloads();
    if (mods == null || mods.isEmpty()) {
      return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "preferred_modalities obligatorio");
    }
    if (loads == null || loads.isEmpty()) {
      return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "preferred_workloads obligatorio");
    }
    try {
      for (String m : mods) {
        Modality.valueOf(m);
      }
      for (String w : loads) {
        Workload.valueOf(w);
      }
    } catch (IllegalArgumentException e) {
      return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Enums de preferencias invalidos");
    }

    return null;
  }

  private static String trimReq(String s) {
    return s == null ? "" : s.trim();
  }

  private static String trimNull(String s) {
    if (s == null) {
      return null;
    }
    String t = s.trim();
    return t.isEmpty() ? null : t;
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
