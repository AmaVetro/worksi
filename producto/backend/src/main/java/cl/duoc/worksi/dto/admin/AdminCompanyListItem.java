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

  public AdminCompanyListItem(
      long companyId, String commercialName, String legalName, String rut) {
    this.companyId = companyId;
    this.commercialName = commercialName;
    this.legalName = legalName;
    this.rut = rut;
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
}
