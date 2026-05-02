package cl.duoc.worksi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "companies")
public class Company {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 25)
  private String phone;

  @Column(name = "commercial_name", nullable = false, length = 180)
  private String commercialName;

  @Column(name = "legal_name", nullable = false, length = 220)
  private String legalName;

  @Column(nullable = false, length = 15)
  private String rut;

  @Column(name = "region_id", nullable = false)
  private Long regionId;

  @Column(name = "commune_id", nullable = false)
  private Long communeId;

  @Column(nullable = false, length = 280)
  private String address;

  @Column(name = "sector_id", nullable = false)
  private Long sectorId;

  @Column(name = "worker_count_approx", nullable = false)
  private int workerCountApprox;

  @Column(name = "image_url", length = 500)
  private String imageUrl;

  @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
  private LocalDateTime updatedAt;

  public Company() {}

  public Long getId() {
    return id;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public void setCommercialName(String commercialName) {
    this.commercialName = commercialName;
  }

  public void setLegalName(String legalName) {
    this.legalName = legalName;
  }

  public void setRut(String rut) {
    this.rut = rut;
  }

  public void setRegionId(Long regionId) {
    this.regionId = regionId;
  }

  public void setCommuneId(Long communeId) {
    this.communeId = communeId;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public void setSectorId(Long sectorId) {
    this.sectorId = sectorId;
  }

  public void setWorkerCountApprox(int workerCountApprox) {
    this.workerCountApprox = workerCountApprox;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public String getPhone() {
    return phone;
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

  public Long getRegionId() {
    return regionId;
  }

  public Long getCommuneId() {
    return communeId;
  }

  public String getAddress() {
    return address;
  }

  public Long getSectorId() {
    return sectorId;
  }

  public int getWorkerCountApprox() {
    return workerCountApprox;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }
}
