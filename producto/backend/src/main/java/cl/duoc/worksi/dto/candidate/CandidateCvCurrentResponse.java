package cl.duoc.worksi.dto.candidate;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

public class CandidateCvCurrentResponse {
  @JsonProperty("cv_id")
  private final long cvId;

  @JsonProperty("original_filename")
  private final String originalFilename;

  @JsonProperty("file_size_bytes")
  private final int fileSizeBytes;

  @JsonProperty("is_current")
  private final boolean current;

  @JsonProperty("uploaded_at")
  private final Instant uploadedAt;

  public CandidateCvCurrentResponse(
      long cvId,
      String originalFilename,
      int fileSizeBytes,
      boolean current,
      Instant uploadedAt) {
    this.cvId = cvId;
    this.originalFilename = originalFilename;
    this.fileSizeBytes = fileSizeBytes;
    this.current = current;
    this.uploadedAt = uploadedAt;
  }

  public long getCvId() {
    return cvId;
  }

  public String getOriginalFilename() {
    return originalFilename;
  }

  public int getFileSizeBytes() {
    return fileSizeBytes;
  }

  public boolean isCurrent() {
    return current;
  }

  public Instant getUploadedAt() {
    return uploadedAt;
  }
}
