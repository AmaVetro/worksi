package cl.duoc.worksi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class SectorSkillId implements Serializable {
  @Column(name = "sector_id", nullable = false)
  private Long sectorId;

  @Column(name = "skill_id", nullable = false)
  private Long skillId;

  protected SectorSkillId() {}

  public Long getSectorId() {
    return sectorId;
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
    SectorSkillId that = (SectorSkillId) o;
    return Objects.equals(sectorId, that.sectorId) && Objects.equals(skillId, that.skillId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(sectorId, skillId);
  }
}
