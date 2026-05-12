package cl.duoc.worksi.entity;

import cl.duoc.worksi.entity.enums.JobStatus;
import cl.duoc.worksi.entity.enums.Modality;
import cl.duoc.worksi.entity.enums.Workload;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "jobs")
public class Job {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "company_id", nullable = false)
  private Long companyId;

  @Column(name = "published_by_user_id")
  private Long publishedByUserId;

  @Column(name = "company_commercial_name", nullable = false, length = 180)
  private String companyCommercialName;

  @Column(nullable = false, length = 180)
  private String title;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String description;

  @Column(nullable = false, length = 120)
  private String city;

  @Column(name = "region_id", nullable = false)
  private Long regionId;

  @Column(name = "commune_id", nullable = false)
  private Long communeId;

  @Column(name = "salary_offered", nullable = false)
  private int salaryOffered;

  @JdbcTypeCode(SqlTypes.TINYINT)
  @Column(name = "years_experience_required", nullable = false)
  private int yearsExperienceRequired;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Modality modality;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Workload workload;

  @Column(name = "image_url", length = 500)
  private String imageUrl;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private JobStatus status;

  @Column(name = "published_at")
  private LocalDateTime publishedAt;

  @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
  private LocalDateTime updatedAt;

  public Job() {}

  public Long getId() {
    return id;
  }

  public Long getCompanyId() {
    return companyId;
  }

  public Long getPublishedByUserId() {
    return publishedByUserId;
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

  public Long getRegionId() {
    return regionId;
  }

  public Long getCommuneId() {
    return communeId;
  }

  public int getSalaryOffered() {
    return salaryOffered;
  }

  public int getYearsExperienceRequired() {
    return yearsExperienceRequired;
  }

  public Modality getModality() {
    return modality;
  }

  public Workload getWorkload() {
    return workload;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public JobStatus getStatus() {
    return status;
  }

  public LocalDateTime getPublishedAt() {
    return publishedAt;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setCompanyId(Long companyId) {
    this.companyId = companyId;
  }

  public void setPublishedByUserId(Long publishedByUserId) {
    this.publishedByUserId = publishedByUserId;
  }

  public void setCompanyCommercialName(String companyCommercialName) {
    this.companyCommercialName = companyCommercialName;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public void setRegionId(Long regionId) {
    this.regionId = regionId;
  }

  public void setCommuneId(Long communeId) {
    this.communeId = communeId;
  }

  public void setSalaryOffered(int salaryOffered) {
    this.salaryOffered = salaryOffered;
  }

  public void setYearsExperienceRequired(int yearsExperienceRequired) {
    this.yearsExperienceRequired = yearsExperienceRequired;
  }

  public void setModality(Modality modality) {
    this.modality = modality;
  }

  public void setWorkload(Workload workload) {
    this.workload = workload;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public void setStatus(JobStatus status) {
    this.status = status;
  }

  public void setPublishedAt(LocalDateTime publishedAt) {
    this.publishedAt = publishedAt;
  }
}
