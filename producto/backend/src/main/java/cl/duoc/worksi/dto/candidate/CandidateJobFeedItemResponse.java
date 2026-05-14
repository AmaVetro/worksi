package cl.duoc.worksi.dto.candidate;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class CandidateJobFeedItemResponse {
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

  @JsonProperty("description_preview")
  private final String descriptionPreview;

  @JsonProperty("skills_preview")
  private final List<CandidateJobSkillPreviewResponse> skillsPreview;

  @JsonProperty("external_image_url")
  private final String externalImageUrl;

  private final boolean hasProtectedJobImage;

  private final CandidateJobMatchResponse match;

  public CandidateJobFeedItemResponse(
      long jobId,
      String title,
      String companyName,
      int salaryOffered,
      String communeName,
      String modality,
      int yearsExperienceRequired,
      String descriptionPreview,
      List<CandidateJobSkillPreviewResponse> skillsPreview,
      String externalImageUrl,
      boolean hasProtectedJobImage,
      CandidateJobMatchResponse match) {
    this.jobId = jobId;
    this.title = title;
    this.companyName = companyName;
    this.salaryOffered = salaryOffered;
    this.communeName = communeName;
    this.modality = modality;
    this.yearsExperienceRequired = yearsExperienceRequired;
    this.descriptionPreview = descriptionPreview;
    this.skillsPreview = skillsPreview;
    this.externalImageUrl = externalImageUrl;
    this.hasProtectedJobImage = hasProtectedJobImage;
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

  public String getDescriptionPreview() {
    return descriptionPreview;
  }

  public List<CandidateJobSkillPreviewResponse> getSkillsPreview() {
    return skillsPreview;
  }

  public String getExternalImageUrl() {
    return externalImageUrl;
  }

  @JsonProperty("has_protected_job_image")
  public boolean getHasProtectedJobImage() {
    return hasProtectedJobImage;
  }

  public CandidateJobMatchResponse getMatch() {
    return match;
  }
}
