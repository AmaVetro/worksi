package cl.duoc.worksi.dto.company;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

public class CompanyJobCreatedResponse {
  private final long id;
  private final String status;

  @JsonProperty("published_at")
  private final Instant publishedAt;

  public CompanyJobCreatedResponse(long id, String status, Instant publishedAt) {
    this.id = id;
    this.status = status;
    this.publishedAt = publishedAt;
  }

  public long getId() {
    return id;
  }

  public String getStatus() {
    return status;
  }

  public Instant getPublishedAt() {
    return publishedAt;
  }
}
