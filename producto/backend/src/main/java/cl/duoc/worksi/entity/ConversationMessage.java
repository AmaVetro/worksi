package cl.duoc.worksi.entity;

import cl.duoc.worksi.entity.enums.MessageSenderRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "conversation_messages")
public class ConversationMessage {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "conversation_id", nullable = false)
  private Long conversationId;

  @Column(name = "sender_user_id", nullable = false)
  private Long senderUserId;

  @Enumerated(EnumType.STRING)
  @Column(name = "sender_role", nullable = false)
  private MessageSenderRole senderRole;

  @Column(nullable = false, length = 500)
  private String body;

  @Column(name = "sent_at", nullable = false, insertable = false, updatable = false)
  private java.time.LocalDateTime sentAt;

  protected ConversationMessage() {}

  public static ConversationMessage create(
      long conversationId, long senderUserId, MessageSenderRole senderRole, String body) {
    ConversationMessage m = new ConversationMessage();
    m.conversationId = conversationId;
    m.senderUserId = senderUserId;
    m.senderRole = senderRole;
    m.body = body;
    return m;
  }

  public Long getId() {
    return id;
  }

  public Long getConversationId() {
    return conversationId;
  }

  public Long getSenderUserId() {
    return senderUserId;
  }

  public MessageSenderRole getSenderRole() {
    return senderRole;
  }

  public String getBody() {
    return body;
  }

  public java.time.LocalDateTime getSentAt() {
    return sentAt;
  }
}
