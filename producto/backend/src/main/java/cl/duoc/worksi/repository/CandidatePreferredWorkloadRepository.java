package cl.duoc.worksi.repository;

import cl.duoc.worksi.entity.CandidatePreferredWorkload;
import cl.duoc.worksi.entity.CandidatePreferredWorkloadId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CandidatePreferredWorkloadRepository
    extends JpaRepository<CandidatePreferredWorkload, CandidatePreferredWorkloadId> {
  List<CandidatePreferredWorkload> findByCandidateUserId(long candidateUserId);

  @Modifying
  @Query("DELETE FROM CandidatePreferredWorkload w WHERE w.candidateUserId = :uid")
  void deleteByCandidateUserId(@Param("uid") long uid);
}
