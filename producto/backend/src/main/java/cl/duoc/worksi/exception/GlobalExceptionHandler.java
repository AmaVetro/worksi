package cl.duoc.worksi.exception;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {
  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(EmailAlreadyExistsException.class)
  public ResponseEntity<Map<String, Object>> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(error("CONFLICT", ex.getMessage(), List.of()));
  }

  @ExceptionHandler(MissingRequiredFieldException.class)
  public ResponseEntity<Map<String, Object>> handleMissingField(MissingRequiredFieldException ex) {
    Map<String, String> detail = Map.of("field", ex.getField(), "message", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(error("VALIDATION_ERROR", "Hay campos invalidos", List.of(detail)));
  }

  @ExceptionHandler(InvalidPasswordPolicyException.class)
  public ResponseEntity<Map<String, Object>> handleInvalidPassword(InvalidPasswordPolicyException ex) {
    Map<String, String> detail = Map.of("field", "password", "message", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(error("VALIDATION_ERROR", "Hay campos invalidos", List.of(detail)));
  }

  @ExceptionHandler(InvalidRutException.class)
  public ResponseEntity<Map<String, Object>> handleInvalidRut(InvalidRutException ex) {
    Map<String, String> detail = Map.of("field", "rut", "message", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(error("VALIDATION_ERROR", "Hay campos invalidos", List.of(detail)));
  }

  @ExceptionHandler(InvalidEmailFormatException.class)
  public ResponseEntity<Map<String, Object>> handleInvalidEmail(InvalidEmailFormatException ex) {
    Map<String, String> detail = Map.of("field", "email", "message", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(error("VALIDATION_ERROR", "Hay campos invalidos", List.of(detail)));
  }

  @ExceptionHandler(InvalidCommuneRegionException.class)
  public ResponseEntity<Map<String, Object>> handleInvalidCommuneRegion(InvalidCommuneRegionException ex) {
    Map<String, String> detail = Map.of("field", "commune_id", "message", ex.getMessage());
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
        .body(error("BUSINESS_RULE_VIOLATION", "Regla de negocio incumplida", List.of(detail)));
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<Map<String, Object>> handleMethodNotSupported(
      HttpRequestMethodNotSupportedException ex) {
    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
        .body(error("METHOD_NOT_ALLOWED", "Metodo no permitido", List.of()));
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<Map<String, Object>> handleNoResourceFound(NoResourceFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(error("NOT_FOUND", "Recurso no encontrado", List.of()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleUnexpected(Exception ex) {
    log.error("Unhandled exception", ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(error("INTERNAL_ERROR", "Error interno del servidor", List.of()));
  }

  private Map<String, Object> error(String code, String message, List<Map<String, String>> details) {
    return Map.of("error", Map.of("code", code, "message", message, "details", details, "trace_id", ""));
  }
}
