package cl.duoc.worksi.dto.messaging;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

public class FirstMessageResponse {
  @JsonProperty("message_id")
  private final long messageId;

  private final String body;

  @JsonProperty("sent_at")
  private final Instant sentAt;

  public FirstMessageResponse(long messageId, String body, Instant sentAt) {
    this.messageId = messageId;
    this.body = body;
    this.sentAt = sentAt;
  }

  public long getMessageId() {
    return messageId;
  }

  public String getBody() {
    return body;
  }

  public Instant getSentAt() {
    return sentAt;
  }
}
