package cl.duoc.worksi.dto.ai;

public class MatchApiResponse {
  private double score;
  private String explanation;

  public double getScore() {
    return score;
  }

  public void setScore(double score) {
    this.score = score;
  }

  public String getExplanation() {
    return explanation;
  }

  public void setExplanation(String explanation) {
    this.explanation = explanation;
  }
}
