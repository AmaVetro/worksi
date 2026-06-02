package cl.duoc.worksi.dto.candidate;

import cl.duoc.worksi.dto.MatchBreakdownResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CandidateJobDetailMatchResponse {
  private final Double score;
  private final String explanation;

  @JsonProperty("match_breakdown")
  private final MatchBreakdownResponse matchBreakdown;

  public CandidateJobDetailMatchResponse(
      Double score, String explanation, MatchBreakdownResponse matchBreakdown) {
    this.score = score;
    this.explanation = explanation;
    this.matchBreakdown = matchBreakdown;
  }

  public Double getScore() {
    return score;
  }

  public String getExplanation() {
    return explanation;
  }

  public MatchBreakdownResponse getMatchBreakdown() {
    return matchBreakdown;
  }
}
