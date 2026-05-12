package cl.duoc.worksi.dto.candidate;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CandidateJobDetailMatchResponse {
  private final Double score;
  private final String explanation;

  public CandidateJobDetailMatchResponse(Double score, String explanation) {
    this.score = score;
    this.explanation = explanation;
  }

  public Double getScore() {
    return score;
  }

  public String getExplanation() {
    return explanation;
  }
}
