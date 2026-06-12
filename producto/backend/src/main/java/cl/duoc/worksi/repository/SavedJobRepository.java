package cl.duoc.worksi.repository;

import cl.duoc.worksi.entity.SavedJob;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SavedJobRepository extends JpaRepository<SavedJob, Long> {
  boolean existsByCandidateUserIdAndJobId(Long candidateUserId, Long jobId);

  Optional<SavedJob> findByCandidateUserIdAndJobId(Long candidateUserId, Long jobId);

  void deleteByCandidateUserIdAndJobId(Long candidateUserId, Long jobId);

  Page<SavedJob> findByCandidateUserIdOrderByCreatedAtDesc(
      Long candidateUserId, Pageable pageable);
}
