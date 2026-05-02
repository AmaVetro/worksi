package cl.duoc.worksi.entity;

import cl.duoc.worksi.entity.enums.Workload;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "candidate_preferred_workloads")
@IdClass(CandidatePreferredWorkloadId.class)
public class CandidatePreferredWorkload {
  @Id
  @Column(name = "candidate_user_id")
  private Long candidateUserId;

  @Id
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Workload workload;

  @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
  private LocalDateTime createdAt;

  protected CandidatePreferredWorkload() {}

  public Long getCandidateUserId() {
    return candidateUserId;
  }

  public Workload getWorkload() {
    return workload;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }
}
