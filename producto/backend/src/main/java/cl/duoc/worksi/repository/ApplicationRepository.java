package cl.duoc.worksi.repository;

import cl.duoc.worksi.entity.Application;
import cl.duoc.worksi.entity.enums.ApplicationStatus;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
  boolean existsByCandidateUserIdAndJobId(long candidateUserId, long jobId);

  @Query("SELECT a.jobId FROM Application a WHERE a.candidateUserId = :uid")
  List<Long> findJobIdsByCandidateUserId(@Param("uid") long candidateUserId);

  Page<Application> findByCandidateUserIdAndStatusIn(
      long candidateUserId, Collection<ApplicationStatus> statuses, Pageable pageable);

  Optional<Application> findByIdAndCandidateUserId(long id, long candidateUserId);

  Page<Application> findByJobIdAndStatusIn(
      long jobId, Collection<ApplicationStatus> statuses, Pageable pageable);

  long countByJobIdAndStatusIn(long jobId, Collection<ApplicationStatus> statuses);
}
