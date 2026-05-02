package cl.duoc.worksi.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public class PasswordRecoveryResetDto {
  @NotBlank
  @JsonProperty("email")
  private String email;

  @NotBlank
  @JsonProperty("recovery_token")
  private String recoveryToken;

  @NotBlank
  @JsonProperty("new_password")
  private String newPassword;

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getRecoveryToken() {
    return recoveryToken;
  }

  public void setRecoveryToken(String recoveryToken) {
    this.recoveryToken = recoveryToken;
  }

  public String getNewPassword() {
    return newPassword;
  }

  public void setNewPassword(String newPassword) {
    this.newPassword = newPassword;
  }
}
