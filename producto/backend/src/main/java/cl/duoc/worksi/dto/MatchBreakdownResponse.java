package cl.duoc.worksi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MatchBreakdownResponse {
  @JsonProperty("final_score")
  private final Double finalScore;

  @JsonProperty("description_score")
  private final Double descriptionScore;

  @JsonProperty("title_score")
  private final Double titleScore;

  @JsonProperty("modality_score")
  private final Double modalityScore;

  @JsonProperty("workload_score")
  private final Double workloadScore;

  @JsonProperty("experience_score")
  private final Double experienceScore;

  public MatchBreakdownResponse(
      Double finalScore,
      Double descriptionScore,
      Double titleScore,
      Double modalityScore,
      Double workloadScore,
      Double experienceScore) {
    this.finalScore = finalScore;
    this.descriptionScore = descriptionScore;
    this.titleScore = titleScore;
    this.modalityScore = modalityScore;
    this.workloadScore = workloadScore;
    this.experienceScore = experienceScore;
  }

  public Double getFinalScore() {
    return finalScore;
  }

  public Double getDescriptionScore() {
    return descriptionScore;
  }

  public Double getTitleScore() {
    return titleScore;
  }

  public Double getModalityScore() {
    return modalityScore;
  }

  public Double getWorkloadScore() {
    return workloadScore;
  }

  public Double getExperienceScore() {
    return experienceScore;
  }
}
