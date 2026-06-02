package cl.duoc.worksi.controller;

import cl.duoc.worksi.dto.candidate.CandidateApplicationCreatedResponse;
import cl.duoc.worksi.dto.candidate.CandidateApplicationRequest;
import cl.duoc.worksi.security.UserPrincipal;
import cl.duoc.worksi.service.CandidateApplicationService;
import cl.duoc.worksi.service.CandidateApplicationsQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/candidate")
public class CandidateApplicationsController {
  private final CandidateApplicationService candidateApplicationService;
  private final CandidateApplicationsQueryService candidateApplicationsQueryService;

  public CandidateApplicationsController(
      CandidateApplicationService candidateApplicationService,
      CandidateApplicationsQueryService candidateApplicationsQueryService) {
    this.candidateApplicationService = candidateApplicationService;
    this.candidateApplicationsQueryService = candidateApplicationsQueryService;
  }

  @PostMapping("/applications")
  public ResponseEntity<CandidateApplicationCreatedResponse> apply(
      @AuthenticationPrincipal UserPrincipal principal,
      @RequestBody CandidateApplicationRequest body) {
    return candidateApplicationService.apply(principal.getUser().getId(), body);
  }

  @GetMapping("/applications")
  public ResponseEntity<?> listMine(
      @AuthenticationPrincipal UserPrincipal principal,
      @RequestParam(name = "page", defaultValue = "1") int page,
      @RequestParam(name = "size", defaultValue = "20") int size,
      @RequestParam(name = "sort", defaultValue = "applied_at,desc") String sort) {
    return candidateApplicationsQueryService.listMine(
        principal.getUser().getId(), page, size, sort);
  }

  @GetMapping("/applications/{application_id}")
  public ResponseEntity<?> getMine(
      @AuthenticationPrincipal UserPrincipal principal,
      @PathVariable("application_id") long applicationId) {
    return candidateApplicationsQueryService.getMine(
        principal.getUser().getId(), applicationId);
  }
}
