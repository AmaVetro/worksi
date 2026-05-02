package cl.duoc.worksi.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AdminRecruiterRequest {
  @JsonProperty("email")
  private String email;

  @JsonProperty("password")
  private String password;

  @JsonProperty("first_name")
  private String firstName;

  @JsonProperty("last_name_paternal")
  private String lastNamePaternal;

  @JsonProperty("last_name_maternal")
  private String lastNameMaternal;

  @JsonProperty("rut")
  private String rut;

  @JsonProperty("phone")
  private String phone;

  @JsonProperty("mobile")
  private String mobile;

  @JsonProperty("birth_date")
  private String birthDate;

  @JsonProperty("company_id")
  private Long companyId;

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastNamePaternal() {
    return lastNamePaternal;
  }

  public void setLastNamePaternal(String lastNamePaternal) {
    this.lastNamePaternal = lastNamePaternal;
  }

  public String getLastNameMaternal() {
    return lastNameMaternal;
  }

  public void setLastNameMaternal(String lastNameMaternal) {
    this.lastNameMaternal = lastNameMaternal;
  }

  public String getRut() {
    return rut;
  }

  public void setRut(String rut) {
    this.rut = rut;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(String birthDate) {
    this.birthDate = birthDate;
  }

  public Long getCompanyId() {
    return companyId;
  }

  public void setCompanyId(Long companyId) {
    this.companyId = companyId;
  }
}
