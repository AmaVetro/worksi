package cl.duoc.worksi.dto.company;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.List;

public class CompanyJobDetailResponse {
  private final long id;

  @JsonProperty("company_commercial_name")
  private final String companyCommercialName;

  private final String title;
  private final String description;
  private final String city;

  @JsonProperty("region_id")
  private final long regionId;

  @JsonProperty("commune_id")
  private final long communeId;

  @JsonProperty("salary_offered")
  private final int salaryOffered;

  @JsonProperty("years_experience_required")
  private final int yearsExperienceRequired;

  private final String modality;
  private final String workload;

  @JsonProperty("image_url")
  private final String imageUrl;

  private final String status;

  @JsonProperty("published_at")
  private final Instant publishedAt;

  @JsonProperty("skills_ids")
  private final List<Long> skillsIds;

  public CompanyJobDetailResponse(
      long id,
      String companyCommercialName,
      String title,
      String description,
      String city,
      long regionId,
      long communeId,
      int salaryOffered,
      int yearsExperienceRequired,
      String modality,
      String workload,
      String imageUrl,
      String status,
      Instant publishedAt,
      List<Long> skillsIds) {
    this.id = id;
    this.companyCommercialName = companyCommercialName;
    this.title = title;
    this.description = description;
    this.city = city;
    this.regionId = regionId;
    this.communeId = communeId;
    this.salaryOffered = salaryOffered;
    this.yearsExperienceRequired = yearsExperienceRequired;
    this.modality = modality;
    this.workload = workload;
    this.imageUrl = imageUrl;
    this.status = status;
    this.publishedAt = publishedAt;
    this.skillsIds = skillsIds;
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

  public String getCity() {
    return city;
  }

  public long getRegionId() {
    return regionId;
  }

  public long getCommuneId() {
    return communeId;
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

  public String getStatus() {
    return status;
  }

  public Instant getPublishedAt() {
    return publishedAt;
  }

  public List<Long> getSkillsIds() {
    return skillsIds;
  }
}
