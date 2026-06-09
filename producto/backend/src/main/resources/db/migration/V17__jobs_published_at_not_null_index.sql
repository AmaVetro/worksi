UPDATE jobs
SET published_at = created_at
WHERE published_at IS NULL;

ALTER TABLE jobs
  MODIFY published_at DATETIME NOT NULL;

CREATE INDEX idx_jobs_published_by_user_published_at
  ON jobs (published_by_user_id, published_at);
