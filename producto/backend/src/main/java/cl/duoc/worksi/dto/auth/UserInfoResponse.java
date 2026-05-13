package cl.duoc.worksi.dto.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInfoResponse {
  @JsonProperty("id")
  private final long id;

  @JsonProperty("role")
  private final String role;

  @JsonProperty("email")
  private final String email;

  private final String firstName;

  private final String lastNamePaternal;

  private final String lastNameMaternal;

  public UserInfoResponse(
      long id,
      String role,
      String email,
      String firstName,
      String lastNamePaternal,
      String lastNameMaternal) {
    this.id = id;
    this.role = role;
    this.email = email;
    this.firstName = firstName;
    this.lastNamePaternal = lastNamePaternal;
    this.lastNameMaternal = lastNameMaternal;
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

  @JsonProperty("first_name")
  public String getFirstName() {
    return firstName;
  }

  @JsonProperty("last_name_paternal")
  public String getLastNamePaternal() {
    return lastNamePaternal;
  }

  @JsonProperty("last_name_maternal")
  public String getLastNameMaternal() {
    return lastNameMaternal;
  }
}
