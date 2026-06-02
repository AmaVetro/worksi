package cl.duoc.worksi.dto.company;

import cl.duoc.worksi.dto.MatchBreakdownResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

public class CompanyJobApplicationItemResponse {
  @JsonProperty("application_id")
  private final long applicationId;

  @JsonProperty("candidate_user_id")
  private final long candidateUserId;

  private final String status;

  @JsonProperty("applied_at")
  private final Instant appliedAt;

  @JsonProperty("viewed_at")
  private final Instant viewedAt;

  @JsonProperty("match_score")
  private final Double matchScore;

  @JsonProperty("match_explanation")
  private final String matchExplanation;

  @JsonProperty("match_breakdown")
  private final MatchBreakdownResponse matchBreakdown;

  @JsonProperty("candidate_preview")
  private final CompanyApplicationCandidatePreviewResponse candidatePreview;

  public CompanyJobApplicationItemResponse(
      long applicationId,
      long candidateUserId,
      String status,
      Instant appliedAt,
      Instant viewedAt,
      Double matchScore,
      String matchExplanation,
      MatchBreakdownResponse matchBreakdown,
      CompanyApplicationCandidatePreviewResponse candidatePreview) {
    this.applicationId = applicationId;
    this.candidateUserId = candidateUserId;
    this.status = status;
    this.appliedAt = appliedAt;
    this.viewedAt = viewedAt;
    this.matchScore = matchScore;
    this.matchExplanation = matchExplanation;
    this.matchBreakdown = matchBreakdown;
    this.candidatePreview = candidatePreview;
  }

  public long getApplicationId() {
    return applicationId;
  }

  public long getCandidateUserId() {
    return candidateUserId;
  }

  public String getStatus() {
    return status;
  }

  public Instant getAppliedAt() {
    return appliedAt;
  }

  public Instant getViewedAt() {
    return viewedAt;
  }

  public Double getMatchScore() {
    return matchScore;
  }

  public String getMatchExplanation() {
    return matchExplanation;
  }

  public MatchBreakdownResponse getMatchBreakdown() {
    return matchBreakdown;
  }

  public CompanyApplicationCandidatePreviewResponse getCandidatePreview() {
    return candidatePreview;
  }
}
