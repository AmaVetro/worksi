package cl.duoc.worksi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "candidate_cvs")
public class CandidateCv {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "candidate_user_id", nullable = false)
  private Long candidateUserId;

  @Column(name = "original_filename", nullable = false, length = 255)
  private String originalFilename;

  @Column(name = "storage_path", nullable = false, length = 700)
  private String storagePath;

  @Column(name = "file_size_bytes", nullable = false)
  private int fileSizeBytes;

  @Column(name = "mime_type", nullable = false, length = 100)
  private String mimeType;

  @Column(name = "file_sha256", columnDefinition = "CHAR(64)")
  private String fileSha256;

  @JdbcTypeCode(SqlTypes.LONGVARCHAR)
  @Column(name = "extracted_text", columnDefinition = "MEDIUMTEXT")
  private String extractedText;

  @JdbcTypeCode(SqlTypes.LONGVARCHAR)
  @Column(name = "normalized_text", columnDefinition = "MEDIUMTEXT")
  private String normalizedText;

  @Column(name = "is_current", nullable = false)
  private boolean current;

  @Column(name = "uploaded_at", nullable = false)
  private LocalDateTime uploadedAt;

  @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
  private LocalDateTime updatedAt;

  protected CandidateCv() {}

  public Long getId() {
    return id;
  }

  public Long getCandidateUserId() {
    return candidateUserId;
  }

  public String getOriginalFilename() {
    return originalFilename;
  }

  public String getStoragePath() {
    return storagePath;
  }

  public int getFileSizeBytes() {
    return fileSizeBytes;
  }

  public String getMimeType() {
    return mimeType;
  }

  public String getFileSha256() {
    return fileSha256;
  }

  public String getExtractedText() {
    return extractedText;
  }

  public String getNormalizedText() {
    return normalizedText;
  }

  public boolean isCurrent() {
    return current;
  }

  public LocalDateTime getUploadedAt() {
    return uploadedAt;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }
}
