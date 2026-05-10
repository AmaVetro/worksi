package cl.duoc.worksi.dto.candidate;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class CandidateProfileResponse {
  @JsonProperty("user_id")
  private final long userId;

  @JsonProperty("first_name")
  private final String firstName;

  @JsonProperty("middle_name")
  private final String middleName;

  @JsonProperty("last_name_paternal")
  private final String lastNamePaternal;

  @JsonProperty("last_name_maternal")
  private final String lastNameMaternal;

  @JsonProperty("phone")
  private final String phone;

  @JsonProperty("email")
  private final String email;

  @JsonProperty("region_id")
  private final long regionId;

  @JsonProperty("commune_id")
  private final long communeId;

  @JsonProperty("sector_id")
  private final Long sectorId;

  @JsonProperty("profile_summary")
  private final String profileSummary;

  @JsonProperty("salary_expected_min")
  private final Integer salaryExpectedMin;

  @JsonProperty("salary_expected_max")
  private final Integer salaryExpectedMax;

  @JsonProperty("preferred_modalities")
  private final List<String> preferredModalities;

  @JsonProperty("preferred_workloads")
  private final List<String> preferredWorkloads;

  @JsonProperty("skills")
  private final List<CandidateProfileSkillResponse> skills;

  @JsonProperty("consent_given")
  private final boolean consentGiven;

  @JsonProperty("consent_given_at")
  private final String consentGivenAt;

  public CandidateProfileResponse(
      long userId,
      String firstName,
      String middleName,
      String lastNamePaternal,
      String lastNameMaternal,
      String phone,
      String email,
      long regionId,
      long communeId,
      Long sectorId,
      String profileSummary,
      Integer salaryExpectedMin,
      Integer salaryExpectedMax,
      List<String> preferredModalities,
      List<String> preferredWorkloads,
      List<CandidateProfileSkillResponse> skills,
      boolean consentGiven,
      String consentGivenAt) {
    this.userId = userId;
    this.firstName = firstName;
    this.middleName = middleName;
    this.lastNamePaternal = lastNamePaternal;
    this.lastNameMaternal = lastNameMaternal;
    this.phone = phone;
    this.email = email;
    this.regionId = regionId;
    this.communeId = communeId;
    this.sectorId = sectorId;
    this.profileSummary = profileSummary;
    this.salaryExpectedMin = salaryExpectedMin;
    this.salaryExpectedMax = salaryExpectedMax;
    this.preferredModalities = preferredModalities;
    this.preferredWorkloads = preferredWorkloads;
    this.skills = skills;
    this.consentGiven = consentGiven;
    this.consentGivenAt = consentGivenAt;
  }

  public long getUserId() {
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

  public String getEmail() {
    return email;
  }

  public long getRegionId() {
    return regionId;
  }

  public long getCommuneId() {
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

  public List<String> getPreferredModalities() {
    return preferredModalities;
  }

  public List<String> getPreferredWorkloads() {
    return preferredWorkloads;
  }

  public List<CandidateProfileSkillResponse> getSkills() {
    return skills;
  }

  public boolean isConsentGiven() {
    return consentGiven;
  }

  public String getConsentGivenAt() {
    return consentGivenAt;
  }
}
