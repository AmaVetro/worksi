package cl.duoc.worksi.controller;

import cl.duoc.worksi.dto.PageResponse;
import cl.duoc.worksi.dto.admin.AdminCompanyListItem;
import cl.duoc.worksi.dto.admin.AdminRecruiterRequest;
import cl.duoc.worksi.service.AdminCompanyService;
import cl.duoc.worksi.service.AdminRecruiterService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

  public AdminController(
      AdminCompanyService adminCompanyService, AdminRecruiterService adminRecruiterService) {
    this.adminCompanyService = adminCompanyService;
    this.adminRecruiterService = adminRecruiterService;
  }

  @PostMapping(value = "/companies", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?> createCompany(
      @RequestPart("data") String data,
      @RequestPart(value = "image", required = false) MultipartFile image) {
    return adminCompanyService.createCompany(data, image);
  }

  @GetMapping("/companies")
  public ResponseEntity<PageResponse<AdminCompanyListItem>> listCompanies(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "created_at,desc") String sort) {
    return adminCompanyService.listCompanies(page, size, sort);
  }

  @PostMapping(value = "/recruiters", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> createRecruiter(@RequestBody AdminRecruiterRequest body) {
    return adminRecruiterService.createRecruiter(body);
  }
}
