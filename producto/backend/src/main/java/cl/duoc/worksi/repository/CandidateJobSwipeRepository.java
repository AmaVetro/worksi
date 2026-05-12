package cl.duoc.worksi.repository;

import cl.duoc.worksi.entity.CandidateJobSwipe;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CandidateJobSwipeRepository extends JpaRepository<CandidateJobSwipe, Long> {
  @Query("SELECT s.jobId FROM CandidateJobSwipe s WHERE s.candidateUserId = :uid")
  List<Long> findJobIdsByCandidateUserId(@Param("uid") long candidateUserId);

  boolean existsByCandidateUserIdAndJobId(long candidateUserId, long jobId);
}
