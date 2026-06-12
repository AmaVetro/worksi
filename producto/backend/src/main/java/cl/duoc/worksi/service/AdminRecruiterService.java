package cl.duoc.worksi.service;

import cl.duoc.worksi.dto.PageResponse;
import cl.duoc.worksi.dto.admin.AdminRecruiterCreatedResponse;
import cl.duoc.worksi.dto.admin.AdminRecruiterDetailResponse;
import cl.duoc.worksi.dto.admin.AdminRecruiterListItem;
import cl.duoc.worksi.dto.admin.AdminRecruiterRequest;
import cl.duoc.worksi.dto.admin.AdminRecruiterUpdateRequest;
import cl.duoc.worksi.entity.Commune;
import cl.duoc.worksi.entity.Company;
import cl.duoc.worksi.entity.Region;
import cl.duoc.worksi.entity.RecruiterProfile;
import cl.duoc.worksi.entity.User;
import cl.duoc.worksi.entity.enums.JobStatus;
import cl.duoc.worksi.entity.enums.UserRole;
import cl.duoc.worksi.repository.CommuneRepository;
import cl.duoc.worksi.repository.CompanyRepository;
import cl.duoc.worksi.repository.JobRepository;
import cl.duoc.worksi.repository.RegionRepository;
import cl.duoc.worksi.repository.RecruiterProfileRepository;
import cl.duoc.worksi.repository.UserRepository;
import cl.duoc.worksi.validation.PasswordRules;
import cl.duoc.worksi.validation.RutRules;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminRecruiterService {
  private static final Pattern EMAIL_PATTERN =
      Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

  private final UserRepository userRepository;
  private final CompanyRepository companyRepository;
  private final CommuneRepository communeRepository;
  private final RegionRepository regionRepository;
  private final RecruiterProfileRepository recruiterProfileRepository;
  private final JobRepository jobRepository;
  private final PasswordEncoder passwordEncoder;

  public AdminRecruiterService(
      UserRepository userRepository,
      CompanyRepository companyRepository,
      CommuneRepository communeRepository,
      RegionRepository regionRepository,
      RecruiterProfileRepository recruiterProfileRepository,
      JobRepository jobRepository,
      PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.companyRepository = companyRepository;
    this.communeRepository = communeRepository;
    this.regionRepository = regionRepository;
    this.recruiterProfileRepository = recruiterProfileRepository;
    this.jobRepository = jobRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional
  public ResponseEntity<?> createRecruiter(AdminRecruiterRequest req) {
    if (req.getEmail() == null
        || req.getPassword() == null
        || req.getFirstName() == null
        || req.getLastNamePaternal() == null
        || req.getLastNameMaternal() == null
        || req.getRut() == null
        || req.getMobile() == null
        || req.getBirthDate() == null
        || req.getCompanyId() == null) {
      return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Hay campos invalidos");
    }

    String email = req.getEmail().trim();
    if (!EMAIL_PATTERN.matcher(email).matches()) {
      return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Email invalido");
    }

    if (!PasswordRules.matches(req.getPassword())) {
      return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "La contrasena no cumple la politica");
    }

    if (!RutRules.isValidChileRut(req.getRut())) {
      return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "RUT invalido");
    }

    String rutNorm = RutRules.normalize(req.getRut());
    if (recruiterProfileRepository.existsByRut(rutNorm)) {
      return error(HttpStatus.CONFLICT, "CONFLICT", "RUT de reclutador ya registrado");
    }

    if (userRepository.existsByEmailIgnoreCase(email)) {
      return error(HttpStatus.CONFLICT, "CONFLICT", "Email ya registrado");
    }

    if (!companyRepository.existsById(req.getCompanyId())) {
      return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "company_id no existe");
    }

    LocalDate birth;
    try {
      birth = LocalDate.parse(req.getBirthDate().trim());
    } catch (Exception e) {
      return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "birth_date invalido");
    }

    User user = new User();
    user.setRole(UserRole.RECRUITER);
    user.setEmail(email);
    user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
    user.setActive(true);
    user.setFailedLoginAttempts(0);
    user.setPasswordResetRequired(false);
    user = userRepository.save(user);

    RecruiterProfile profile = new RecruiterProfile();
    profile.setUserId(user.getId());
    profile.setCompanyId(req.getCompanyId());
    profile.setFirstName(req.getFirstName().trim());
    profile.setLastNamePaternal(req.getLastNamePaternal().trim());
    profile.setLastNameMaternal(req.getLastNameMaternal().trim());
    profile.setRut(rutNorm);
    profile.setMobile(req.getMobile().trim());
    if (req.getPhone() != null && !req.getPhone().isBlank()) {
      profile.setPhone(req.getPhone().trim());
    }
    profile.setBirthDate(birth);
    recruiterProfileRepository.save(profile);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            new AdminRecruiterCreatedResponse(
                user.getId(), UserRole.RECRUITER.name(), user.getEmail(), req.getCompanyId()));
  }

  public ResponseEntity<?> getRecruiter(long recruiterUserId) {
    Optional<RecruiterProfile> profileOpt = recruiterProfileRepository.findById(recruiterUserId);
    if (profileOpt.isEmpty()) {
      return error(HttpStatus.NOT_FOUND, "NOT_FOUND", "Reclutador no encontrado");
    }
    User user = userRepository.findById(recruiterUserId).orElse(null);
    if (user == null || user.getRole() != UserRole.RECRUITER) {
      return error(HttpStatus.NOT_FOUND, "NOT_FOUND", "Reclutador no encontrado");
    }
    return ResponseEntity.ok(toDetail(user, profileOpt.get()));
  }

  @Transactional
  public ResponseEntity<?> updateRecruiter(long recruiterUserId, AdminRecruiterUpdateRequest req) {
    Optional<RecruiterProfile> profileOpt = recruiterProfileRepository.findById(recruiterUserId);
    if (profileOpt.isEmpty()) {
      return error(HttpStatus.NOT_FOUND, "NOT_FOUND", "Reclutador no encontrado");
    }
    User user = userRepository.findById(recruiterUserId).orElse(null);
    if (user == null || user.getRole() != UserRole.RECRUITER) {
      return error(HttpStatus.NOT_FOUND, "NOT_FOUND", "Reclutador no encontrado");
    }
    if (req.getEmail() == null
        || req.getFirstName() == null
        || req.getLastNamePaternal() == null
        || req.getLastNameMaternal() == null
        || req.getRut() == null
        || req.getMobile() == null
        || req.getBirthDate() == null
        || req.getCompanyId() == null) {
      return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Hay campos invalidos");
    }
    String email = req.getEmail().trim();
    if (!EMAIL_PATTERN.matcher(email).matches()) {
      return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Email invalido");
    }
    if (req.getPassword() != null && !req.getPassword().isBlank()) {
      if (!PasswordRules.matches(req.getPassword())) {
        return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "La contrasena no cumple la politica");
      }
    }
    if (!RutRules.isValidChileRut(req.getRut())) {
      return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "RUT invalido");
    }
    String rutNorm = RutRules.normalize(req.getRut());
    if (recruiterProfileRepository.existsByRutAndUserIdNot(rutNorm, recruiterUserId)) {
      return error(HttpStatus.CONFLICT, "CONFLICT", "RUT de reclutador ya registrado");
    }
    Optional<User> emailOwner = userRepository.findByEmailIgnoreCase(email);
    if (emailOwner.isPresent() && !emailOwner.get().getId().equals(recruiterUserId)) {
      return error(HttpStatus.CONFLICT, "CONFLICT", "Email ya registrado");
    }
    if (!companyRepository.existsById(req.getCompanyId())) {
      return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "company_id no existe");
    }
    LocalDate birth;
    try {
      birth = LocalDate.parse(req.getBirthDate().trim());
    } catch (Exception e) {
      return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "birth_date invalido");
    }
    user.setEmail(email);
    if (req.getPassword() != null && !req.getPassword().isBlank()) {
      user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
    }
    userRepository.save(user);
    RecruiterProfile profile = profileOpt.get();
    profile.setCompanyId(req.getCompanyId());
    profile.setFirstName(req.getFirstName().trim());
    profile.setLastNamePaternal(req.getLastNamePaternal().trim());
    profile.setLastNameMaternal(req.getLastNameMaternal().trim());
    profile.setRut(rutNorm);
    profile.setMobile(req.getMobile().trim());
    if (req.getPhone() != null && !req.getPhone().isBlank()) {
      profile.setPhone(req.getPhone().trim());
    } else {
      profile.setPhone(null);
    }
    profile.setBirthDate(birth);
    recruiterProfileRepository.save(profile);
    return ResponseEntity.ok(toDetail(user, profile));
  }

  @Transactional
  public ResponseEntity<?> deleteRecruiter(long recruiterUserId) {
    Optional<RecruiterProfile> profileOpt = recruiterProfileRepository.findById(recruiterUserId);
    if (profileOpt.isEmpty()) {
      return error(HttpStatus.NOT_FOUND, "NOT_FOUND", "Reclutador no encontrado");
    }
    User user = userRepository.findById(recruiterUserId).orElse(null);
    if (user == null || user.getRole() != UserRole.RECRUITER) {
      return error(HttpStatus.NOT_FOUND, "NOT_FOUND", "Reclutador no encontrado");
    }
    if (jobRepository.countByPublishedByUserIdAndStatusNot(recruiterUserId, JobStatus.DELETED) > 0) {
      return error(
          HttpStatus.CONFLICT,
          "CONFLICT",
          "No se puede eliminar: el reclutador tiene ofertas publicadas");
    }
    userRepository.delete(user);
    return ResponseEntity.noContent().build();
  }

  public ResponseEntity<PageResponse<AdminRecruiterListItem>> listRecruiters(
      int page, int size, String sort) {
    int p = Math.max(1, page);
    int sz = Math.min(100, Math.max(1, size));
    Pageable pageable = PageRequest.of(p - 1, sz, parseSort(sort));
    Page<RecruiterProfile> result = recruiterProfileRepository.findAll(pageable);
    Map<Long, User> users =
        userRepository
            .findAllById(
                result.getContent().stream().map(RecruiterProfile::getUserId).toList())
            .stream()
            .collect(Collectors.toMap(User::getId, u -> u));
    Map<Long, Company> companies =
        companyRepository
            .findAllById(
                result.getContent().stream().map(RecruiterProfile::getCompanyId).toList())
            .stream()
            .collect(Collectors.toMap(Company::getId, c -> c));
    List<AdminRecruiterListItem> items =
        result.getContent().stream()
            .map(
                rp -> {
                  User u = users.get(rp.getUserId());
                  Company co = companies.get(rp.getCompanyId());
                  String contactPhone = contactPhone(rp);
                  String regionName = regionNameForCompany(co);
                  String communeName = communeNameForCompany(co);
                  return new AdminRecruiterListItem(
                      rp.getUserId(),
                      u != null ? u.getEmail() : "",
                      UserRole.RECRUITER.name(),
                      rp.getFirstName(),
                      rp.getLastNamePaternal(),
                      rp.getLastNameMaternal(),
                      rp.getCompanyId(),
                      co != null ? co.getCommercialName() : "",
                      contactPhone,
                      regionName,
                      communeName);
                })
            .toList();
    return ResponseEntity.ok(
        new PageResponse<>(
            items, p, result.getSize(), result.getTotalElements(), result.getTotalPages()));
  }

  private AdminRecruiterDetailResponse toDetail(User user, RecruiterProfile profile) {
    String phone = profile.getPhone() != null ? profile.getPhone() : "";
    return new AdminRecruiterDetailResponse(
        user.getId(),
        user.getEmail(),
        profile.getFirstName(),
        profile.getLastNamePaternal(),
        profile.getLastNameMaternal(),
        profile.getRut(),
        phone,
        profile.getMobile(),
        profile.getBirthDate().toString(),
        profile.getCompanyId());
  }

  private static String contactPhone(RecruiterProfile rp) {
    if (rp.getPhone() != null && !rp.getPhone().isBlank()) {
      return rp.getPhone().trim();
    }
    return rp.getMobile() != null ? rp.getMobile().trim() : "";
  }

  private String regionNameForCompany(Company company) {
    if (company == null) {
      return "";
    }
    return regionRepository.findById(company.getRegionId()).map(Region::getName).orElse("");
  }

  private String communeNameForCompany(Company company) {
    if (company == null) {
      return "";
    }
    return communeRepository
        .findById(company.getCommuneId())
        .filter(cm -> cm.getRegionId().equals(company.getRegionId()))
        .map(Commune::getName)
        .orElse("");
  }

  private static Sort parseSort(String raw) {
    if (raw == null || raw.isBlank()) {
      return Sort.by(Sort.Direction.DESC, "userId");
    }
    String[] parts = raw.split(",");
    String field = parts[0].trim();
    Sort.Direction dir =
        parts.length > 1 && "asc".equalsIgnoreCase(parts[1].trim())
            ? Sort.Direction.ASC
            : Sort.Direction.DESC;
    if ("created_at".equals(field)) {
      field = "userId";
    }
    return Sort.by(dir, field);
  }

  private static ResponseEntity<Map<String, Object>> error(
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
