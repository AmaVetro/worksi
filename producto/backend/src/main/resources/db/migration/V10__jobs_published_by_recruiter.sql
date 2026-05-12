ALTER TABLE jobs
  ADD COLUMN published_by_user_id BIGINT UNSIGNED NULL AFTER company_id;

ALTER TABLE jobs
  ADD CONSTRAINT fk_jobs_published_by_user
  FOREIGN KEY (published_by_user_id) REFERENCES users(id)
  ON UPDATE CASCADE
  ON DELETE RESTRICT;

CREATE INDEX idx_jobs_published_by_user ON jobs (published_by_user_id);
