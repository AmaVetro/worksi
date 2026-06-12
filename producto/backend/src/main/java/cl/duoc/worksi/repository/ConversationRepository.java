package cl.duoc.worksi.repository;

import cl.duoc.worksi.entity.Conversation;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
  Optional<Conversation> findByRecruiterUserIdAndCandidateUserId(
      long recruiterUserId, long candidateUserId);

  Page<Conversation> findByRecruiterUserId(long recruiterUserId, Pageable pageable);

  Page<Conversation> findByCandidateUserId(long candidateUserId, Pageable pageable);

  Optional<Conversation> findFirstByCandidateUserIdAndLoginNoticeDismissedFalseOrderByCreatedAtDesc(
      long candidateUserId);
}
