package cl.duoc.worksi.repository;

import cl.duoc.worksi.entity.ConversationMessage;
import cl.duoc.worksi.entity.enums.MessageSenderRole;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationMessageRepository extends JpaRepository<ConversationMessage, Long> {
  Page<ConversationMessage> findByConversationId(long conversationId, Pageable pageable);

  List<ConversationMessage> findByConversationIdAndIdGreaterThanOrderByIdAsc(
      long conversationId, long afterMessageId, Pageable pageable);

  Optional<ConversationMessage> findTopByConversationIdOrderByIdDesc(long conversationId);

  Optional<ConversationMessage> findFirstByConversationIdOrderByIdAsc(long conversationId);

  long countByConversationIdAndSenderRoleAndIdGreaterThan(
      long conversationId, MessageSenderRole senderRole, long messageId);
}
