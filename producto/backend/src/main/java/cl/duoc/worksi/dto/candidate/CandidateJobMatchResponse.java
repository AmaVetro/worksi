package cl.duoc.worksi.dto.candidate;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CandidateJobMatchResponse {
  private final Double score;

  @JsonProperty("explanation_short")
  private final String explanationShort;

  public CandidateJobMatchResponse(Double score, String explanationShort) {
    this.score = score;
    this.explanationShort = explanationShort;
  }

  public Double getScore() {
    return score;
  }

  public String getExplanationShort() {
    return explanationShort;
  }
}
