package cl.duoc.worksi.repository;

import cl.duoc.worksi.entity.JobSkill;
import cl.duoc.worksi.entity.JobSkillId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JobSkillRepository extends JpaRepository<JobSkill, JobSkillId> {
  @Query(
      "SELECT js FROM JobSkill js JOIN Skill sk ON sk.id = js.id.skillId WHERE js.id.jobId = :jobId ORDER BY sk.name ASC")
  List<JobSkill> findAllByJobIdOrderBySkillName(@Param("jobId") long jobId);
}
