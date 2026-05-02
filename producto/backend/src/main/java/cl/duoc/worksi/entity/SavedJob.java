package cl.duoc.worksi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "saved_jobs")
public class SavedJob {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "candidate_user_id", nullable = false)
  private Long candidateUserId;

  @Column(name = "job_id", nullable = false)
  private Long jobId;

  @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
  private LocalDateTime createdAt;

  protected SavedJob() {}

  public Long getId() {
    return id;
  }

  public Long getCandidateUserId() {
    return candidateUserId;
  }

  public Long getJobId() {
    return jobId;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }
}
