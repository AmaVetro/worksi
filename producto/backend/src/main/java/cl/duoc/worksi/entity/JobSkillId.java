package cl.duoc.worksi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class JobSkillId implements Serializable {
  @Column(name = "job_id", nullable = false)
  private Long jobId;

  @Column(name = "skill_id", nullable = false)
  private Long skillId;

  protected JobSkillId() {}

  public Long getJobId() {
    return jobId;
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
    JobSkillId jobSkillId = (JobSkillId) o;
    return Objects.equals(jobId, jobSkillId.jobId) && Objects.equals(skillId, jobSkillId.skillId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(jobId, skillId);
  }
}
