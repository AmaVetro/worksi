package cl.duoc.worksi.dto.candidate;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CandidateRegisterResponse {
  @JsonProperty("user_id")
  private final long userId;

  @JsonProperty("role")
  private final String role;

  @JsonProperty("access_token")
  private final String accessToken;

  @JsonProperty("token_type")
  private final String tokenType;

  @JsonProperty("expires_in")
  private final int expiresIn;

  public CandidateRegisterResponse(
      long userId, String role, String accessToken, String tokenType, int expiresIn) {
    this.userId = userId;
    this.role = role;
    this.accessToken = accessToken;
    this.tokenType = tokenType;
    this.expiresIn = expiresIn;
  }

  public long getUserId() {
    return userId;
  }

  public String getRole() {
    return role;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public String getTokenType() {
    return tokenType;
  }

  public int getExpiresIn() {
    return expiresIn;
  }
}
