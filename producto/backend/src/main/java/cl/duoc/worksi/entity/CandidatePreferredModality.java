package cl.duoc.worksi.entity;

import cl.duoc.worksi.entity.enums.Modality;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "candidate_preferred_modalities")
@IdClass(CandidatePreferredModalityId.class)
public class CandidatePreferredModality {
  @Id
  @Column(name = "candidate_user_id")
  private Long candidateUserId;

  @Id
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Modality modality;

  @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
  private LocalDateTime createdAt;

  protected CandidatePreferredModality() {}

  public Long getCandidateUserId() {
    return candidateUserId;
  }

  public Modality getModality() {
    return modality;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }
}
