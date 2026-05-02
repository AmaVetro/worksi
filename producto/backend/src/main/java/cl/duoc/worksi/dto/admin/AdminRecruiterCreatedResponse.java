package cl.duoc.worksi.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AdminRecruiterCreatedResponse {
  @JsonProperty("user_id")
  private final long userId;

  @JsonProperty("role")
  private final String role;

  @JsonProperty("email")
  private final String email;

  @JsonProperty("company_id")
  private final long companyId;

  public AdminRecruiterCreatedResponse(
      long userId, String role, String email, long companyId) {
    this.userId = userId;
    this.role = role;
    this.email = email;
    this.companyId = companyId;
  }

  public long getUserId() {
    return userId;
  }

  public String getRole() {
    return role;
  }

  public String getEmail() {
    return email;
  }

  public long getCompanyId() {
    return companyId;
  }
}
