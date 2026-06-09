package cl.duoc.worksi.dto.company;

import jakarta.validation.constraints.NotBlank;

public class CompanyJobStatusPatchRequest {
  @NotBlank private String status;

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
