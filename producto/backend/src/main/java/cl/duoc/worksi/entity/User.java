package cl.duoc.worksi.entity;

import cl.duoc.worksi.entity.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private UserRole role;

  @Column(nullable = false, unique = true, length = 190)
  private String email;

  @Column(name = "password_hash", nullable = false, length = 255)
  private String passwordHash;

  @Column(name = "is_active", nullable = false)
  private boolean active;

  @JdbcTypeCode(SqlTypes.SMALLINT)
  @Column(name = "failed_login_attempts", nullable = false)
  private int failedLoginAttempts;

  @Column(name = "lock_until")
  private LocalDateTime lockUntil;

  @Column(name = "last_login_at")
  private LocalDateTime lastLoginAt;

  @Column(name = "password_reset_required", nullable = false)
  private boolean passwordResetRequired;

  @Column(name = "password_reset_requested_at")
  private LocalDateTime passwordResetRequestedAt;

  @Column(name = "password_reset_code_hash", length = 255)
  private String passwordResetCodeHash;

  @Column(name = "password_reset_code_expires_at")
  private LocalDateTime passwordResetCodeExpiresAt;

  @Column(name = "password_changed_at")
  private LocalDateTime passwordChangedAt;

  @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
  private LocalDateTime updatedAt;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  protected User() {}

  public Long getId() {
    return id;
  }

  public UserRole getRole() {
    return role;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public boolean isActive() {
    return active;
  }

  public int getFailedLoginAttempts() {
    return failedLoginAttempts;
  }

  public LocalDateTime getLockUntil() {
    return lockUntil;
  }

  public LocalDateTime getLastLoginAt() {
    return lastLoginAt;
  }

  public boolean isPasswordResetRequired() {
    return passwordResetRequired;
  }

  public LocalDateTime getPasswordResetRequestedAt() {
    return passwordResetRequestedAt;
  }

  public String getPasswordResetCodeHash() {
    return passwordResetCodeHash;
  }

  public LocalDateTime getPasswordResetCodeExpiresAt() {
    return passwordResetCodeExpiresAt;
  }

  public LocalDateTime getPasswordChangedAt() {
    return passwordChangedAt;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public LocalDateTime getDeletedAt() {
    return deletedAt;
  }
}
