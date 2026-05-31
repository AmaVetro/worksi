package cl.duoc.worksi.controller;

import cl.duoc.worksi.dto.company.CompanyProfileImagePatchRequest;
import cl.duoc.worksi.security.UserPrincipal;
import cl.duoc.worksi.service.CompanyJobService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/company")
public class CompanyController {
  private final CompanyJobService companyJobService;

  public CompanyController(CompanyJobService companyJobService) {
    this.companyJobService = companyJobService;
  }

  @GetMapping("/profile")
  public ResponseEntity<?> getProfile(@AuthenticationPrincipal UserPrincipal principal) {
    return companyJobService.getRecruiterCompanyProfile(principal.getUser().getId());
  }

  @GetMapping("/profile/image")
  public ResponseEntity<?> getProfileImage(@AuthenticationPrincipal UserPrincipal principal) {
    return companyJobService.getRecruiterCompanyProfileImage(principal.getUser().getId());
  }

  @PatchMapping("/profile/image")
  public ResponseEntity<?> patchProfileImage(
      @AuthenticationPrincipal UserPrincipal principal,
      @Valid @RequestBody CompanyProfileImagePatchRequest body) {
    return companyJobService.patchCompanyImage(principal.getUser().getId(), body);
  }

  @PostMapping(value = "/jobs", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?> createJob(
      @AuthenticationPrincipal UserPrincipal principal,
      @RequestPart("data") String data,
      @RequestPart(value = "image", required = false) MultipartFile image) {
    return companyJobService.createJob(principal.getUser().getId(), data, image);
  }

  @GetMapping("/jobs")
  public ResponseEntity<?> listJobs(
      @AuthenticationPrincipal UserPrincipal principal,
      @RequestParam(name = "page", defaultValue = "1") int page,
      @RequestParam(name = "size", defaultValue = "20") int size,
      @RequestParam(name = "sort", defaultValue = "created_at,desc") String sort) {
    return companyJobService.listMyJobs(principal.getUser().getId(), page, size, sort);
  }

  @GetMapping("/jobs/{job_id}/image")
  public ResponseEntity<?> getJobImage(
      @AuthenticationPrincipal UserPrincipal principal, @PathVariable("job_id") long jobId) {
    return companyJobService.getMyJobImage(principal.getUser().getId(), jobId);
  }

  @GetMapping("/jobs/{job_id}")
  public ResponseEntity<?> getJob(
      @AuthenticationPrincipal UserPrincipal principal, @PathVariable("job_id") long jobId) {
    return companyJobService.getMyJob(principal.getUser().getId(), jobId);
  }

  @PatchMapping(value = "/jobs/{job_id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?> updateJob(
      @AuthenticationPrincipal UserPrincipal principal,
      @PathVariable("job_id") long jobId,
      @RequestPart("data") String data,
      @RequestPart(value = "image", required = false) MultipartFile image) {
    return companyJobService.updateJob(principal.getUser().getId(), jobId, data, image);
  }
}
