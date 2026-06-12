CREATE TABLE conversations (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  application_id BIGINT UNSIGNED NOT NULL,
  recruiter_user_id BIGINT UNSIGNED NOT NULL,
  candidate_user_id BIGINT UNSIGNED NOT NULL,
  company_id BIGINT UNSIGNED NOT NULL,
  job_id BIGINT UNSIGNED NOT NULL,
  login_notice_dismissed TINYINT(1) NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_conversations_application (application_id),
  UNIQUE KEY uk_conversations_recruiter_candidate (recruiter_user_id, candidate_user_id),
  KEY idx_conversations_candidate (candidate_user_id),
  KEY idx_conversations_recruiter (recruiter_user_id),
  KEY idx_conversations_updated (updated_at),
  CONSTRAINT fk_conversations_application
    FOREIGN KEY (application_id) REFERENCES applications(id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT fk_conversations_recruiter
    FOREIGN KEY (recruiter_user_id) REFERENCES users(id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT fk_conversations_candidate
    FOREIGN KEY (candidate_user_id) REFERENCES candidate_profiles(user_id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT fk_conversations_company
    FOREIGN KEY (company_id) REFERENCES companies(id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT fk_conversations_job
    FOREIGN KEY (job_id) REFERENCES jobs(id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE conversation_messages (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  conversation_id BIGINT UNSIGNED NOT NULL,
  sender_user_id BIGINT UNSIGNED NOT NULL,
  sender_role ENUM('CANDIDATE','RECRUITER') NOT NULL,
  body VARCHAR(500) NOT NULL,
  sent_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_conversation_messages_conversation (conversation_id),
  KEY idx_conversation_messages_poll (conversation_id, id),
  CONSTRAINT fk_conversation_messages_conversation
    FOREIGN KEY (conversation_id) REFERENCES conversations(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  CONSTRAINT fk_conversation_messages_sender
    FOREIGN KEY (sender_user_id) REFERENCES users(id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
) ENGINE=InnoDB;
