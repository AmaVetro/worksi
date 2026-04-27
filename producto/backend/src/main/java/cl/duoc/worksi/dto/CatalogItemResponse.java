package cl.duoc.worksi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CatalogItemResponse {
  @JsonProperty("id")
  private final long id;

  @JsonProperty("code")
  private final String code;

  @JsonProperty("name")
  private final String name;

  public CatalogItemResponse(long id, String code, String name) {
    this.id = id;
    this.code = code;
    this.name = name;
  }

  public long getId() {
    return id;
  }

  public String getCode() {
    return code;
  }

  public String getName() {
    return name;
  }
}
