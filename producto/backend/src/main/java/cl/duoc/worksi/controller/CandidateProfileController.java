package cl.duoc.worksi.controller;

import cl.duoc.worksi.dto.candidate.CandidateProfilePatchRequest;
import cl.duoc.worksi.security.UserPrincipal;
import cl.duoc.worksi.service.CandidateProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/candidate")
public class CandidateProfileController {
  private final CandidateProfileService candidateProfileService;

  public CandidateProfileController(CandidateProfileService candidateProfileService) {
    this.candidateProfileService = candidateProfileService;
  }

  @GetMapping("/profile")
  public ResponseEntity<?> getProfile(@AuthenticationPrincipal UserPrincipal principal) {
    return candidateProfileService.getProfile(principal.getUser().getId());
  }

  @PatchMapping("/profile")
  public ResponseEntity<?> patchProfile(
      @AuthenticationPrincipal UserPrincipal principal,
      @RequestBody CandidateProfilePatchRequest body) {
    return candidateProfileService.patchProfile(principal.getUser().getId(), body);
  }
}
