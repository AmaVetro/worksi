package cl.duoc.worksi.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MatchApiRequest {
  @JsonProperty("cv_text")
  private final String cvText;

  @JsonProperty("job_text")
  private final String jobText;

  public MatchApiRequest(String cvText, String jobText) {
    this.cvText = cvText;
    this.jobText = jobText;
  }

  public String getCvText() {
    return cvText;
  }

  public String getJobText() {
    return jobText;
  }
}
