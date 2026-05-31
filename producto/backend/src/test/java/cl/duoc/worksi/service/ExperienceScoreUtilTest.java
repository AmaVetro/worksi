package cl.duoc.worksi.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExperienceScoreUtilTest {

  @Test
  void fullScoreWhenCandidateMeetsOrExceedsRequired() {
    assertEquals(100.0, ExperienceScoreUtil.compute(4, 4));
    assertEquals(100.0, ExperienceScoreUtil.compute(6, 4));
  }

  @Test
  void tieredScoresByRatio() {
    assertEquals(75.0, ExperienceScoreUtil.compute(3, 4));
    assertEquals(50.0, ExperienceScoreUtil.compute(2, 4));
    assertEquals(25.0, ExperienceScoreUtil.compute(1, 4));
    assertEquals(0.0, ExperienceScoreUtil.compute(0, 4));
  }

  @Test
  void fullScoreWhenJobRequiresZeroYears() {
    assertEquals(100.0, ExperienceScoreUtil.compute(0, 0));
  }
}
