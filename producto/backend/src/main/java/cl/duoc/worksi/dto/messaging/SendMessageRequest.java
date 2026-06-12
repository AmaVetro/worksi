package cl.duoc.worksi.dto.messaging;

import jakarta.validation.constraints.NotBlank;

public class SendMessageRequest {
  @NotBlank private String body;

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }
}
