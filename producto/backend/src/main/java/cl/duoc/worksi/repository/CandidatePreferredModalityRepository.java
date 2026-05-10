package cl.duoc.worksi.repository;

import cl.duoc.worksi.entity.CandidatePreferredModality;
import cl.duoc.worksi.entity.CandidatePreferredModalityId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CandidatePreferredModalityRepository
    extends JpaRepository<CandidatePreferredModality, CandidatePreferredModalityId> {
  List<CandidatePreferredModality> findByCandidateUserId(long candidateUserId);

  @Modifying
  @Query("DELETE FROM CandidatePreferredModality m WHERE m.candidateUserId = :uid")
  void deleteByCandidateUserId(@Param("uid") long uid);
}
