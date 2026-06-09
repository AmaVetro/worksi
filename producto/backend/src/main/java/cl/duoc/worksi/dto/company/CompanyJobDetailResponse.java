package cl.duoc.worksi.dto.company;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public class CompanyJobDetailResponse {
  private final long id;

  @JsonProperty("company_commercial_name")
  private final String companyCommercialName;

  private final String title;
  private final String description;

  @JsonProperty("region_id")
  private final long regionId;

  @JsonProperty("commune_id")
  private final long communeId;

  @JsonProperty("region_name")
  private final String regionName;

  @JsonProperty("commune_name")
  private final String communeName;

  @JsonProperty("salary_offered")
  private final int salaryOffered;

  @JsonProperty("years_experience_required")
  private final int yearsExperienceRequired;

  private final String modality;
  private final String workload;

  @JsonProperty("image_url")
  private final String imageUrl;

  @JsonProperty("external_image_url")
  private final String externalImageUrl;

  @JsonProperty("has_protected_image")
  private final boolean hasProtectedImage;

  private final String status;

  @JsonProperty("published_at")
  private final Instant publishedAt;

  @JsonProperty("created_at")
  private final Instant createdAt;

  @JsonProperty("closing_date")
  private final LocalDate closingDate;

  @JsonProperty("skills_ids")
  private final List<Long> skillsIds;

  private final List<CompanyJobSkillItemResponse> skills;

  @JsonProperty("applications_count")
  private final long applicationsCount;

  public CompanyJobDetailResponse(
      long id,
      String companyCommercialName,
      String title,
      String description,
      long regionId,
      long communeId,
      String regionName,
      String communeName,
      int salaryOffered,
      int yearsExperienceRequired,
      String modality,
      String workload,
      String imageUrl,
      String externalImageUrl,
      boolean hasProtectedImage,
      String status,
      Instant publishedAt,
      Instant createdAt,
      LocalDate closingDate,
      List<Long> skillsIds,
      List<CompanyJobSkillItemResponse> skills,
      long applicationsCount) {
    this.id = id;
    this.companyCommercialName = companyCommercialName;
    this.title = title;
    this.description = description;
    this.regionId = regionId;
    this.communeId = communeId;
    this.regionName = regionName;
    this.communeName = communeName;
    this.salaryOffered = salaryOffered;
    this.yearsExperienceRequired = yearsExperienceRequired;
    this.modality = modality;
    this.workload = workload;
    this.imageUrl = imageUrl;
    this.externalImageUrl = externalImageUrl;
    this.hasProtectedImage = hasProtectedImage;
    this.status = status;
    this.publishedAt = publishedAt;
    this.createdAt = createdAt;
    this.closingDate = closingDate;
    this.skillsIds = skillsIds;
    this.skills = skills;
    this.applicationsCount = applicationsCount;
  }

  public long getId() {
    return id;
  }

  public String getCompanyCommercialName() {
    return companyCommercialName;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public long getRegionId() {
    return regionId;
  }

  public long getCommuneId() {
    return communeId;
  }

  public String getRegionName() {
    return regionName;
  }

  public String getCommuneName() {
    return communeName;
  }

  public int getSalaryOffered() {
    return salaryOffered;
  }

  public int getYearsExperienceRequired() {
    return yearsExperienceRequired;
  }

  public String getModality() {
    return modality;
  }

  public String getWorkload() {
    return workload;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public String getExternalImageUrl() {
    return externalImageUrl;
  }

  @JsonProperty("has_protected_image")
  public boolean isHasProtectedImage() {
    return hasProtectedImage;
  }

  public String getStatus() {
    return status;
  }

  public Instant getPublishedAt() {
    return publishedAt;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public LocalDate getClosingDate() {
    return closingDate;
  }

  public List<Long> getSkillsIds() {
    return skillsIds;
  }

  public List<CompanyJobSkillItemResponse> getSkills() {
    return skills;
  }

  public long getApplicationsCount() {
    return applicationsCount;
  }
}
