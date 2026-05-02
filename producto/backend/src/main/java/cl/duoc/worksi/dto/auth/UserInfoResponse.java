package cl.duoc.worksi.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserInfoResponse {
  @JsonProperty("id")
  private final long id;

  @JsonProperty("role")
  private final String role;

  @JsonProperty("email")
  private final String email;

  public UserInfoResponse(long id, String role, String email) {
    this.id = id;
    this.role = role;
    this.email = email;
  }

  public long getId() {
    return id;
  }

  public String getRole() {
    return role;
  }

  public String getEmail() {
    return email;
  }
}
