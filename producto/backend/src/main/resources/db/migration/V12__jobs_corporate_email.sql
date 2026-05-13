ALTER TABLE jobs ADD COLUMN corporate_email VARCHAR(254) NULL;
UPDATE jobs SET corporate_email = CONCAT('legacy-job-', id, '@placeholder.worksi') WHERE corporate_email IS NULL;
ALTER TABLE jobs MODIFY COLUMN corporate_email VARCHAR(254) NOT NULL;
