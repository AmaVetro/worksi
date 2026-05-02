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

  @Column(name = "matched_at")
  private LocalDateTime matchedAt;

  @Column(name = "applied_at", nullable = false, insertable = false, updatable = false)
  private LocalDateTime appliedAt;

  @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
  private LocalDateTime updatedAt;

  protected Application() {}

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
}
