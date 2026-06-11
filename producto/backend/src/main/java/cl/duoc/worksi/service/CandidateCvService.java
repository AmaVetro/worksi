package cl.duoc.worksi.service;

import cl.duoc.worksi.dto.candidate.CandidateCvCurrentResponse;
import cl.duoc.worksi.entity.CandidateCv;
import cl.duoc.worksi.repository.CandidateCvRepository;
import cl.duoc.worksi.repository.CandidateProfileRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CandidateCvService {
  private static final int MAX_CV_BYTES = 1048576;

  private final CandidateCvRepository candidateCvRepository;
  private final CandidateProfileRepository candidateProfileRepository;
  private final CvTextExtractionService cvTextExtractionService;
  private final Path cvBaseDir;

  public CandidateCvService(
      CandidateCvRepository candidateCvRepository,
      CandidateProfileRepository candidateProfileRepository,
      CvTextExtractionService cvTextExtractionService,
      @Value("${worksi.storage.candidate-cvs}") String candidateCvsDir) {
    this.candidateCvRepository = candidateCvRepository;
    this.candidateProfileRepository = candidateProfileRepository;
    this.cvTextExtractionService = cvTextExtractionService;
    this.cvBaseDir = Path.of(candidateCvsDir);
  }

  public ResponseEntity<CandidateCvCurrentResponse> getCurrentMetadata(long candidateUserId) {
    requireCandidateProfile(candidateUserId);
    CandidateCv cv =
        candidateCvRepository
            .findTopByCandidateUserIdAndCurrentIsTrueOrderByUploadedAtDesc(candidateUserId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CV no encontrado"));
    return ResponseEntity.ok(toMetadata(cv));
  }

  public ResponseEntity<byte[]> getCurrentFile(long candidateUserId) {
    requireCandidateProfile(candidateUserId);
    CandidateCv cv =
        candidateCvRepository
            .findTopByCandidateUserIdAndCurrentIsTrueOrderByUploadedAtDesc(candidateUserId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CV no encontrado"));
    return serveCvFile(cv, false);
  }

  public ResponseEntity<byte[]> serveCvFile(CandidateCv cv, boolean attachment) {
    byte[] bytes = readCvBytes(cv);
    String filename = sanitizeFilename(cv.getOriginalFilename());
    String disposition = attachment ? "attachment" : "inline";
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_PDF)
        .header(HttpHeaders.CONTENT_DISPOSITION, disposition + "; filename=\"" + filename + "\"")
        .body(bytes);
  }

  public Optional<CandidateCv> findCurrentCv(long candidateUserId) {
    return candidateCvRepository.findTopByCandidateUserIdAndCurrentIsTrueOrderByUploadedAtDesc(
        candidateUserId);
  }

  @Transactional
  public ResponseEntity<CandidateCvCurrentResponse> uploadCv(
      long candidateUserId, MultipartFile file) {
    requireCandidateProfile(candidateUserId);
    if (file == null || file.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "file obligatorio");
    }
    if (file.getSize() > MAX_CV_BYTES) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El PDF supera 1 MB");
    }
    String ct = file.getContentType();
    if (ct == null || !ct.equalsIgnoreCase("application/pdf")) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solo se permite PDF");
    }
    String fn = file.getOriginalFilename();
    if (fn == null || !fn.toLowerCase(Locale.ROOT).endsWith(".pdf")) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El archivo debe ser PDF");
    }
    final byte[] pdfBytes;
    try {
      pdfBytes = file.getBytes();
    } catch (IOException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se pudo leer el archivo");
    }
    if (pdfBytes.length < 4
        || pdfBytes[0] != '%'
        || pdfBytes[1] != 'P'
        || pdfBytes[2] != 'D'
        || pdfBytes[3] != 'F') {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El archivo debe ser PDF");
    }
    for (CandidateCv old :
        candidateCvRepository.findByCandidateUserIdAndCurrentTrue(candidateUserId)) {
      old.setCurrent(false);
      candidateCvRepository.save(old);
    }
    Path userDir = cvBaseDir.resolve("candidates").resolve(String.valueOf(candidateUserId));
    try {
      Files.createDirectories(userDir);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
    String storedName = UUID.randomUUID() + ".pdf";
    Path absolutePath = userDir.resolve(storedName);
    try {
      Files.write(
          absolutePath, pdfBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
    CandidateCv cv = new CandidateCv();
    cv.setCandidateUserId(candidateUserId);
    cv.setOriginalFilename(fn.trim());
    cv.setStoragePath(absolutePath.toAbsolutePath().toString());
    cv.setFileSizeBytes(pdfBytes.length);
    cv.setMimeType("application/pdf");
    cv.setCurrent(true);
    cv.setUploadedAt(LocalDateTime.now(ZoneOffset.UTC));
    candidateCvRepository.save(cv);
    cvTextExtractionService.extractAndPersist(cv.getId());
    return ResponseEntity.status(HttpStatus.CREATED).body(toMetadata(cv));
  }

  private void requireCandidateProfile(long candidateUserId) {
    if (!candidateProfileRepository.existsById(candidateUserId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil no encontrado");
    }
  }

  private byte[] readCvBytes(CandidateCv cv) {
    Path base = cvBaseDir.toAbsolutePath().normalize();
    Path file = Path.of(cv.getStoragePath()).toAbsolutePath().normalize();
    if (!file.startsWith(base)) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Ruta de CV no permitida");
    }
    if (!Files.isRegularFile(file)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Archivo de CV no encontrado");
    }
    try {
      return Files.readAllBytes(file);
    } catch (IOException e) {
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo leer el CV");
    }
  }

  private CandidateCvCurrentResponse toMetadata(CandidateCv cv) {
    Instant uploaded =
        cv.getUploadedAt() != null ? cv.getUploadedAt().atZone(ZoneOffset.UTC).toInstant() : null;
    return new CandidateCvCurrentResponse(
        cv.getId(),
        cv.getOriginalFilename(),
        cv.getFileSizeBytes(),
        cv.isCurrent(),
        uploaded);
  }

  private String sanitizeFilename(String name) {
    if (name == null || name.isBlank()) {
      return "cv.pdf";
    }
    String trimmed = name.trim().replace("\"", "'");
    return trimmed.isEmpty() ? "cv.pdf" : trimmed;
  }

  public static ResponseEntity<Map<String, Object>> err(
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
