package cl.duoc.worksi.dto.candidate;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CandidateSavedJobRequest {
  @JsonProperty("job_id")
  private Long jobId;

  public Long getJobId() {
    return jobId;
  }

  public void setJobId(Long jobId) {
    this.jobId = jobId;
  }
}
