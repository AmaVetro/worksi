package cl.duoc.worksi.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public class PasswordRecoveryVerifyDto {
  @NotBlank
  @JsonProperty("email")
  private String email;

  @NotBlank
  @JsonProperty("code")
  private String code;

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }
}
