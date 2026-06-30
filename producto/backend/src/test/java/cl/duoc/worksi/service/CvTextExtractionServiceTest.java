package cl.duoc.worksi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cl.duoc.worksi.entity.CandidateCv;
import cl.duoc.worksi.repository.CandidateCvRepository;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class CvTextExtractionServiceTest {

  private static final long CV_ID = 11L;
  private static final long CANDIDATE_ID = 42L;

  @TempDir Path tempDir;

  @Mock private CandidateCvRepository candidateCvRepository;

  private CvTextExtractionService cvTextExtractionService;

  @BeforeEach
  void setUp() {
    cvTextExtractionService = new CvTextExtractionService(candidateCvRepository);
  }

  @Test
  void extractAndPersistStoresNormalizedTextFromSelectablePdf() throws IOException {
    String visible =
        "Desarrollador Java senior Spring Boot REST APIs MySQL Git microservicios backend.";
    Path pdf = writeMinimalPdf(tempDir.resolve("cv-useful.pdf"), visible);
    CandidateCv cv = cvAtPath(pdf);
    when(candidateCvRepository.findById(CV_ID)).thenReturn(Optional.of(cv));

    cvTextExtractionService.extractAndPersist(CV_ID);

    ArgumentCaptor<CandidateCv> saved = ArgumentCaptor.forClass(CandidateCv.class);
    verify(candidateCvRepository).save(saved.capture());
    String normalized = saved.getValue().getNormalizedText();
    assertTrue(normalized.contains("Desarrollador Java"));
    assertTrue(normalized.length() >= 24);
    assertTrue(saved.getValue().getExtractedText() != null);
  }

  @Test
  void extractAndPersistRejectsPdfWithoutUsefulText() throws IOException {
    Path pdf = writeMinimalPdf(tempDir.resolve("cv-useless.pdf"), "juan@x.cl");
    CandidateCv cv = cvAtPath(pdf);
    when(candidateCvRepository.findById(CV_ID)).thenReturn(Optional.of(cv));

    ResponseStatusException ex =
        assertThrows(
            ResponseStatusException.class, () -> cvTextExtractionService.extractAndPersist(CV_ID));

    assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, ex.getStatusCode());
    assertTrue(ex.getReason().contains("texto util"));
  }

  private CandidateCv cvAtPath(Path pdf) {
    CandidateCv cv = new CandidateCv();
    cv.setCandidateUserId(CANDIDATE_ID);
    cv.setOriginalFilename("cv.pdf");
    cv.setStoragePath(pdf.toString());
    cv.setFileSizeBytes(1024);
    cv.setMimeType("application/pdf");
    cv.setCurrent(true);
    cv.setUploadedAt(LocalDateTime.now(ZoneOffset.UTC));
    return cv;
  }

  static Path writeMinimalPdf(Path target, String visibleText) throws IOException {
    String safe = visibleText.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
    String stream = "BT /F1 10 Tf 10 700 Td (" + safe + ") Tj ET";
    int streamLen = stream.getBytes(StandardCharsets.US_ASCII).length;
    String pdf =
        "%PDF-1.4\n"
            + "1 0 obj<</Type/Catalog/Pages 2 0 R>>endobj\n"
            + "2 0 obj<</Type/Pages/Kids[3 0 R]/Count 1>>endobj\n"
            + "3 0 obj<</Type/Page/MediaBox[0 0 612 792]/Parent 2 0 R/Resources<</Font<</F1 4 0 R>>>>/Contents 5 0 R>>endobj\n"
            + "4 0 obj<</Type/Font/Subtype/Type1/BaseFont/Helvetica>>endobj\n"
            + "5 0 obj<</Length "
            + streamLen
            + ">>stream\n"
            + stream
            + "\nendstream\nendobj\n"
            + "xref\n0 6\n0000000000 65535 f \n"
            + "trailer<</Size 6/Root 1 0 R>>\n"
            + "startxref\n0\n%%EOF\n";
    Files.writeString(target, pdf, StandardCharsets.US_ASCII);
    return target;
  }
}
