package cl.duoc.worksi.service;

import cl.duoc.worksi.dto.candidate.CandidateProfilePatchRequest;
import cl.duoc.worksi.dto.candidate.CandidateProfileResponse;
import cl.duoc.worksi.dto.candidate.CandidateProfileSkillResponse;
import cl.duoc.worksi.entity.CandidatePreferredModality;
import cl.duoc.worksi.entity.CandidatePreferredWorkload;
import cl.duoc.worksi.entity.CandidateProfile;
import cl.duoc.worksi.entity.CandidateSkill;
import cl.duoc.worksi.entity.Skill;
import cl.duoc.worksi.entity.User;
import cl.duoc.worksi.entity.enums.Modality;
import cl.duoc.worksi.entity.enums.Workload;
import cl.duoc.worksi.repository.CandidatePreferredModalityRepository;
import cl.duoc.worksi.repository.CandidatePreferredWorkloadRepository;
import cl.duoc.worksi.repository.CandidateProfileRepository;
import cl.duoc.worksi.repository.CandidateSkillRepository;
import cl.duoc.worksi.repository.CommuneRepository;
import cl.duoc.worksi.repository.RegionRepository;
import cl.duoc.worksi.repository.SectorRepository;
import cl.duoc.worksi.repository.SkillRepository;
import cl.duoc.worksi.repository.UserRepository;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CandidateProfileService {
  private final CandidateProfileRepository candidateProfileRepository;
  private final UserRepository userRepository;
  private final CandidateSkillRepository candidateSkillRepository;
  private final CandidatePreferredModalityRepository candidatePreferredModalityRepository;
  private final CandidatePreferredWorkloadRepository candidatePreferredWorkloadRepository;
  private final SkillRepository skillRepository;
  private final RegionRepository regionRepository;
  private final CommuneRepository communeRepository;
  private final SectorRepository sectorRepository;

  public CandidateProfileService(
      CandidateProfileRepository candidateProfileRepository,
      UserRepository userRepository,
      CandidateSkillRepository candidateSkillRepository,
      CandidatePreferredModalityRepository candidatePreferredModalityRepository,
      CandidatePreferredWorkloadRepository candidatePreferredWorkloadRepository,
      SkillRepository skillRepository,
      RegionRepository regionRepository,
      CommuneRepository communeRepository,
      SectorRepository sectorRepository) {
    this.candidateProfileRepository = candidateProfileRepository;
    this.userRepository = userRepository;
    this.candidateSkillRepository = candidateSkillRepository;
    this.candidatePreferredModalityRepository = candidatePreferredModalityRepository;
    this.candidatePreferredWorkloadRepository = candidatePreferredWorkloadRepository;
    this.skillRepository = skillRepository;
    this.regionRepository = regionRepository;
    this.communeRepository = communeRepository;
    this.sectorRepository = sectorRepository;
  }

  public ResponseEntity<?> getProfile(long userId) {
    Optional<CandidateProfile> opt = candidateProfileRepository.findById(userId);
    if (opt.isEmpty()) {
      return err(HttpStatus.NOT_FOUND, "NOT_FOUND", "Perfil no encontrado");
    }
    return ResponseEntity.ok(toResponse(opt.get()));
  }

  @Transactional
  public ResponseEntity<?> patchProfile(long userId, CandidateProfilePatchRequest req) {
    Optional<CandidateProfile> opt = candidateProfileRepository.findById(userId);
    if (opt.isEmpty()) {
      return err(HttpStatus.NOT_FOUND, "NOT_FOUND", "Perfil no encontrado");
    }
    CandidateProfile profile = opt.get();

    if (req.getRegionId() != null && req.getCommuneId() == null) {
      return err(
          HttpStatus.BAD_REQUEST,
          "VALIDATION_ERROR",
          "commune_id obligatorio si se envia region_id");
    }

    long regionId = profile.getRegionId();
    long communeId = profile.getCommuneId();
    if (req.getRegionId() != null) {
      regionId = req.getRegionId();
    }
    if (req.getCommuneId() != null) {
      communeId = req.getCommuneId();
    }

    if (req.getRegionId() != null && !regionRepository.existsById(req.getRegionId())) {
      return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "region_id no existe");
    }
    if (req.getSectorId() != null && !sectorRepository.existsByIdAndActiveIsTrue(req.getSectorId())) {
      return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "sector_id invalido");
    }
    if (req.getRegionId() != null || req.getCommuneId() != null) {
      if (!communeRepository.existsByIdAndRegionId(communeId, regionId)) {
        return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "commune_id no coincide con region");
      }
      profile.setRegionId(regionId);
      profile.setCommuneId(communeId);
    }

    if (req.getSectorId() != null) {
      profile.setSectorId(req.getSectorId());
    }

    if (req.getProfileSummary() != null) {
      String s = req.getProfileSummary().trim();
      profile.setProfileSummary(s.isEmpty() ? null : s);
    }

    Integer smin = req.getSalaryExpectedMin();
    Integer smax = req.getSalaryExpectedMax();
    if (smin != null || smax != null) {
      if (smin == null || smax == null) {
        return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Rango de sueldo incompleto");
      }
      if (smin > smax) {
        return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Rango de sueldo incoherente");
      }
      profile.setSalaryExpectedMin(smin);
      profile.setSalaryExpectedMax(smax);
    }

    if (req.getYearsExperience() != null) {
      int years = req.getYearsExperience();
      if (years < 0 || years > 50) {
        return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "years_experience invalido");
      }
      profile.setYearsExperience(years);
    }

    if (req.getPreferredModalities() != null) {
      if (req.getPreferredModalities().isEmpty()) {
        return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "preferred_modalities invalido");
      }
      try {
        for (String m : req.getPreferredModalities()) {
          Modality.valueOf(m);
        }
      } catch (IllegalArgumentException e) {
        return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "preferred_modalities invalido");
      }
      candidatePreferredModalityRepository.deleteByCandidateUserId(userId);
      candidatePreferredModalityRepository.flush();
      for (String m : req.getPreferredModalities()) {
        candidatePreferredModalityRepository.save(
            new CandidatePreferredModality(userId, Modality.valueOf(m)));
      }
    }

    if (req.getPreferredWorkloads() != null) {
      if (req.getPreferredWorkloads().isEmpty()) {
        return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "preferred_workloads invalido");
      }
      try {
        for (String w : req.getPreferredWorkloads()) {
          Workload.valueOf(w);
        }
      } catch (IllegalArgumentException e) {
        return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "preferred_workloads invalido");
      }
      candidatePreferredWorkloadRepository.deleteByCandidateUserId(userId);
      candidatePreferredWorkloadRepository.flush();
      for (String w : req.getPreferredWorkloads()) {
        candidatePreferredWorkloadRepository.save(
            new CandidatePreferredWorkload(userId, Workload.valueOf(w)));
      }
    }

    if (req.getSkillsIds() != null) {
      List<Long> skillIds = req.getSkillsIds();
      if (skillIds.size() < 3 || skillIds.size() > 12) {
        return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "skills_ids debe tener entre 3 y 12");
      }
      if (skillIds.size() != new HashSet<>(skillIds).size()) {
        return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "skills_ids duplicados");
      }
      long effectiveSector = profile.getSectorId() == null ? -1L : profile.getSectorId();
      if (effectiveSector < 1) {
        return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "sector_id requerido para skills");
      }
      List<Skill> allowed = skillRepository.findActiveBySectorId(effectiveSector);
      Set<Long> allowedSet = new HashSet<>();
      for (Skill s : allowed) {
        allowedSet.add(s.getId());
      }
      for (Long sid : skillIds) {
        if (!allowedSet.contains(sid)) {
          return err(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "skill no pertenece al rubro");
        }
      }
      candidateSkillRepository.deleteByCandidateUserId(userId);
      candidateSkillRepository.flush();
      for (Long sid : new LinkedHashSet<>(skillIds)) {
        candidateSkillRepository.save(new CandidateSkill(userId, sid));
      }
    }

    candidateProfileRepository.save(profile);
    candidateProfileRepository.flush();

    CandidateProfile fresh =
        candidateProfileRepository.findById(userId).orElseThrow(() -> new IllegalStateException());
    return ResponseEntity.ok(toResponse(fresh));
  }

  private CandidateProfileResponse toResponse(CandidateProfile p) {
    User u =
        userRepository.findById(p.getUserId()).orElseThrow(() -> new IllegalStateException());

    List<String> modalities = new ArrayList<>();
    for (CandidatePreferredModality m : candidatePreferredModalityRepository.findByCandidateUserId(p.getUserId())) {
      modalities.add(m.getModality().name());
    }
    List<String> workloads = new ArrayList<>();
    for (CandidatePreferredWorkload w : candidatePreferredWorkloadRepository.findByCandidateUserId(p.getUserId())) {
      workloads.add(w.getWorkload().name());
    }

    List<CandidateSkill> links = candidateSkillRepository.findByIdCandidateUserIdOrderByIdSkillIdAsc(p.getUserId());
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

    String consentAt =
        p.getConsentGivenAt() == null
            ? null
            : p.getConsentGivenAt().atOffset(ZoneOffset.UTC).toInstant().toString();

    return new CandidateProfileResponse(
        p.getUserId(),
        p.getFirstName(),
        p.getMiddleName(),
        p.getLastNamePaternal(),
        p.getLastNameMaternal(),
        p.getPhone(),
        u.getEmail(),
        p.getRegionId(),
        p.getCommuneId(),
        p.getSectorId(),
        p.getProfileSummary(),
        p.getSalaryExpectedMin(),
        p.getSalaryExpectedMax(),
        p.getYearsExperience(),
        modalities,
        workloads,
        skillsOut,
        p.isConsentGiven(),
        consentAt);
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
