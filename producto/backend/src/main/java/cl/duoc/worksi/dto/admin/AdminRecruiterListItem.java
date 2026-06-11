package cl.duoc.worksi.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AdminRecruiterListItem {
  @JsonProperty("user_id")
  private final long userId;

  @JsonProperty("email")
  private final String email;

  @JsonProperty("role")
  private final String role;

  @JsonProperty("first_name")
  private final String firstName;

  @JsonProperty("last_name_paternal")
  private final String lastNamePaternal;

  @JsonProperty("last_name_maternal")
  private final String lastNameMaternal;

  @JsonProperty("company_id")
  private final long companyId;

  @JsonProperty("company_commercial_name")
  private final String companyCommercialName;

  public AdminRecruiterListItem(
      long userId,
      String email,
      String role,
      String firstName,
      String lastNamePaternal,
      String lastNameMaternal,
      long companyId,
      String companyCommercialName) {
    this.userId = userId;
    this.email = email;
    this.role = role;
    this.firstName = firstName;
    this.lastNamePaternal = lastNamePaternal;
    this.lastNameMaternal = lastNameMaternal;
    this.companyId = companyId;
    this.companyCommercialName = companyCommercialName;
  }

  public long getUserId() {
    return userId;
  }

  public String getEmail() {
    return email;
  }

  public String getRole() {
    return role;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastNamePaternal() {
    return lastNamePaternal;
  }

  public String getLastNameMaternal() {
    return lastNameMaternal;
  }

  public long getCompanyId() {
    return companyId;
  }

  public String getCompanyCommercialName() {
    return companyCommercialName;
  }
}
