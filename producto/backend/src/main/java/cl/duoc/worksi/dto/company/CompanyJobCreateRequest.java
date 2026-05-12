package cl.duoc.worksi.dto.company;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public class CompanyJobCreateRequest {
  @JsonProperty("company_commercial_name")
  @NotBlank
  private String companyCommercialName;

  @NotBlank private String title;

  @NotBlank private String description;

  @NotBlank private String city;

  @JsonProperty("region_id")
  @NotNull
  private Long regionId;

  @JsonProperty("commune_id")
  @NotNull
  private Long communeId;

  @JsonProperty("salary_offered")
  @NotNull
  private Integer salaryOffered;

  @JsonProperty("years_experience_required")
  @NotNull
  private Integer yearsExperienceRequired;

  @NotNull private String modality;

  @NotNull private String workload;

  @JsonProperty("skills_ids")
  @NotEmpty
  @Size(min = 3, max = 8)
  private List<Long> skillsIds;

  @JsonProperty("image_url")
  private String imageUrl;

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

  public Long getRegionId() {
    return regionId;
  }

  public Long getCommuneId() {
    return communeId;
  }

  public Integer getSalaryOffered() {
    return salaryOffered;
  }

  public Integer getYearsExperienceRequired() {
    return yearsExperienceRequired;
  }

  public String getModality() {
    return modality;
  }

  public String getWorkload() {
    return workload;
  }

  public List<Long> getSkillsIds() {
    return skillsIds;
  }

  public String getImageUrl() {
    return imageUrl;
  }
}
