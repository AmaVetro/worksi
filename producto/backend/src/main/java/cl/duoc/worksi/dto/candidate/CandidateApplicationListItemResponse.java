package cl.duoc.worksi.dto.candidate;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

public class CandidateApplicationListItemResponse {
  @JsonProperty("application_id")
  private final long applicationId;

  @JsonProperty("job_id")
  private final long jobId;

  @JsonProperty("job_title")
  private final String jobTitle;

  @JsonProperty("company_commercial_name")
  private final String companyCommercialName;

  @JsonProperty("salary_offered")
  private final int salaryOffered;

  private final String status;

  @JsonProperty("applied_at")
  private final Instant appliedAt;

  @JsonProperty("match_score")
  private final Double matchScore;

  public CandidateApplicationListItemResponse(
      long applicationId,
      long jobId,
      String jobTitle,
      String companyCommercialName,
      int salaryOffered,
      String status,
      Instant appliedAt,
      Double matchScore) {
    this.applicationId = applicationId;
    this.jobId = jobId;
    this.jobTitle = jobTitle;
    this.companyCommercialName = companyCommercialName;
    this.salaryOffered = salaryOffered;
    this.status = status;
    this.appliedAt = appliedAt;
    this.matchScore = matchScore;
  }

  public long getApplicationId() {
    return applicationId;
  }

  public long getJobId() {
    return jobId;
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

  public String getStatus() {
    return status;
  }

  public Instant getAppliedAt() {
    return appliedAt;
  }

  public Double getMatchScore() {
    return matchScore;
  }
}
