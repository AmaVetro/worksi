package cl.duoc.worksi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CandidateSkillId implements Serializable {
  @Column(name = "candidate_user_id", nullable = false)
  private Long candidateUserId;

  @Column(name = "skill_id", nullable = false)
  private Long skillId;

  protected CandidateSkillId() {}

  public Long getCandidateUserId() {
    return candidateUserId;
  }

  public Long getSkillId() {
    return skillId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CandidateSkillId that = (CandidateSkillId) o;
    return Objects.equals(candidateUserId, that.candidateUserId) && Objects.equals(skillId, that.skillId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(candidateUserId, skillId);
  }
}
