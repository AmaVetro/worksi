package cl.duoc.worksi.dto.messaging;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

public class MessageItemResponse {
  @JsonProperty("message_id")
  private final long messageId;

  @JsonProperty("conversation_id")
  private final long conversationId;

  @JsonProperty("sender_user_id")
  private final long senderUserId;

  @JsonProperty("sender_role")
  private final String senderRole;

  private final String body;

  @JsonProperty("sent_at")
  private final Instant sentAt;

  public MessageItemResponse(
      long messageId,
      long conversationId,
      long senderUserId,
      String senderRole,
      String body,
      Instant sentAt) {
    this.messageId = messageId;
    this.conversationId = conversationId;
    this.senderUserId = senderUserId;
    this.senderRole = senderRole;
    this.body = body;
    this.sentAt = sentAt;
  }

  public long getMessageId() {
    return messageId;
  }

  public long getConversationId() {
    return conversationId;
  }

  public long getSenderUserId() {
    return senderUserId;
  }

  public String getSenderRole() {
    return senderRole;
  }

  public String getBody() {
    return body;
  }

  public Instant getSentAt() {
    return sentAt;
  }
}
