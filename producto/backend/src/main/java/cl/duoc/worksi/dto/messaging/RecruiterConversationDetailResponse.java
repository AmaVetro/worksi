package cl.duoc.worksi.dto.messaging;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

public class RecruiterConversationDetailResponse {
  @JsonProperty("conversation_id")
  private final long conversationId;

  @JsonProperty("candidate_user_id")
  private final long candidateUserId;

  @JsonProperty("candidate_display_name")
  private final String candidateDisplayName;

  @JsonProperty("application_id")
  private final long applicationId;

  @JsonProperty("job_id")
  private final long jobId;

  @JsonProperty("job_title")
  private final String jobTitle;

  @JsonProperty("created_at")
  private final Instant createdAt;

  public RecruiterConversationDetailResponse(
      long conversationId,
      long candidateUserId,
      String candidateDisplayName,
      long applicationId,
      long jobId,
      String jobTitle,
      Instant createdAt) {
    this.conversationId = conversationId;
    this.candidateUserId = candidateUserId;
    this.candidateDisplayName = candidateDisplayName;
    this.applicationId = applicationId;
    this.jobId = jobId;
    this.jobTitle = jobTitle;
    this.createdAt = createdAt;
  }

  public long getConversationId() {
    return conversationId;
  }

  public long getCandidateUserId() {
    return candidateUserId;
  }

  public String getCandidateDisplayName() {
    return candidateDisplayName;
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

  public Instant getCreatedAt() {
    return createdAt;
  }
}
