package cl.duoc.worksi.dto.candidate;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

public class CandidateApplicationDetailResponse {
  @JsonProperty("application_id")
  private final long applicationId;

  @JsonProperty("job_id")
  private final long jobId;

  private final String status;

  @JsonProperty("applied_at")
  private final Instant appliedAt;

  @JsonProperty("viewed_at")
  private final Instant viewedAt;

  @JsonProperty("job_title")
  private final String jobTitle;

  @JsonProperty("company_commercial_name")
  private final String companyCommercialName;

  @JsonProperty("salary_offered")
  private final int salaryOffered;

  private final String modality;

  @JsonProperty("years_experience_required")
  private final int yearsExperienceRequired;

  @JsonProperty("commune_name")
  private final String communeName;

  private final String description;

  @JsonProperty("match_score")
  private final Double matchScore;

  public CandidateApplicationDetailResponse(
      long applicationId,
      long jobId,
      String status,
      Instant appliedAt,
      Instant viewedAt,
      String jobTitle,
      String companyCommercialName,
      int salaryOffered,
      String modality,
      int yearsExperienceRequired,
      String communeName,
      String description,
      Double matchScore) {
    this.applicationId = applicationId;
    this.jobId = jobId;
    this.status = status;
    this.appliedAt = appliedAt;
    this.viewedAt = viewedAt;
    this.jobTitle = jobTitle;
    this.companyCommercialName = companyCommercialName;
    this.salaryOffered = salaryOffered;
    this.modality = modality;
    this.yearsExperienceRequired = yearsExperienceRequired;
    this.communeName = communeName;
    this.description = description;
    this.matchScore = matchScore;
  }

  public long getApplicationId() {
    return applicationId;
  }

  public long getJobId() {
    return jobId;
  }

  public String getStatus() {
    return status;
  }

  public Instant getAppliedAt() {
    return appliedAt;
  }

  public Instant getViewedAt() {
    return viewedAt;
  }

  public String getJobTitle() {
    return jobTitle;
  }

  public String getCompanyCommercialName() {
    return companyCommercialName;
  }

  public int getSalaryOffered() {
    return salaryOffered;
  }

  public String getModality() {
    return modality;
  }

  public int getYearsExperienceRequired() {
    return yearsExperienceRequired;
  }

  public String getCommuneName() {
    return communeName;
  }

  public String getDescription() {
    return description;
  }

  public Double getMatchScore() {
    return matchScore;
  }
}
