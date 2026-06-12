package cl.duoc.worksi.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AdminRecruiterDetailResponse {
  @JsonProperty("user_id")
  private final long userId;

  @JsonProperty("email")
  private final String email;

  @JsonProperty("first_name")
  private final String firstName;

  @JsonProperty("last_name_paternal")
  private final String lastNamePaternal;

  @JsonProperty("last_name_maternal")
  private final String lastNameMaternal;

  @JsonProperty("rut")
  private final String rut;

  @JsonProperty("phone")
  private final String phone;

  @JsonProperty("mobile")
  private final String mobile;

  @JsonProperty("birth_date")
  private final String birthDate;

  @JsonProperty("company_id")
  private final long companyId;

  public AdminRecruiterDetailResponse(
      long userId,
      String email,
      String firstName,
      String lastNamePaternal,
      String lastNameMaternal,
      String rut,
      String phone,
      String mobile,
      String birthDate,
      long companyId) {
    this.userId = userId;
    this.email = email;
    this.firstName = firstName;
    this.lastNamePaternal = lastNamePaternal;
    this.lastNameMaternal = lastNameMaternal;
    this.rut = rut;
    this.phone = phone;
    this.mobile = mobile;
    this.birthDate = birthDate;
    this.companyId = companyId;
  }

  public long getUserId() {
    return userId;
  }

  public String getEmail() {
    return email;
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

  public String getRut() {
    return rut;
  }

  public String getPhone() {
    return phone;
  }

  public String getMobile() {
    return mobile;
  }

  public String getBirthDate() {
    return birthDate;
  }

  public long getCompanyId() {
    return companyId;
  }
}
