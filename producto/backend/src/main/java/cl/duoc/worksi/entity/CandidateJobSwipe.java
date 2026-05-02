package cl.duoc.worksi.entity;

import cl.duoc.worksi.entity.enums.SwipeAction;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "candidate_job_swipes")
public class CandidateJobSwipe {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "candidate_user_id", nullable = false)
  private Long candidateUserId;

  @Column(name = "job_id", nullable = false)
  private Long jobId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private SwipeAction action;

  @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
  private LocalDateTime createdAt;

  protected CandidateJobSwipe() {}

  public Long getId() {
    return id;
  }

  public Long getCandidateUserId() {
    return candidateUserId;
  }

  public Long getJobId() {
    return jobId;
  }

  public SwipeAction getAction() {
    return action;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }
}
