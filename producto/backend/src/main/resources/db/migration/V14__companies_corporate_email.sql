ALTER TABLE companies ADD COLUMN corporate_email VARCHAR(254) NULL;
UPDATE companies SET corporate_email = CONCAT('legacy-company-', id, '@placeholder.worksi') WHERE corporate_email IS NULL;
ALTER TABLE companies MODIFY COLUMN corporate_email VARCHAR(254) NOT NULL;
