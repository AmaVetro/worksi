package cl.duoc.worksi.controller;

import cl.duoc.worksi.security.UserPrincipal;
import cl.duoc.worksi.service.CandidateCvService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/candidate/cv")
public class CandidateCvController {
  private final CandidateCvService candidateCvService;

  public CandidateCvController(CandidateCvService candidateCvService) {
    this.candidateCvService = candidateCvService;
  }

  @GetMapping("/current")
  public ResponseEntity<?> getCurrent(@AuthenticationPrincipal UserPrincipal principal) {
    return candidateCvService.getCurrentMetadata(principal.getUser().getId());
  }

  @GetMapping("/current/file")
  public ResponseEntity<?> getCurrentFile(@AuthenticationPrincipal UserPrincipal principal) {
    return candidateCvService.getCurrentFile(principal.getUser().getId());
  }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?> uploadCv(
      @AuthenticationPrincipal UserPrincipal principal,
      @RequestPart("file") MultipartFile file) {
    return candidateCvService.uploadCv(principal.getUser().getId(), file);
  }
}
