package cl.duoc.worksi.controller;

import cl.duoc.worksi.dto.CandidateRegistrationValidateRequest;
import cl.duoc.worksi.service.RegistrationValidationService;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CandidateRegistrationValidationController {
  private final RegistrationValidationService registrationValidationService;

  public CandidateRegistrationValidationController(
      RegistrationValidationService registrationValidationService) {
    this.registrationValidationService = registrationValidationService;
  }

  @PostMapping("/api/v1/validation/candidate-registration")
  public Map<String, Object> validate(@RequestBody CandidateRegistrationValidateRequest request) {
    registrationValidationService.validate(request);
    return Map.of("valid", true);
  }
}
