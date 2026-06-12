package cl.duoc.worksi.dto.messaging;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginNoticeItemResponse {
  @JsonProperty("conversation_id")
  private final long conversationId;

  @JsonProperty("company_commercial_name")
  private final String companyCommercialName;

  @JsonProperty("job_title")
  private final String jobTitle;

  @JsonProperty("first_message_preview")
  private final String firstMessagePreview;

  @JsonProperty("salary_offered")
  private final int salaryOffered;

  public LoginNoticeItemResponse(
      long conversationId,
      String companyCommercialName,
      String jobTitle,
      String firstMessagePreview,
      int salaryOffered) {
    this.conversationId = conversationId;
    this.companyCommercialName = companyCommercialName;
    this.jobTitle = jobTitle;
    this.firstMessagePreview = firstMessagePreview;
    this.salaryOffered = salaryOffered;
  }

  public long getConversationId() {
    return conversationId;
  }

  public String getCompanyCommercialName() {
    return companyCommercialName;
  }

  public String getJobTitle() {
    return jobTitle;
  }

  public String getFirstMessagePreview() {
    return firstMessagePreview;
  }

  public int getSalaryOffered() {
    return salaryOffered;
  }
}
