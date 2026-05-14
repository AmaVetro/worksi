package cl.duoc.worksi.controller;

import cl.duoc.worksi.dto.PageResponse;
import cl.duoc.worksi.dto.candidate.CandidateJobFeedItemResponse;
import cl.duoc.worksi.dto.candidate.CandidateSwipeRequest;
import cl.duoc.worksi.security.UserPrincipal;
import cl.duoc.worksi.service.CandidateJobFeedService;
import cl.duoc.worksi.service.CandidateSwipeService;
import cl.duoc.worksi.service.CompanyJobService;
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
public class CandidateJobsController {
  private final CandidateJobFeedService candidateJobFeedService;
  private final CandidateSwipeService candidateSwipeService;
  private final CompanyJobService companyJobService;

  public CandidateJobsController(
      CandidateJobFeedService candidateJobFeedService,
      CandidateSwipeService candidateSwipeService,
      CompanyJobService companyJobService) {
    this.candidateJobFeedService = candidateJobFeedService;
    this.candidateSwipeService = candidateSwipeService;
    this.companyJobService = companyJobService;
  }

  @GetMapping("/jobs/feed")
  public ResponseEntity<PageResponse<CandidateJobFeedItemResponse>> feed(
      @AuthenticationPrincipal UserPrincipal principal,
      @RequestParam(name = "page", defaultValue = "1") int page,
      @RequestParam(name = "size", defaultValue = "20") int size) {
    return candidateJobFeedService.feed(principal.getUser().getId(), page, size);
  }

  @GetMapping("/jobs/{job_id}")
  public ResponseEntity<?> jobDetail(
      @AuthenticationPrincipal UserPrincipal principal, @PathVariable("job_id") long jobId) {
    return candidateJobFeedService.detail(principal.getUser().getId(), jobId);
  }

  @GetMapping("/jobs/{job_id}/image")
  public ResponseEntity<?> jobImage(@PathVariable("job_id") long jobId) {
    return companyJobService.getActiveJobImageForCandidate(jobId);
  }

  @PostMapping("/swipes")
  public ResponseEntity<Void> swipe(
      @AuthenticationPrincipal UserPrincipal principal, @RequestBody CandidateSwipeRequest body) {
    candidateSwipeService.record(principal.getUser().getId(), body);
    return ResponseEntity.noContent().build();
  }
}
