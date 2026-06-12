package cl.duoc.worksi.dto.messaging;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

public class CandidateConversationDetailResponse {
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

  @JsonProperty("created_at")
  private final Instant createdAt;

  public CandidateConversationDetailResponse(
      long conversationId,
      long companyId,
      String companyCommercialName,
      long applicationId,
      long jobId,
      String jobTitle,
      Instant createdAt) {
    this.conversationId = conversationId;
    this.companyId = companyId;
    this.companyCommercialName = companyCommercialName;
    this.applicationId = applicationId;
    this.jobId = jobId;
    this.jobTitle = jobTitle;
    this.createdAt = createdAt;
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

  public Instant getCreatedAt() {
    return createdAt;
  }
}
