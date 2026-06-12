package cl.duoc.worksi.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AdminCompanyDetailResponse {
  @JsonProperty("company_id")
  private final long companyId;

  @JsonProperty("commercial_name")
  private final String commercialName;

  @JsonProperty("legal_name")
  private final String legalName;

  @JsonProperty("rut")
  private final String rut;

  @JsonProperty("phone")
  private final String phone;

  @JsonProperty("corporate_email")
  private final String corporateEmail;

  @JsonProperty("address")
  private final String address;

  @JsonProperty("region_id")
  private final long regionId;

  @JsonProperty("commune_id")
  private final long communeId;

  @JsonProperty("sector_id")
  private final long sectorId;

  @JsonProperty("worker_count_approx")
  private final int workerCountApprox;

  @JsonProperty("has_image")
  private final boolean hasImage;

  public AdminCompanyDetailResponse(
      long companyId,
      String commercialName,
      String legalName,
      String rut,
      String phone,
      String corporateEmail,
      String address,
      long regionId,
      long communeId,
      long sectorId,
      int workerCountApprox,
      boolean hasImage) {
    this.companyId = companyId;
    this.commercialName = commercialName;
    this.legalName = legalName;
    this.rut = rut;
    this.phone = phone;
    this.corporateEmail = corporateEmail;
    this.address = address;
    this.regionId = regionId;
    this.communeId = communeId;
    this.sectorId = sectorId;
    this.workerCountApprox = workerCountApprox;
    this.hasImage = hasImage;
  }

  public long getCompanyId() {
    return companyId;
  }

  public String getCommercialName() {
    return commercialName;
  }

  public String getLegalName() {
    return legalName;
  }

  public String getRut() {
    return rut;
  }

  public String getPhone() {
    return phone;
  }

  public String getCorporateEmail() {
    return corporateEmail;
  }

  public String getAddress() {
    return address;
  }

  public long getRegionId() {
    return regionId;
  }

  public long getCommuneId() {
    return communeId;
  }

  public long getSectorId() {
    return sectorId;
  }

  public int getWorkerCountApprox() {
    return workerCountApprox;
  }

  public boolean isHasImage() {
    return hasImage;
  }
}
