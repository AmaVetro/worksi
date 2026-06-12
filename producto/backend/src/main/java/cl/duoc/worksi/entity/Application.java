package cl.duoc.worksi.entity;

import cl.duoc.worksi.entity.enums.ApplicationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Entity
@Table(name = "applications")
public class Application {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "candidate_user_id", nullable = false)
  private Long candidateUserId;

  @Column(name = "job_id", nullable = false)
  private Long jobId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ApplicationStatus status;

  @Column(name = "viewed_at")
  private LocalDateTime viewedAt;

  @Column(name = "cancelled_at")
  private LocalDateTime cancelledAt;

  @Column(name = "match_score", precision = 5, scale = 2)
  private BigDecimal matchScore;

  @Column(name = "match_explanation", length = 500)
  private String matchExplanation;

  @Column(name = "description_score", precision = 5, scale = 2)
  private BigDecimal descriptionScore;

  @Column(name = "title_score", precision = 5, scale = 2)
  private BigDecimal titleScore;

  @Column(name = "modality_score", precision = 5, scale = 2)
  private BigDecimal modalityScore;

  @Column(name = "workload_score", precision = 5, scale = 2)
  private BigDecimal workloadScore;

  @Column(name = "experience_score", precision = 5, scale = 2)
  private BigDecimal experienceScore;

  @Column(name = "matched_at")
  private LocalDateTime matchedAt;

  @Column(name = "applied_at", nullable = false, insertable = false, updatable = false)
  private LocalDateTime appliedAt;

  @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
  private LocalDateTime updatedAt;

  protected Application() {}

  public static Application createApplied(
      long candidateUserId,
      long jobId,
      BigDecimal matchScore,
      String matchExplanation,
      BigDecimal descriptionScore,
      BigDecimal titleScore,
      BigDecimal modalityScore,
      BigDecimal workloadScore,
      BigDecimal experienceScore) {
    Application a = new Application();
    a.candidateUserId = candidateUserId;
    a.jobId = jobId;
    a.status = ApplicationStatus.APPLIED;
    a.matchScore = matchScore;
    if (matchExplanation != null && matchExplanation.length() > 500) {
      a.matchExplanation = matchExplanation.substring(0, 500);
    } else {
      a.matchExplanation = matchExplanation;
    }
    a.descriptionScore = descriptionScore;
    a.titleScore = titleScore;
    a.modalityScore = modalityScore;
    a.workloadScore = workloadScore;
    a.experienceScore = experienceScore;
    a.matchedAt = LocalDateTime.now(ZoneOffset.UTC);
    return a;
  }

  public Long getId() {
    return id;
  }

  public Long getCandidateUserId() {
    return candidateUserId;
  }

  public Long getJobId() {
    return jobId;
  }

  public ApplicationStatus getStatus() {
    return status;
  }

  public LocalDateTime getViewedAt() {
    return viewedAt;
  }

  public LocalDateTime getCancelledAt() {
    return cancelledAt;
  }

  public BigDecimal getMatchScore() {
    return matchScore;
  }

  public String getMatchExplanation() {
    return matchExplanation;
  }

  public BigDecimal getDescriptionScore() {
    return descriptionScore;
  }

  public BigDecimal getTitleScore() {
    return titleScore;
  }

  public BigDecimal getModalityScore() {
    return modalityScore;
  }

  public BigDecimal getWorkloadScore() {
    return workloadScore;
  }

  public BigDecimal getExperienceScore() {
    return experienceScore;
  }

  public LocalDateTime getMatchedAt() {
    return matchedAt;
  }

  public LocalDateTime getAppliedAt() {
    return appliedAt;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void markViewedIfApplied() {
    if (status != ApplicationStatus.APPLIED) {
      return;
    }
    status = ApplicationStatus.VIEWED;
    viewedAt = LocalDateTime.now(ZoneOffset.UTC);
  }

  public void cancel() {
    if (status == ApplicationStatus.CANCELLED) {
      throw new IllegalStateException("Postulacion ya cancelada");
    }
    status = ApplicationStatus.CANCELLED;
    cancelledAt = LocalDateTime.now(ZoneOffset.UTC);
  }
}
