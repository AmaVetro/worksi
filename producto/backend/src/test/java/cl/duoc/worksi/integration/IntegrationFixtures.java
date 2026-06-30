package cl.duoc.worksi.integration;

import cl.duoc.worksi.entity.CandidateCv;
import cl.duoc.worksi.entity.CandidatePreferredModality;
import cl.duoc.worksi.entity.CandidatePreferredWorkload;
import cl.duoc.worksi.entity.CandidateProfile;
import cl.duoc.worksi.entity.CandidateSkill;
import cl.duoc.worksi.entity.Company;
import cl.duoc.worksi.entity.Commune;
import cl.duoc.worksi.entity.Job;
import cl.duoc.worksi.entity.JobSkill;
import cl.duoc.worksi.entity.RecruiterProfile;
import cl.duoc.worksi.entity.Region;
import cl.duoc.worksi.entity.User;
import cl.duoc.worksi.entity.enums.JobStatus;
import cl.duoc.worksi.entity.enums.Modality;
import cl.duoc.worksi.entity.enums.UserRole;
import cl.duoc.worksi.entity.enums.Workload;
import cl.duoc.worksi.repository.CandidateCvRepository;
import cl.duoc.worksi.repository.CandidatePreferredModalityRepository;
import cl.duoc.worksi.repository.CandidatePreferredWorkloadRepository;
import cl.duoc.worksi.repository.CandidateProfileRepository;
import cl.duoc.worksi.repository.CandidateSkillRepository;
import cl.duoc.worksi.repository.CompanyRepository;
import cl.duoc.worksi.repository.CommuneRepository;
import cl.duoc.worksi.repository.JobRepository;
import cl.duoc.worksi.repository.JobSkillRepository;
import cl.duoc.worksi.repository.RecruiterProfileRepository;
import cl.duoc.worksi.repository.RegionRepository;
import cl.duoc.worksi.repository.UserRepository;
import cl.duoc.worksi.security.JwtService;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;

public final class IntegrationFixtures {

  public static final String VALID_PASSWORD = "Aa!1234567890";

  public static final String JAVA_CV_TEXT =
      "Desarrollador Java senior con Spring Boot, APIs REST, microservicios, MySQL, Git, "
          + "JUnit, Maven, cinco años de experiencia en backend empresarial y equipos ágiles.";

  public static String uniqueRut() {
    String digits = Long.toString(Math.abs(UUID.randomUUID().getMostSignificantBits() % 99_999_998L) + 1_000_000L);
    return digits + "-5";
  }

  public static String uniqueCompanyRut() {
    String digits = Long.toString(Math.abs(UUID.randomUUID().getMostSignificantBits() % 89_999_998L) + 10_000_000L);
    return digits + "-7";
  }

  private IntegrationFixtures() {}

  public record SeededCandidate(User user, String token) {}

  public record SeededRecruiter(User user, String token, Company company) {}

  public record RegionCommune(Region region, Commune commune) {}

  public static RegionCommune resolveRegionCommune(
      RegionRepository regionRepository, CommuneRepository communeRepository) {
    Region region =
        regionRepository.findAll().stream()
            .filter(r -> "CL-RM".equals(r.getCode()))
            .findFirst()
            .orElseThrow();
    Commune commune =
        communeRepository.findByRegionIdAndActiveIsTrueOrderByNameAsc(region.getId()).stream()
            .filter(c -> "Santiago".equals(c.getName()))
            .findFirst()
            .orElseThrow();
    return new RegionCommune(region, commune);
  }

