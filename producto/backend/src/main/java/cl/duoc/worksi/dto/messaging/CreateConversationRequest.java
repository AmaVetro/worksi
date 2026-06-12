package cl.duoc.worksi.dto.messaging;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateConversationRequest {
  @NotNull
  @JsonProperty("application_id")
  private Long applicationId;

  @NotBlank
  @JsonProperty("first_message")
  private String firstMessage;

  public Long getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(Long applicationId) {
    this.applicationId = applicationId;
  }

  public String getFirstMessage() {
    return firstMessage;
  }

  public void setFirstMessage(String firstMessage) {
    this.firstMessage = firstMessage;
  }
}
