package cl.duoc.worksi.dto.company;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CompanyProfileImagePatchRequest {
  @JsonProperty("image_url")
  private String imageUrl;

  public String getImageUrl() {
    return imageUrl;
  }
}
