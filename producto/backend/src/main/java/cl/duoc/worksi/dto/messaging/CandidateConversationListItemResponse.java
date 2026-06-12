package cl.duoc.worksi.dto.messaging;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

public class CandidateConversationListItemResponse {
  @JsonProperty("conversation_id")
  private final long conversationId;

  @JsonProperty("company_id")
  private final long companyId;

  @JsonProperty("company_commercial_name")
  private final String companyCommercialName;

  @JsonProperty("application_id")
  private final long applicationId;

  @JsonProperty("job_id")
  private final long jobId;

  @JsonProperty("job_title")
  private final String jobTitle;

  @JsonProperty("last_message_preview")
  private final String lastMessagePreview;

  @JsonProperty("last_message_at")
  private final Instant lastMessageAt;

  @JsonProperty("updated_at")
  private final Instant updatedAt;

  @JsonProperty("unread_count")
  private final int unreadCount;

  @JsonProperty("match_score")
  private final Double matchScore;

  public CandidateConversationListItemResponse(
      long conversationId,
      long companyId,
      String companyCommercialName,
      long applicationId,
      long jobId,
      String jobTitle,
      String lastMessagePreview,
      Instant lastMessageAt,
      Instant updatedAt,
      int unreadCount,
      Double matchScore) {
    this.conversationId = conversationId;
    this.companyId = companyId;
    this.companyCommercialName = companyCommercialName;
    this.applicationId = applicationId;
    this.jobId = jobId;
    this.jobTitle = jobTitle;
    this.lastMessagePreview = lastMessagePreview;
    this.lastMessageAt = lastMessageAt;
    this.updatedAt = updatedAt;
    this.unreadCount = unreadCount;
    this.matchScore = matchScore;
  }

  public long getConversationId() {
    return conversationId;
  }

  public long getCompanyId() {
    return companyId;
  }

  public String getCompanyCommercialName() {
    return companyCommercialName;
  }

  public long getApplicationId() {
    return applicationId;
  }

  public long getJobId() {
    return jobId;
  }

  public String getJobTitle() {
    return jobTitle;
  }

  public String getLastMessagePreview() {
    return lastMessagePreview;
  }

  public Instant getLastMessageAt() {
    return lastMessageAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public int getUnreadCount() {
    return unreadCount;
  }

  public Double getMatchScore() {
    return matchScore;
  }
}
