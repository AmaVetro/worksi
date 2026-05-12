package cl.duoc.worksi.repository;

import cl.duoc.worksi.entity.Skill;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SkillRepository extends JpaRepository<Skill, Long> {

  @Query(
      "SELECT sk FROM Skill sk JOIN SectorSkill ss ON sk.id = ss.id.skillId "
          + "WHERE ss.id.sectorId = :sectorId AND sk.active = TRUE ORDER BY sk.name")
  List<Skill> findActiveBySectorId(@Param("sectorId") long sectorId);

  @Query("SELECT sk FROM Skill sk WHERE sk.id IN :ids AND sk.active = TRUE ORDER BY sk.name")
  List<Skill> findAllActiveByIdIn(@Param("ids") Collection<Long> ids);
}
