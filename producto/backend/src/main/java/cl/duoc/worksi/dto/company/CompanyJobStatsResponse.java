package cl.duoc.worksi.dto.company;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CompanyJobStatsResponse {
  @JsonProperty("active_count")
  private final long activeCount;

  @JsonProperty("inactive_count")
  private final long inactiveCount;

  @JsonProperty("published_today_count")
  private final long publishedTodayCount;

  public CompanyJobStatsResponse(
      long activeCount, long inactiveCount, long publishedTodayCount) {
    this.activeCount = activeCount;
    this.inactiveCount = inactiveCount;
    this.publishedTodayCount = publishedTodayCount;
  }

  public long getActiveCount() {
    return activeCount;
  }

  public long getInactiveCount() {
    return inactiveCount;
  }

  public long getPublishedTodayCount() {
    return publishedTodayCount;
  }
}
