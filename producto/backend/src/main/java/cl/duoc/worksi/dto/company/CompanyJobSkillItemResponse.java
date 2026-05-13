package cl.duoc.worksi.dto.company;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CompanyJobSkillItemResponse {
  private final long id;
  private final String name;

  public CompanyJobSkillItemResponse(long id, String name) {
    this.id = id;
    this.name = name;
  }

  @JsonProperty("id")
  public long getId() {
    return id;
  }

  @JsonProperty("name")
  public String getName() {
    return name;
  }
}
