package cl.duoc.worksi.repository;

import cl.duoc.worksi.entity.CandidateSkill;
import cl.duoc.worksi.entity.CandidateSkillId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidateSkillRepository extends JpaRepository<CandidateSkill, CandidateSkillId> {}
