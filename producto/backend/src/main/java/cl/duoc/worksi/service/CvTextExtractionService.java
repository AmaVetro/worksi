package cl.duoc.worksi.service;

import cl.duoc.worksi.entity.CandidateCv;
import cl.duoc.worksi.repository.CandidateCvRepository;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.xml.sax.SAXException;

@Service
public class CvTextExtractionService {
  private static final int MIN_USEFUL_CHARS = 24;
  private final Parser parser = new AutoDetectParser();
  private final CandidateCvRepository candidateCvRepository;

  public CvTextExtractionService(CandidateCvRepository candidateCvRepository) {
    this.candidateCvRepository = candidateCvRepository;
  }

  public void extractAndPersist(long cvId) {
    CandidateCv cv =
        candidateCvRepository
            .findById(cvId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CV no encontrado"));
    Path path = Path.of(cv.getStoragePath());
    byte[] bytes;
    try {
      bytes = Files.readAllBytes(path);
    } catch (IOException e) {
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo leer el archivo CV");
    }
    BodyContentHandler handler = new BodyContentHandler(-1);
    Metadata metadata = new Metadata();
    ParseContext context = new ParseContext();
    try {
      parser.parse(new ByteArrayInputStream(bytes), handler, metadata, context);
    } catch (IOException | SAXException | TikaException e) {
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY,
          "No se pudo extraer texto del PDF; verifica que tenga texto seleccionable");
    }
    String raw = handler.toString();
    if (raw == null) {
      raw = "";
    }
    String normalized = normalizeExtracted(raw);
    if (countLettersAndDigits(normalized) < MIN_USEFUL_CHARS) {
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY,
          "El PDF no entrega texto util seleccionable (sin OCR en MVP)");
    }
    cv.setExtractedText(raw);
    cv.setNormalizedText(normalized);
    candidateCvRepository.save(cv);
  }

  static String normalizeExtracted(String raw) {
    String s = Normalizer.normalize(raw, Normalizer.Form.NFKC);
    s = s.replace('\r', '\n');
    s = s.replace('\u00a0', ' ');
    s = s.replaceAll("[\\t\\f\\v]+", " ");
    s = s.replaceAll(" +\n", "\n");
    s = s.replaceAll("\n{3,}", "\n\n");
    s = s.replaceAll(" {2,}", " ");
    s = s.replaceAll("\\n ", "\n");
    return s.trim();
  }

  private static int countLettersAndDigits(String s) {
    int n = 0;
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (Character.isLetterOrDigit(c)) {
        n++;
      }
    }
    return n;
  }
}
