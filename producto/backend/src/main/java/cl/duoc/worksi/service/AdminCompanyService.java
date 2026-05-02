package cl.duoc.worksi.service;

import cl.duoc.worksi.dto.PageResponse;
import cl.duoc.worksi.dto.admin.AdminCompanyCreatedResponse;
import cl.duoc.worksi.dto.admin.AdminCompanyDataRequest;
import cl.duoc.worksi.dto.admin.AdminCompanyListItem;
import cl.duoc.worksi.entity.Company;
import cl.duoc.worksi.repository.CommuneRepository;
import cl.duoc.worksi.repository.CompanyRepository;
import cl.duoc.worksi.repository.RegionRepository;
import cl.duoc.worksi.repository.SectorRepository;
import cl.duoc.worksi.validation.RutRules;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AdminCompanyService {
  private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of("image/png", "image/jpeg");

  private final CompanyRepository companyRepository;
  private final RegionRepository regionRepository;
  private final CommuneRepository communeRepository;
  private final SectorRepository sectorRepository;
  private final ObjectMapper objectMapper;
  private final Path companyImageBaseDir;

  public AdminCompanyService(
      CompanyRepository companyRepository,
      RegionRepository regionRepository,
      CommuneRepository communeRepository,
      SectorRepository sectorRepository,
      ObjectMapper objectMapper,
      @Value("${worksi.storage.company-images}") String companyImagesDir) {
    this.companyRepository = companyRepository;
    this.regionRepository = regionRepository;
    this.communeRepository = communeRepository;
    this.sectorRepository = sectorRepository;
    this.objectMapper = objectMapper;
    this.companyImageBaseDir = Path.of(companyImagesDir);
  }

  @Transactional
  public ResponseEntity<?> createCompany(String dataJson, MultipartFile image) {
    if (dataJson == null || dataJson.isBlank()) {
      return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Parte data obligatoria");
    }
    AdminCompanyDataRequest data;
    try {
      data = objectMapper.readValue(dataJson, AdminCompanyDataRequest.class);
    } catch (IOException e) {
      return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "JSON data invalido");
    }

    ResponseEntity<?> validation = validateCompanyPayload(data);
    if (validation != null) {
      return validation;
    }

    if (image != null && !image.isEmpty()) {
      String ct = image.getContentType();
      if (ct == null || !ALLOWED_IMAGE_TYPES.contains(ct.toLowerCase())) {
        return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Imagen debe ser PNG o JPEG");
      }
    }

    String rutNorm = RutRules.normalize(data.getRut());
    if (companyRepository.existsByRut(rutNorm)) {
      return error(HttpStatus.CONFLICT, "CONFLICT", "RUT de empresa ya registrado");
    }

    Company c = new Company();
    c.setCommercialName(data.getCommercialName().trim());
    c.setLegalName(data.getLegalName().trim());
    c.setPhone(data.getPhone().trim());
    c.setAddress(data.getAddress().trim());
    c.setRut(rutNorm);
    c.setRegionId(data.getRegionId());
    c.setCommuneId(data.getCommuneId());
    c.setSectorId(data.getSectorId());
    c.setWorkerCountApprox(data.getWorkerCountApprox());

    c = companyRepository.save(c);

    if (image != null && !image.isEmpty()) {
      try {
        Files.createDirectories(companyImageBaseDir);
        String ext = image.getContentType().toLowerCase().contains("png") ? ".png" : ".jpg";
        String filename = c.getId() + "_" + UUID.randomUUID() + ext;
        Path target = companyImageBaseDir.resolve(filename);
        try (InputStream in = image.getInputStream()) {
          Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }
        c.setImageUrl(target.toString());
        companyRepository.save(c);
      } catch (IOException e) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "No se pudo guardar la imagen");
      }
    }

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new AdminCompanyCreatedResponse(c.getId()));
  }

  public ResponseEntity<PageResponse<AdminCompanyListItem>> listCompanies(
      int page, int size, String sort) {
    int p = Math.max(1, page);
    int sz = Math.min(100, Math.max(1, size));
    Pageable pageable = PageRequest.of(p - 1, sz, parseSort(sort));
    Page<Company> result = companyRepository.findAll(pageable);
    List<AdminCompanyListItem> items =
        result.getContent().stream()
            .map(
                c ->
                    new AdminCompanyListItem(
                        c.getId(), c.getCommercialName(), c.getLegalName(), c.getRut()))
            .toList();
    PageResponse<AdminCompanyListItem> body =
        new PageResponse<>(
            items, p, result.getSize(), result.getTotalElements(), result.getTotalPages());
    return ResponseEntity.ok(body);
  }

  private ResponseEntity<?> validateCompanyPayload(AdminCompanyDataRequest data) {
    if (data.getCommercialName() == null || data.getCommercialName().isBlank()) {
      return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "commercial_name obligatorio");
    }
    if (data.getLegalName() == null || data.getLegalName().isBlank()) {
      return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "legal_name obligatorio");
    }
    if (data.getPhone() == null || data.getPhone().isBlank()) {
      return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "phone obligatorio");
    }
    if (data.getAddress() == null || data.getAddress().isBlank()) {
      return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "address obligatorio");
    }
    if (data.getRut() == null || data.getRut().isBlank()) {
      return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "rut obligatorio");
    }
    if (!RutRules.isValidChileRut(data.getRut())) {
      return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "RUT invalido");
    }
    if (data.getRegionId() == null
        || data.getCommuneId() == null
        || data.getSectorId() == null
        || data.getWorkerCountApprox() == null) {
      return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Hay campos invalidos");
    }
    if (data.getWorkerCountApprox() < 1) {
      return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "worker_count_approx invalido");
    }
    if (!regionRepository.existsById(data.getRegionId())) {
      return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "region_id no existe");
    }
    if (!sectorRepository.existsById(data.getSectorId())) {
      return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "sector_id no existe");
    }
    if (!communeRepository.existsByIdAndRegionId(data.getCommuneId(), data.getRegionId())) {
      return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "commune_id no coincide con region");
    }
    return null;
  }

  private static Sort parseSort(String raw) {
    if (raw == null || raw.isBlank()) {
      return Sort.by(Sort.Direction.DESC, "createdAt");
    }
    String[] parts = raw.split(",");
    String field = parts[0].trim();
    Sort.Direction dir =
        parts.length > 1 && "asc".equalsIgnoreCase(parts[1].trim())
            ? Sort.Direction.ASC
            : Sort.Direction.DESC;
    if (!"created_at".equals(field)) {
      return Sort.by(Sort.Direction.DESC, "createdAt");
    }
    return Sort.by(dir, "createdAt");
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
