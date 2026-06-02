package cl.duoc.worksi.dto.company;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

public class CompanyJobListItemResponse {
  private final long id;
  private final String title;

  @JsonProperty("company_commercial_name")
  private final String companyCommercialName;

  @JsonProperty("salary_offered")
  private final int salaryOffered;

  private final String modality;
  private final String status;

  @JsonProperty("published_at")
  private final Instant publishedAt;

  @JsonProperty("created_at")
  private final Instant createdAt;

  @JsonProperty("applications_count")
  private final long applicationsCount;

  public CompanyJobListItemResponse(
      long id,
      String title,
      String companyCommercialName,
      int salaryOffered,
      String modality,
      String status,
      Instant publishedAt,
      Instant createdAt,
      long applicationsCount) {
    this.id = id;
    this.title = title;
    this.companyCommercialName = companyCommercialName;
    this.salaryOffered = salaryOffered;
    this.modality = modality;
    this.status = status;
    this.publishedAt = publishedAt;
    this.createdAt = createdAt;
    this.applicationsCount = applicationsCount;
  }

  public long getId() {
    return id;
  }

  public String getTitle() {
    return title;
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

  public String getStatus() {
    return status;
  }

  public Instant getPublishedAt() {
    return publishedAt;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public long getApplicationsCount() {
    return applicationsCount;
  }
}
