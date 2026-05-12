package cl.duoc.worksi.dto.company;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CompanyProfileImageResponse {
  @JsonProperty("company_id")
  private final long companyId;

  @JsonProperty("image_url")
  private final String imageUrl;

  public CompanyProfileImageResponse(long companyId, String imageUrl) {
    this.companyId = companyId;
    this.imageUrl = imageUrl;
  }

  public long getCompanyId() {
    return companyId;
  }

  public String getImageUrl() {
    return imageUrl;
  }
}
