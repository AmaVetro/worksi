package cl.duoc.worksi.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LockInfoResponse {
  @JsonProperty("is_locked")
  private final boolean locked;

  @JsonProperty("failed_attempts")
  private final int failedAttempts;

  @JsonProperty("remaining_attempts")
  private final int remainingAttempts;

  public LockInfoResponse(boolean locked, int failedAttempts, int remainingAttempts) {
    this.locked = locked;
    this.failedAttempts = failedAttempts;
    this.remainingAttempts = remainingAttempts;
  }

  public boolean isLocked() {
    return locked;
  }

  public int getFailedAttempts() {
    return failedAttempts;
  }

  public int getRemainingAttempts() {
    return remainingAttempts;
  }
}
