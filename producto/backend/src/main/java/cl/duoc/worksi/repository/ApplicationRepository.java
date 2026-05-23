package cl.duoc.worksi.repository;

import cl.duoc.worksi.entity.Application;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
  boolean existsByCandidateUserIdAndJobId(long candidateUserId, long jobId);

  @Query("SELECT a.jobId FROM Application a WHERE a.candidateUserId = :uid")
  List<Long> findJobIdsByCandidateUserId(@Param("uid") long candidateUserId);
}
