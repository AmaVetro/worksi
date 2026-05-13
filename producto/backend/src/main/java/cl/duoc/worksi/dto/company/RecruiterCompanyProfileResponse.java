package cl.duoc.worksi.dto.company;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RecruiterCompanyProfileResponse {
  private final long companyId;

  private final String commercialName;

  private final String corporateEmail;

  private final String externalImageUrl;

  private final boolean hasProtectedImage;

  public RecruiterCompanyProfileResponse(
      long companyId,
      String commercialName,
      String corporateEmail,
      String externalImageUrl,
      boolean hasProtectedImage) {
    this.companyId = companyId;
    this.commercialName = commercialName;
    this.corporateEmail = corporateEmail;
    this.externalImageUrl = externalImageUrl;
    this.hasProtectedImage = hasProtectedImage;
  }

  @JsonProperty("company_id")
  public long getCompanyId() {
    return companyId;
  }

  @JsonProperty("commercial_name")
  public String getCommercialName() {
    return commercialName;
  }

  @JsonProperty("corporate_email")
  public String getCorporateEmail() {
    return corporateEmail;
  }

  @JsonProperty("external_image_url")
  public String getExternalImageUrl() {
    return externalImageUrl;
  }

  @JsonProperty("has_protected_image")
  public boolean isHasProtectedImage() {
    return hasProtectedImage;
  }
}
