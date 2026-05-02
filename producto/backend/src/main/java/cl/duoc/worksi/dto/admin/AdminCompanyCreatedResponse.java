package cl.duoc.worksi.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AdminCompanyCreatedResponse {
  @JsonProperty("company_id")
  private final long companyId;

  public AdminCompanyCreatedResponse(long companyId) {
    this.companyId = companyId;
  }

  public long getCompanyId() {
    return companyId;
  }
}
