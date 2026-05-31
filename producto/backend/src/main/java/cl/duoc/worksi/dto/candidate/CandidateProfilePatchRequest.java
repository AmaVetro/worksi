package cl.duoc.worksi.dto.candidate;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class CandidateProfilePatchRequest {
  @JsonProperty("profile_summary")
  private String profileSummary;

  @JsonProperty("salary_expected_min")
  private Integer salaryExpectedMin;

  @JsonProperty("salary_expected_max")
  private Integer salaryExpectedMax;

  @JsonProperty("years_experience")
  private Integer yearsExperience;

  @JsonProperty("region_id")
  private Long regionId;

  @JsonProperty("commune_id")
  private Long communeId;

  @JsonProperty("sector_id")
  private Long sectorId;

  @JsonProperty("preferred_modalities")
  private List<String> preferredModalities;

  @JsonProperty("preferred_workloads")
  private List<String> preferredWorkloads;

  @JsonProperty("skills_ids")
  private List<Long> skillsIds;

  public String getProfileSummary() {
    return profileSummary;
  }

  public void setProfileSummary(String profileSummary) {
    this.profileSummary = profileSummary;
  }

  public Integer getSalaryExpectedMin() {
    return salaryExpectedMin;
  }

  public void setSalaryExpectedMin(Integer salaryExpectedMin) {
    this.salaryExpectedMin = salaryExpectedMin;
  }

  public Integer getSalaryExpectedMax() {
    return salaryExpectedMax;
  }

  public void setSalaryExpectedMax(Integer salaryExpectedMax) {
    this.salaryExpectedMax = salaryExpectedMax;
  }

  public Integer getYearsExperience() {
    return yearsExperience;
  }

  public void setYearsExperience(Integer yearsExperience) {
    this.yearsExperience = yearsExperience;
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

  public Long getSectorId() {
    return sectorId;
  }

  public void setSectorId(Long sectorId) {
    this.sectorId = sectorId;
  }

  public List<String> getPreferredModalities() {
    return preferredModalities;
  }

  public void setPreferredModalities(List<String> preferredModalities) {
    this.preferredModalities = preferredModalities;
  }

  public List<String> getPreferredWorkloads() {
    return preferredWorkloads;
  }

  public void setPreferredWorkloads(List<String> preferredWorkloads) {
    this.preferredWorkloads = preferredWorkloads;
  }

  public List<Long> getSkillsIds() {
    return skillsIds;
  }

  public void setSkillsIds(List<Long> skillsIds) {
    this.skillsIds = skillsIds;
  }
}
