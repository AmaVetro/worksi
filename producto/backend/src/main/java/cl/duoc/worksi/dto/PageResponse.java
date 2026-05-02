package cl.duoc.worksi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class PageResponse<T> {
  @JsonProperty("items")
  private final List<T> items;

  @JsonProperty("page")
  private final int page;

  @JsonProperty("size")
  private final int size;

  @JsonProperty("total_items")
  private final long totalItems;

  @JsonProperty("total_pages")
  private final int totalPages;

  public PageResponse(List<T> items, int page, int size, long totalItems, int totalPages) {
    this.items = items;
    this.page = page;
    this.size = size;
    this.totalItems = totalItems;
    this.totalPages = totalPages;
  }

  public List<T> getItems() {
    return items;
  }

  public int getPage() {
    return page;
  }

  public int getSize() {
    return size;
  }

  public long getTotalItems() {
    return totalItems;
  }

  public int getTotalPages() {
    return totalPages;
  }
}
