package cl.duoc.worksi.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AdminCompanyListItem {
  @JsonProperty("company_id")
  private final long companyId;

  @JsonProperty("commercial_name")
  private final String commercialName;

  @JsonProperty("legal_name")
  private final String legalName;

  @JsonProperty("rut")
  private final String rut;

  @JsonProperty("corporate_email")
  private final String corporateEmail;

  @JsonProperty("phone")
  private final String phone;

  @JsonProperty("region_name")
  private final String regionName;

  @JsonProperty("commune_name")
  private final String communeName;

  @JsonProperty("sector_name")
  private final String sectorName;

  public AdminCompanyListItem(
      long companyId,
      String commercialName,
      String legalName,
      String rut,
      String corporateEmail,
      String phone,
      String regionName,
      String communeName,
      String sectorName) {
    this.companyId = companyId;
    this.commercialName = commercialName;
    this.legalName = legalName;
    this.rut = rut;
    this.corporateEmail = corporateEmail;
    this.phone = phone;
    this.regionName = regionName;
    this.communeName = communeName;
    this.sectorName = sectorName;
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

  public String getCorporateEmail() {
    return corporateEmail;
  }

  public String getPhone() {
    return phone;
  }

  public String getRegionName() {
    return regionName;
  }

  public String getCommuneName() {
    return communeName;
  }

  public String getSectorName() {
    return sectorName;
  }
}
