package cl.duoc.worksi.dto.company;

import cl.duoc.worksi.dto.candidate.CandidateProfileSkillResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class CompanyCandidateProfileResponse {
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

  @JsonProperty("region_name")
  private final String regionName;

  @JsonProperty("commune_name")
  private final String communeName;

  @JsonProperty("sector_name")
  private final String sectorName;

  @JsonProperty("profile_summary")
  private final String profileSummary;

  @JsonProperty("salary_expected_min")
  private final Integer salaryExpectedMin;

  @JsonProperty("salary_expected_max")
  private final Integer salaryExpectedMax;

  @JsonProperty("years_experience")
  private final int yearsExperience;

  @JsonProperty("preferred_modalities")
  private final List<String> preferredModalities;

  @JsonProperty("preferred_workloads")
  private final List<String> preferredWorkloads;

  @JsonProperty("skills")
  private final List<CandidateProfileSkillResponse> skills;

  public CompanyCandidateProfileResponse(
      long userId,
      String firstName,
      String middleName,
      String lastNamePaternal,
      String lastNameMaternal,
      String phone,
      String email,
      String regionName,
      String communeName,
      String sectorName,
      String profileSummary,
      Integer salaryExpectedMin,
      Integer salaryExpectedMax,
      int yearsExperience,
      List<String> preferredModalities,
      List<String> preferredWorkloads,
      List<CandidateProfileSkillResponse> skills) {
    this.userId = userId;
    this.firstName = firstName;
    this.middleName = middleName;
    this.lastNamePaternal = lastNamePaternal;
    this.lastNameMaternal = lastNameMaternal;
    this.phone = phone;
    this.email = email;
    this.regionName = regionName;
    this.communeName = communeName;
    this.sectorName = sectorName;
    this.profileSummary = profileSummary;
    this.salaryExpectedMin = salaryExpectedMin;
    this.salaryExpectedMax = salaryExpectedMax;
    this.yearsExperience = yearsExperience;
    this.preferredModalities = preferredModalities;
    this.preferredWorkloads = preferredWorkloads;
    this.skills = skills;
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

  public String getRegionName() {
    return regionName;
  }

  public String getCommuneName() {
    return communeName;
  }

  public String getSectorName() {
    return sectorName;
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

  public int getYearsExperience() {
    return yearsExperience;
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
}
