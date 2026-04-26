package cl.duoc.worksi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "onboarding_candidate_sessions")
public class OnboardingCandidateSession {
    public enum Status {
        OPEN,
        COMPLETED,
        EXPIRED,
        CANCELLED
    }

    @Id
    @Column(name = "id", nullable = false, length = 36, columnDefinition = "CHAR(36)")
    private String id;

    @Column(name = "email", nullable = false, length = 190)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "middle_name", length = 100)
    private String middleName;

    @Column(name = "last_name_paternal", nullable = false, length = 100)
    private String lastNamePaternal;

    @Column(name = "last_name_maternal", nullable = false, length = 100)
    private String lastNameMaternal;

    @Column(name = "phone", nullable = false, length = 25)
    private String phone;

    @Column(name = "rut", nullable = false, length = 15)
    private String rut;

    @Column(name = "document_number", nullable = false, length = 40)
    private String documentNumber;

    @Column(name = "street", length = 160)
    private String street;

    @Column(name = "region_id", nullable = false)
    private Long regionId;

    @Column(name = "commune_id", nullable = false)
    private Long communeId;

    @Enumerated(EnumType.STRING)
    @Column(
        name = "status",
        nullable = false,
        columnDefinition = "ENUM('OPEN','COMPLETED','EXPIRED','CANCELLED')")
    private Status status;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastNamePaternal() {
        return lastNamePaternal;
    }

    public void setLastNamePaternal(String lastNamePaternal) {
        this.lastNamePaternal = lastNamePaternal;
    }

    public String getLastNameMaternal() {
        return lastNameMaternal;
    }

    public void setLastNameMaternal(String lastNameMaternal) {
        this.lastNameMaternal = lastNameMaternal;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}