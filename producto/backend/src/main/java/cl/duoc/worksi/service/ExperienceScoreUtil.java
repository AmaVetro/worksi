package cl.duoc.worksi.service;

final class ExperienceScoreUtil {
  private ExperienceScoreUtil() {}

  static double compute(int candidateYears, int requiredYears) {
    if (requiredYears <= 0) {
      return 100.0;
    }
    int safeCandidate = Math.max(0, candidateYears);
    double ratio = (double) safeCandidate / requiredYears;
    if (ratio >= 1.0) {
      return 100.0;
    }
    if (ratio >= 0.75) {
      return 75.0;
    }
    if (ratio >= 0.50) {
      return 50.0;
    }
    if (ratio >= 0.25) {
      return 25.0;
    }
    return 0.0;
  }
}
