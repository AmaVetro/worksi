package cl.duoc.worksi.dto.candidate;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CandidateProfileSkillResponse {
  @JsonProperty("id")
  private final long id;

  @JsonProperty("name")
  private final String name;

  public CandidateProfileSkillResponse(long id, String name) {
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