  public static SeededCandidate seedCandidate(
      UserRepository userRepository,
      CandidateProfileRepository candidateProfileRepository,
      CandidatePreferredModalityRepository modalityRepository,
      CandidatePreferredWorkloadRepository workloadRepository,
      CandidateCvRepository candidateCvRepository,
      CandidateSkillRepository candidateSkillRepository,
      PasswordEncoder passwordEncoder,
      JwtService jwtService,
      RegionCommune rc,
      Path cvStorageDir,
      List<Long> skillIds) {
    User user = new User();
    user.setRole(UserRole.CANDIDATE);
    user.setEmail("it-candidate-" + UUID.randomUUID() + "@worksi.test");
    user.setPasswordHash(passwordEncoder.encode(VALID_PASSWORD));
    user.setActive(true);
    user.setPasswordResetRequired(false);
    user.setFailedLoginAttempts(0);
    user = userRepository.saveAndFlush(user);

    CandidateProfile profile = new CandidateProfile();
    profile.setUserId(user.getId());
    profile.setFirstName("Ana");
    profile.setLastNamePaternal("Rojas");
    profile.setLastNameMaternal("Perez");
    profile.setPhone("+56912345678");
    profile.setRut(uniqueRut());
    profile.setDocumentNumber(Long.toString(Math.abs(UUID.randomUUID().getMostSignificantBits() % 99_999_999L)));
    profile.setRegionId(rc.region().getId());
    profile.setCommuneId(rc.commune().getId());
    profile.setSectorId(1L);
    profile.setYearsExperience(5);
    profile.setConsentGiven(true);
    profile.setConsentGivenAt(LocalDateTime.now(ZoneOffset.UTC));
    candidateProfileRepository.saveAndFlush(profile);

    modalityRepository.save(new CandidatePreferredModality(user.getId(), Modality.REMOTE));
    workloadRepository.save(new CandidatePreferredWorkload(user.getId(), Workload.FULL_TIME));

    CandidateCv cv = new CandidateCv();
    cv.setCandidateUserId(user.getId());
    cv.setOriginalFilename("cv.pdf");
    cv.setStoragePath(cvStorageDir.resolve("cv-" + user.getId() + ".pdf").toString());
    cv.setFileSizeBytes(2048);
    cv.setMimeType("application/pdf");
    cv.setNormalizedText(JAVA_CV_TEXT);
    cv.setCurrent(true);
    cv.setUploadedAt(LocalDateTime.now(ZoneOffset.UTC));
    candidateCvRepository.saveAndFlush(cv);

    for (Long skillId : skillIds) {
      candidateSkillRepository.saveAndFlush(new CandidateSkill(user.getId(), skillId));
    }

    return new SeededCandidate(user, jwtService.createToken(user));
  }

  public static SeededRecruiter seedRecruiter(
      UserRepository userRepository,
      RecruiterProfileRepository recruiterProfileRepository,
      CompanyRepository companyRepository,
      PasswordEncoder passwordEncoder,
      JwtService jwtService,
      RegionCommune rc) {
    Company company = new Company();
    company.setPhone("+56987654321");
    company.setCommercialName("Empresa IT Test");
    company.setLegalName("Empresa IT Test SpA");
    company.setRut(uniqueCompanyRut());
    company.setRegionId(rc.region().getId());
    company.setCommuneId(rc.commune().getId());
    company.setAddress("Av. Providencia 123");
    company.setCorporateEmail("contacto-" + UUID.randomUUID() + "@empresait.test");
    company.setSectorId(1L);
    company.setWorkerCountApprox(25);
    company = companyRepository.saveAndFlush(company);

    User user = new User();
    user.setRole(UserRole.RECRUITER);
    user.setEmail("it-recruiter-" + UUID.randomUUID() + "@worksi.test");
    user.setPasswordHash(passwordEncoder.encode(VALID_PASSWORD));
    user.setActive(true);
    user.setPasswordResetRequired(false);
    user.setFailedLoginAttempts(0);
    user = userRepository.saveAndFlush(user);

    RecruiterProfile recruiterProfile = new RecruiterProfile();
    recruiterProfile.setUserId(user.getId());
    recruiterProfile.setCompanyId(company.getId());
    recruiterProfile.setFirstName("Luis");
    recruiterProfile.setLastNamePaternal("Mora");
    recruiterProfile.setLastNameMaternal("Silva");
    recruiterProfile.setRut(uniqueRut());
    recruiterProfile.setMobile("+56911112222");
    recruiterProfile.setBirthDate(LocalDate.of(1988, 3, 15));
    recruiterProfileRepository.saveAndFlush(recruiterProfile);

    return new SeededRecruiter(user, jwtService.createToken(user), company);
  }

  public static long seedJob(
      JobRepository jobRepository,
      JobSkillRepository jobSkillRepository,
      Company company,
      long recruiterUserId,
      RegionCommune rc,
      String title,
      String description,
      List<Long> jobSkillIds) {
    Job job = new Job();
    job.setCompanyId(company.getId());
    job.setPublishedByUserId(recruiterUserId);
    job.setCompanyCommercialName(company.getCommercialName());
    job.setTitle(title);
    job.setDescription(description);
    job.setRegionId(rc.region().getId());
    job.setCommuneId(rc.commune().getId());
    job.setSalaryOffered(850000);
    job.setYearsExperienceRequired(3);
    job.setModality(Modality.REMOTE);
    job.setWorkload(Workload.FULL_TIME);
    job.setStatus(JobStatus.ACTIVE);
    job.setPublishedAt(LocalDateTime.now(ZoneOffset.UTC));
    long jobId = jobRepository.saveAndFlush(job).getId();

    for (Long skillId : jobSkillIds) {
      jobSkillRepository.saveAndFlush(new JobSkill(jobId, skillId));
    }
    return jobId;
  }
}
