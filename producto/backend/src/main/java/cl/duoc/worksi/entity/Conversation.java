package cl.duoc.worksi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Entity
@Table(name = "conversations")
public class Conversation {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "application_id", nullable = false)
  private Long applicationId;

  @Column(name = "recruiter_user_id", nullable = false)
  private Long recruiterUserId;

  @Column(name = "candidate_user_id", nullable = false)
  private Long candidateUserId;

  @Column(name = "company_id", nullable = false)
  private Long companyId;

  @Column(name = "job_id", nullable = false)
  private Long jobId;

  @Column(name = "login_notice_dismissed", nullable = false)
  private boolean loginNoticeDismissed;

  @Column(name = "recruiter_last_read_message_id", nullable = false)
  private long recruiterLastReadMessageId;

  @Column(name = "candidate_last_read_message_id", nullable = false)
  private long candidateLastReadMessageId;

  @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false, insertable = false)
  private LocalDateTime updatedAt;

  protected Conversation() {}

  public static Conversation create(
      long applicationId,
      long recruiterUserId,
      long candidateUserId,
      long companyId,
      long jobId) {
    Conversation c = new Conversation();
    c.applicationId = applicationId;
    c.recruiterUserId = recruiterUserId;
    c.candidateUserId = candidateUserId;
    c.companyId = companyId;
    c.jobId = jobId;
    c.loginNoticeDismissed = false;
    c.recruiterLastReadMessageId = 0L;
    c.candidateLastReadMessageId = 0L;
    c.updatedAt = LocalDateTime.now(ZoneOffset.UTC);
    return c;
  }

  public Long getId() {
    return id;
  }

  public Long getApplicationId() {
    return applicationId;
  }

  public Long getRecruiterUserId() {
    return recruiterUserId;
  }

  public Long getCandidateUserId() {
    return candidateUserId;
  }

  public Long getCompanyId() {
    return companyId;
  }

  public Long getJobId() {
    return jobId;
  }

  public boolean isLoginNoticeDismissed() {
    return loginNoticeDismissed;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void dismissLoginNotice() {
    loginNoticeDismissed = true;
  }

  public void touchUpdated() {
    updatedAt = LocalDateTime.now(ZoneOffset.UTC);
  }

  public long getRecruiterLastReadMessageId() {
    return recruiterLastReadMessageId;
  }

  public void markRecruiterRead(long messageId) {
    if (messageId > recruiterLastReadMessageId) {
      recruiterLastReadMessageId = messageId;
    }
  }

  public long getCandidateLastReadMessageId() {
    return candidateLastReadMessageId;
  }

  public void markCandidateRead(long messageId) {
    if (messageId > candidateLastReadMessageId) {
      candidateLastReadMessageId = messageId;
    }
  }
}
