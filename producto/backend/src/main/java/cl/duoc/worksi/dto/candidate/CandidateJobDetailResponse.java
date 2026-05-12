package cl.duoc.worksi.dto.candidate;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class CandidateJobDetailResponse {
  @JsonProperty("job_id")
  private final long jobId;

  private final String title;

  @JsonProperty("company_name")
  private final String companyName;

  @JsonProperty("salary_offered")
  private final int salaryOffered;

  @JsonProperty("commune_name")
  private final String communeName;

  private final String modality;

  @JsonProperty("years_experience_required")
  private final int yearsExperienceRequired;

  private final String description;
  private final String workload;
  private final List<CandidateJobSkillPreviewResponse> skills;

  private final CandidateJobDetailMatchResponse match;

  public CandidateJobDetailResponse(
      long jobId,
      String title,
      String companyName,
      int salaryOffered,
      String communeName,
      String modality,
      int yearsExperienceRequired,
      String description,
      String workload,
      List<CandidateJobSkillPreviewResponse> skills,
      CandidateJobDetailMatchResponse match) {
    this.jobId = jobId;
    this.title = title;
    this.companyName = companyName;
    this.salaryOffered = salaryOffered;
    this.communeName = communeName;
    this.modality = modality;
    this.yearsExperienceRequired = yearsExperienceRequired;
    this.description = description;
    this.workload = workload;
    this.skills = skills;
    this.match = match;
  }

  public long getJobId() {
    return jobId;
  }

  public String getTitle() {
    return title;
  }

  public String getCompanyName() {
    return companyName;
  }

  public int getSalaryOffered() {
    return salaryOffered;
  }

  public String getCommuneName() {
    return communeName;
  }

  public String getModality() {
    return modality;
  }

  public int getYearsExperienceRequired() {
    return yearsExperienceRequired;
  }

  public String getDescription() {
    return description;
  }

  public String getWorkload() {
    return workload;
  }

  public List<CandidateJobSkillPreviewResponse> getSkills() {
    return skills;
  }

  public CandidateJobDetailMatchResponse getMatch() {
    return match;
  }
}
