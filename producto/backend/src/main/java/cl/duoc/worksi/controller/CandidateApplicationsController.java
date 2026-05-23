package cl.duoc.worksi.controller;

import cl.duoc.worksi.dto.candidate.CandidateApplicationCreatedResponse;
import cl.duoc.worksi.dto.candidate.CandidateApplicationRequest;
import cl.duoc.worksi.security.UserPrincipal;
import cl.duoc.worksi.service.CandidateApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/candidate")
public class CandidateApplicationsController {
  private final CandidateApplicationService candidateApplicationService;

  public CandidateApplicationsController(CandidateApplicationService candidateApplicationService) {
    this.candidateApplicationService = candidateApplicationService;
  }

  @PostMapping("/applications")
  public ResponseEntity<CandidateApplicationCreatedResponse> apply(
      @AuthenticationPrincipal UserPrincipal principal,
      @RequestBody CandidateApplicationRequest body) {
    return candidateApplicationService.apply(principal.getUser().getId(), body);
  }
}
