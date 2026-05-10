package cl.duoc.worksi.repository;

import cl.duoc.worksi.entity.CandidateSkill;
import cl.duoc.worksi.entity.CandidateSkillId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CandidateSkillRepository extends JpaRepository<CandidateSkill, CandidateSkillId> {
  List<CandidateSkill> findByIdCandidateUserIdOrderByIdSkillIdAsc(long candidateUserId);

  @Modifying
  @Query("DELETE FROM CandidateSkill cs WHERE cs.id.candidateUserId = :uid")
  void deleteByCandidateUserId(@Param("uid") long uid);
}
