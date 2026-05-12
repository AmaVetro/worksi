package cl.duoc.worksi.dto.candidate;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CandidateJobSkillPreviewResponse {
  private final long id;
  private final String name;

  public CandidateJobSkillPreviewResponse(long id, String name) {
    this.id = id;
    this.name = name;
  }

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }
}
