package cl.duoc.worksi.dto.company;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

public class CompanyApplicationViewedResponse {
  @JsonProperty("application_id")
  private final long applicationId;

  @JsonProperty("status")
  private final String status;

  @JsonProperty("viewed_at")
  private final Instant viewedAt;

  public CompanyApplicationViewedResponse(long applicationId, String status, Instant viewedAt) {
    this.applicationId = applicationId;
    this.status = status;
    this.viewedAt = viewedAt;
  }

  public long getApplicationId() {
    return applicationId;
  }

  public String getStatus() {
    return status;
  }

  public Instant getViewedAt() {
    return viewedAt;
  }
}
