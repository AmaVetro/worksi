package cl.duoc.worksi.controller;

import cl.duoc.worksi.service.CandidateRegistrationService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class CandidateRegistrationController {
  private final CandidateRegistrationService candidateRegistrationService;

  public CandidateRegistrationController(CandidateRegistrationService candidateRegistrationService) {
    this.candidateRegistrationService = candidateRegistrationService;
  }

  @PostMapping(
      value = "/api/v1/auth/register/candidate",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?> register(
      @RequestPart("data") String data, @RequestPart("file") MultipartFile file) {
    return candidateRegistrationService.register(data, file);
  }
}
