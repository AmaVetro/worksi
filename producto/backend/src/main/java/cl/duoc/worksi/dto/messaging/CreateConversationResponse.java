package cl.duoc.worksi.dto.messaging;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

public class CreateConversationResponse {
  @JsonProperty("conversation_id")
  private final long conversationId;

  @JsonProperty("application_id")
  private final long applicationId;

  @JsonProperty("candidate_user_id")
  private final long candidateUserId;

  @JsonProperty("company_id")
  private final long companyId;

  @JsonProperty("company_commercial_name")
  private final String companyCommercialName;

  @JsonProperty("recruiter_user_id")
  private final long recruiterUserId;

  @JsonProperty("created_at")
  private final Instant createdAt;

  @JsonProperty("first_message")
  private final FirstMessageResponse firstMessage;

  public CreateConversationResponse(
      long conversationId,
      long applicationId,
      long candidateUserId,
      long companyId,
      String companyCommercialName,
      long recruiterUserId,
      Instant createdAt,
      FirstMessageResponse firstMessage) {
    this.conversationId = conversationId;
    this.applicationId = applicationId;
    this.candidateUserId = candidateUserId;
    this.companyId = companyId;
    this.companyCommercialName = companyCommercialName;
    this.recruiterUserId = recruiterUserId;
    this.createdAt = createdAt;
    this.firstMessage = firstMessage;
  }

  public long getConversationId() {
    return conversationId;
  }

  public long getApplicationId() {
    return applicationId;
  }

  public long getCandidateUserId() {
    return candidateUserId;
  }

  public long getCompanyId() {
    return companyId;
  }

  public String getCompanyCommercialName() {
    return companyCommercialName;
  }

  public long getRecruiterUserId() {
    return recruiterUserId;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public FirstMessageResponse getFirstMessage() {
    return firstMessage;
  }
}
