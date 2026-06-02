package cl.duoc.worksi.dto.candidate;

import cl.duoc.worksi.dto.MatchBreakdownResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CandidateJobMatchResponse {
  private final Double score;

  @JsonProperty("explanation_short")
  private final String explanationShort;

  @JsonProperty("match_breakdown")
  private final MatchBreakdownResponse matchBreakdown;

  public CandidateJobMatchResponse(
      Double score, String explanationShort, MatchBreakdownResponse matchBreakdown) {
    this.score = score;
    this.explanationShort = explanationShort;
    this.matchBreakdown = matchBreakdown;
  }

  public Double getScore() {
    return score;
  }

  public String getExplanationShort() {
    return explanationShort;
  }

  public MatchBreakdownResponse getMatchBreakdown() {
    return matchBreakdown;
  }
}
