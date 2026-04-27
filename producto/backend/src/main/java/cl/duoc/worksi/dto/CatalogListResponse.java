package cl.duoc.worksi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class CatalogListResponse {
  @JsonProperty("items")
  private final List<CatalogItemResponse> items;

  public CatalogListResponse(List<CatalogItemResponse> items) {
    this.items = items;
  }

  public List<CatalogItemResponse> getItems() {
    return items;
  }
}
