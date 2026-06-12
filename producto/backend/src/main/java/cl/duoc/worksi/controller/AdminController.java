package cl.duoc.worksi.controller;

import cl.duoc.worksi.dto.PageResponse;
import cl.duoc.worksi.dto.admin.AdminCompanyListItem;
import cl.duoc.worksi.dto.admin.AdminJobsStatsResponse;
import cl.duoc.worksi.dto.admin.AdminRecruiterListItem;
import cl.duoc.worksi.dto.admin.AdminRecruiterRequest;
import cl.duoc.worksi.dto.admin.AdminRecruiterUpdateRequest;
import cl.duoc.worksi.dto.admin.AdminSystemStatusResponse;
import cl.duoc.worksi.dto.company.CompanyJobStatusPatchRequest;
import cl.duoc.worksi.service.AdminCompanyService;
import cl.duoc.worksi.service.AdminJobsStatsService;
import cl.duoc.worksi.service.AdminRecruiterService;
import cl.duoc.worksi.service.AdminSystemStatusService;
import cl.duoc.worksi.service.CompanyJobService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/api/v1/admin")
public class AdminController {
  private final AdminCompanyService adminCompanyService;
  private final AdminRecruiterService adminRecruiterService;
  private final AdminJobsStatsService adminJobsStatsService;
  private final AdminSystemStatusService adminSystemStatusService;
  private final CompanyJobService companyJobService;

  public AdminController(
      AdminCompanyService adminCompanyService,
      AdminRecruiterService adminRecruiterService,
      AdminJobsStatsService adminJobsStatsService,
      AdminSystemStatusService adminSystemStatusService,
      CompanyJobService companyJobService) {
    this.adminCompanyService = adminCompanyService;
    this.adminRecruiterService = adminRecruiterService;
    this.adminJobsStatsService = adminJobsStatsService;
    this.adminSystemStatusService = adminSystemStatusService;
    this.companyJobService = companyJobService;
  }

  @PostMapping(value = "/companies", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?> createCompany(
      @RequestPart("data") String data,
      @RequestPart(value = "image", required = false) MultipartFile image) {
    return adminCompanyService.createCompany(data, image);
  }

  @GetMapping("/companies/{company_id}")
  public ResponseEntity<?> getCompany(@PathVariable("company_id") long companyId) {
    return adminCompanyService.getCompany(companyId);
  }

  @PatchMapping(value = "/companies/{company_id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?> updateCompany(
      @PathVariable("company_id") long companyId,
      @RequestPart("data") String data,
      @RequestPart(value = "image", required = false) MultipartFile image) {
    return adminCompanyService.updateCompany(companyId, data, image);
  }

  @DeleteMapping("/companies/{company_id}")
  public ResponseEntity<?> deleteCompany(@PathVariable("company_id") long companyId) {
    return adminCompanyService.deleteCompany(companyId);
  }

  @GetMapping("/companies")
  public ResponseEntity<PageResponse<AdminCompanyListItem>> listCompanies(
      @RequestParam(name = "page", defaultValue = "1") int page,
      @RequestParam(name = "size", defaultValue = "20") int size,
      @RequestParam(name = "sort", defaultValue = "created_at,desc") String sort) {
    return adminCompanyService.listCompanies(page, size, sort);
  }

  @PostMapping(value = "/recruiters", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> createRecruiter(@RequestBody AdminRecruiterRequest body) {
    return adminRecruiterService.createRecruiter(body);
  }

  @GetMapping("/recruiters/{recruiter_user_id}")
  public ResponseEntity<?> getRecruiter(
      @PathVariable("recruiter_user_id") long recruiterUserId) {
    return adminRecruiterService.getRecruiter(recruiterUserId);
  }

  @PatchMapping(value = "/recruiters/{recruiter_user_id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> updateRecruiter(
      @PathVariable("recruiter_user_id") long recruiterUserId,
      @RequestBody AdminRecruiterUpdateRequest body) {
    return adminRecruiterService.updateRecruiter(recruiterUserId, body);
  }

  @DeleteMapping("/recruiters/{recruiter_user_id}")
  public ResponseEntity<?> deleteRecruiter(@PathVariable("recruiter_user_id") long recruiterUserId) {
    return adminRecruiterService.deleteRecruiter(recruiterUserId);
  }

  @GetMapping("/recruiters")
  public ResponseEntity<PageResponse<AdminRecruiterListItem>> listRecruiters(
      @RequestParam(name = "page", defaultValue = "1") int page,
      @RequestParam(name = "size", defaultValue = "20") int size,
      @RequestParam(name = "sort", defaultValue = "created_at,desc") String sort) {
    return adminRecruiterService.listRecruiters(page, size, sort);
  }

  @GetMapping("/jobs/stats")
  public ResponseEntity<AdminJobsStatsResponse> jobsStats() {
    return ResponseEntity.ok(adminJobsStatsService.activeJobsTotal());
  }

  @GetMapping("/jobs")
  public ResponseEntity<?> listJobs(
      @RequestParam(name = "page", defaultValue = "1") int page,
      @RequestParam(name = "size", defaultValue = "20") int size,
      @RequestParam(name = "sort", defaultValue = "created_at,desc") String sort,
      @RequestParam(name = "status", defaultValue = "ACTIVE") String status,
      @RequestParam(name = "company_name", defaultValue = "") String companyName,
      @RequestParam(name = "title", defaultValue = "") String title) {
    return companyJobService.listAllJobs(page, size, sort, status, companyName, title);
  }

  @GetMapping("/jobs/{job_id}/image")
  public ResponseEntity<?> getJobImage(@PathVariable("job_id") long jobId) {
    return companyJobService.getJobImageAsAdmin(jobId);
  }

  @GetMapping("/jobs/{job_id}")
  public ResponseEntity<?> getJob(@PathVariable("job_id") long jobId) {
    return companyJobService.getJobAsAdmin(jobId);
  }

  @PatchMapping(value = "/jobs/{job_id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?> updateJob(
      @PathVariable("job_id") long jobId,
      @RequestPart("data") String data,
      @RequestPart(value = "image", required = false) MultipartFile image) {
    return companyJobService.updateJobAsAdmin(jobId, data, image);
  }

  @PatchMapping("/jobs/{job_id}/status")
  public ResponseEntity<?> patchJobStatus(
      @PathVariable("job_id") long jobId, @Valid @RequestBody CompanyJobStatusPatchRequest body) {
    return companyJobService.patchJobStatusAsAdmin(jobId, body);
  }

  @DeleteMapping("/jobs/{job_id}")
  public ResponseEntity<?> deleteJob(@PathVariable("job_id") long jobId) {
    return companyJobService.deleteJobAsAdmin(jobId);
  }

  @GetMapping("/system/status")
  public ResponseEntity<AdminSystemStatusResponse> systemStatus() {
    return ResponseEntity.ok(adminSystemStatusService.systemStatus());
  }
}
