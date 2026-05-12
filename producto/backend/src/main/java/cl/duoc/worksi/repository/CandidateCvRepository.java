package cl.duoc.worksi.repository;

import cl.duoc.worksi.entity.CandidateCv;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidateCvRepository extends JpaRepository<CandidateCv, Long> {
  Optional<CandidateCv> findTopByCandidateUserIdAndCurrentIsTrueOrderByUploadedAtDesc(
      Long candidateUserId);
}
