package cl.duoc.worksi.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AdminCompanyDataRequest {
  @JsonProperty("commercial_name")
  private String commercialName;

  @JsonProperty("legal_name")
  private String legalName;

  @JsonProperty("phone")
  private String phone;

  @JsonProperty("address")
  private String address;

  @JsonProperty("rut")
  private String rut;

  @JsonProperty("region_id")
  private Long regionId;

  @JsonProperty("commune_id")
  private Long communeId;

  @JsonProperty("sector_id")
  private Long sectorId;

  @JsonProperty("worker_count_approx")
  private Integer workerCountApprox;

  public String getCommercialName() {
    return commercialName;
  }

  public void setCommercialName(String commercialName) {
    this.commercialName = commercialName;
  }

  public String getLegalName() {
    return legalName;
  }

  public void setLegalName(String legalName) {
    this.legalName = legalName;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getRut() {
    return rut;
  }

  public void setRut(String rut) {
    this.rut = rut;
  }

  public Long getRegionId() {
    return regionId;
  }

  public void setRegionId(Long regionId) {
    this.regionId = regionId;
  }

  public Long getCommuneId() {
    return communeId;
  }

  public void setCommuneId(Long communeId) {
    this.communeId = communeId;
  }

  public Long getSectorId() {
    return sectorId;
  }

  public void setSectorId(Long sectorId) {
    this.sectorId = sectorId;
  }

  public Integer getWorkerCountApprox() {
    return workerCountApprox;
  }

  public void setWorkerCountApprox(Integer workerCountApprox) {
    this.workerCountApprox = workerCountApprox;
  }
}
