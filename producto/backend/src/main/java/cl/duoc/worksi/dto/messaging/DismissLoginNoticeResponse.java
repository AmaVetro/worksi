package cl.duoc.worksi.dto.messaging;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DismissLoginNoticeResponse {
  @JsonProperty("conversation_id")
  private final long conversationId;

  @JsonProperty("login_notice_dismissed")
  private final boolean loginNoticeDismissed;

  public DismissLoginNoticeResponse(long conversationId, boolean loginNoticeDismissed) {
    this.conversationId = conversationId;
    this.loginNoticeDismissed = loginNoticeDismissed;
  }

  public long getConversationId() {
    return conversationId;
  }

  public boolean isLoginNoticeDismissed() {
    return loginNoticeDismissed;
  }
}
