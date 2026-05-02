package cl.duoc.worksi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "recruiter_profiles")
public class RecruiterProfile {
  @Id
  @Column(name = "user_id")
  private Long userId;

  @Column(name = "company_id", nullable = false)
  private Long companyId;

  @Column(name = "first_name", nullable = false, length = 100)
  private String firstName;

  @Column(name = "last_name_paternal", nullable = false, length = 100)
  private String lastNamePaternal;

  @Column(name = "last_name_maternal", nullable = false, length = 100)
  private String lastNameMaternal;

  @Column(nullable = false, length = 15)
  private String rut;

  @Column(length = 25)
  private String phone;

  @Column(nullable = false, length = 25)
  private String mobile;

  @Column(name = "birth_date", nullable = false)
  private LocalDate birthDate;

  @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
  private LocalDateTime updatedAt;

  public RecruiterProfile() {}

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public void setCompanyId(Long companyId) {
    this.companyId = companyId;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public void setLastNamePaternal(String lastNamePaternal) {
    this.lastNamePaternal = lastNamePaternal;
  }

  public void setLastNameMaternal(String lastNameMaternal) {
    this.lastNameMaternal = lastNameMaternal;
  }

  public void setRut(String rut) {
    this.rut = rut;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public void setBirthDate(LocalDate birthDate) {
    this.birthDate = birthDate;
  }

  public Long getUserId() {
    return userId;
  }

  public Long getCompanyId() {
    return companyId;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastNamePaternal() {
    return lastNamePaternal;
  }

  public String getLastNameMaternal() {
    return lastNameMaternal;
  }

  public String getRut() {
    return rut;
  }

  public String getPhone() {
    return phone;
  }

  public String getMobile() {
    return mobile;
  }

  public LocalDate getBirthDate() {
    return birthDate;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }
}
