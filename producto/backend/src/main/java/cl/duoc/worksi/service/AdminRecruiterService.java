package cl.duoc.worksi.service;

import cl.duoc.worksi.dto.admin.AdminRecruiterCreatedResponse;
import cl.duoc.worksi.dto.admin.AdminRecruiterRequest;
import cl.duoc.worksi.entity.RecruiterProfile;
import cl.duoc.worksi.entity.User;
import cl.duoc.worksi.entity.enums.UserRole;
import cl.duoc.worksi.repository.CompanyRepository;
import cl.duoc.worksi.repository.RecruiterProfileRepository;
import cl.duoc.worksi.repository.UserRepository;
import cl.duoc.worksi.validation.PasswordRules;
import cl.duoc.worksi.validation.RutRules;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
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
  private final RecruiterProfileRepository recruiterProfileRepository;
  private final PasswordEncoder passwordEncoder;

  public AdminRecruiterService(
      UserRepository userRepository,
      CompanyRepository companyRepository,
      RecruiterProfileRepository recruiterProfileRepository,
      PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.companyRepository = companyRepository;
    this.recruiterProfileRepository = recruiterProfileRepository;
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
