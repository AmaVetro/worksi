package cl.duoc.worksi.repository;

import cl.duoc.worksi.entity.JobSkill;
import cl.duoc.worksi.entity.JobSkillId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobSkillRepository extends JpaRepository<JobSkill, JobSkillId> {}
