package cl.duoc.worksi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CandidateRegistrationValidateRequest {
  @JsonProperty("email")
  private String email;

  @JsonProperty("password")
  private String password;

  @JsonProperty("first_name")
  private String firstName;

  @JsonProperty("middle_name")
  private String middleName;

  @JsonProperty("last_name_paternal")
  private String lastNamePaternal;

  @JsonProperty("last_name_maternal")
  private String lastNameMaternal;

  @JsonProperty("phone")
  private String phone;

  @JsonProperty("rut")
  private String rut;

  @JsonProperty("document_number")
  private String documentNumber;

  @JsonProperty("street")
  private String street;

  @JsonProperty("region_id")
  private Long regionId;

  @JsonProperty("commune_id")
  private Long communeId;

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

  public String getMiddleName() {
    return middleName;
  }

  public void setMiddleName(String middleName) {
    this.middleName = middleName;
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

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getRut() {
    return rut;
  }

  public void setRut(String rut) {
    this.rut = rut;
  }

  public String getDocumentNumber() {
    return documentNumber;
  }

  public void setDocumentNumber(String documentNumber) {
    this.documentNumber = documentNumber;
  }

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public Long getRegionId() {
    return regionId;
  }

  public void setRegionId(Long regionId) {
    this.regionId = regionId;
  }

  public Long getCommuneId() {
    return communeId;
  }

  public void setCommuneId(Long communeId) {
    this.communeId = communeId;
  }
}
