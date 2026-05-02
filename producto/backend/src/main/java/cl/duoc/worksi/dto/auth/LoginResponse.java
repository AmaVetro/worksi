package cl.duoc.worksi.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginResponse {
  @JsonProperty("access_token")
  private final String accessToken;

  @JsonProperty("token_type")
  private final String tokenType;

  @JsonProperty("expires_in")
  private final int expiresIn;

  @JsonProperty("user")
  private final UserInfoResponse user;

  @JsonProperty("lock_info")
  private final LockInfoResponse lockInfo;

  public LoginResponse(
      String accessToken,
      String tokenType,
      int expiresIn,
      UserInfoResponse user,
      LockInfoResponse lockInfo) {
    this.accessToken = accessToken;
    this.tokenType = tokenType;
    this.expiresIn = expiresIn;
    this.user = user;
    this.lockInfo = lockInfo;
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

  public UserInfoResponse getUser() {
    return user;
  }

  public LockInfoResponse getLockInfo() {
    return lockInfo;
  }
}
