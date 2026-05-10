package cl.duoc.worksi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "candidate_profiles")
public class CandidateProfile {
  @Id
  @Column(name = "user_id")
  private Long userId;

  @Column(name = "first_name", nullable = false, length = 100)
  private String firstName;

  @Column(name = "middle_name", length = 100)
  private String middleName;

  @Column(name = "last_name_paternal", nullable = false, length = 100)
  private String lastNamePaternal;

  @Column(name = "last_name_maternal", nullable = false, length = 100)
  private String lastNameMaternal;

  @Column(nullable = false, length = 25)
  private String phone;

  @Column(nullable = false, length = 15)
  private String rut;

  @Column(name = "document_number", nullable = false, length = 40)
  private String documentNumber;

  @Column(length = 160)
  private String street;

  @Column(name = "region_id", nullable = false)
  private Long regionId;

  @Column(name = "commune_id", nullable = false)
  private Long communeId;

  @Column(name = "sector_id")
  private Long sectorId;

  @Column(name = "profile_summary", columnDefinition = "TEXT")
  private String profileSummary;

  @Column(name = "salary_expected_min")
  private Integer salaryExpectedMin;

  @Column(name = "salary_expected_max")
  private Integer salaryExpectedMax;

  @Column(name = "consent_given", nullable = false)
  private boolean consentGiven;

  @Column(name = "consent_given_at")
  private LocalDateTime consentGivenAt;

  @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
  private LocalDateTime updatedAt;

  public CandidateProfile() {}

  public Long getUserId() {
    return userId;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getMiddleName() {
    return middleName;
  }

  public String getLastNamePaternal() {
    return lastNamePaternal;
  }

  public String getLastNameMaternal() {
    return lastNameMaternal;
  }

  public String getPhone() {
    return phone;
  }

  public String getRut() {
    return rut;
  }

  public String getDocumentNumber() {
    return documentNumber;
  }

  public String getStreet() {
    return street;
  }

  public Long getRegionId() {
    return regionId;
  }

  public Long getCommuneId() {
    return communeId;
  }

  public Long getSectorId() {
    return sectorId;
  }

  public String getProfileSummary() {
    return profileSummary;
  }

  public Integer getSalaryExpectedMin() {
    return salaryExpectedMin;
  }

  public Integer getSalaryExpectedMax() {
    return salaryExpectedMax;
  }

  public boolean isConsentGiven() {
    return consentGiven;
  }

  public LocalDateTime getConsentGivenAt() {
    return consentGivenAt;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public void setMiddleName(String middleName) {
    this.middleName = middleName;
  }

  public void setLastNamePaternal(String lastNamePaternal) {
    this.lastNamePaternal = lastNamePaternal;
  }

  public void setLastNameMaternal(String lastNameMaternal) {
    this.lastNameMaternal = lastNameMaternal;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public void setRut(String rut) {
    this.rut = rut;
  }

  public void setDocumentNumber(String documentNumber) {
    this.documentNumber = documentNumber;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public void setRegionId(Long regionId) {
    this.regionId = regionId;
  }

  public void setCommuneId(Long communeId) {
    this.communeId = communeId;
  }

  public void setSectorId(Long sectorId) {
    this.sectorId = sectorId;
  }

  public void setProfileSummary(String profileSummary) {
    this.profileSummary = profileSummary;
  }

  public void setSalaryExpectedMin(Integer salaryExpectedMin) {
    this.salaryExpectedMin = salaryExpectedMin;
  }

  public void setSalaryExpectedMax(Integer salaryExpectedMax) {
    this.salaryExpectedMax = salaryExpectedMax;
  }

  public void setConsentGiven(boolean consentGiven) {
    this.consentGiven = consentGiven;
  }

  public void setConsentGivenAt(LocalDateTime consentGivenAt) {
    this.consentGivenAt = consentGivenAt;
  }
}
