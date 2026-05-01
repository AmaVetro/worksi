SET @db := DATABASE();

SET @cnt := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'companies' AND COLUMN_NAME = 'address');
SET @stmt := IF(@cnt = 0, 'ALTER TABLE companies ADD COLUMN address VARCHAR(280) NULL AFTER commune_id', 'SELECT 1');
PREPARE p FROM @stmt; EXECUTE p; DEALLOCATE PREPARE p;

UPDATE companies SET commercial_name = '-' WHERE commercial_name IS NULL OR TRIM(commercial_name) = '';
UPDATE companies SET legal_name = '-' WHERE legal_name IS NULL OR TRIM(legal_name) = '';
UPDATE companies SET phone = '-' WHERE phone IS NULL OR TRIM(phone) = '';
UPDATE companies SET address = '-' WHERE address IS NULL OR TRIM(address) = '';

ALTER TABLE companies MODIFY COLUMN commercial_name VARCHAR(180) NOT NULL;
ALTER TABLE companies MODIFY COLUMN legal_name VARCHAR(220) NOT NULL;
ALTER TABLE companies MODIFY COLUMN phone VARCHAR(25) NOT NULL;
ALTER TABLE companies MODIFY COLUMN address VARCHAR(280) NOT NULL;

SET @cnt := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'jobs' AND COLUMN_NAME = 'company_commercial_name');
SET @stmt := IF(@cnt = 0, 'ALTER TABLE jobs ADD COLUMN company_commercial_name VARCHAR(180) NULL AFTER company_id', 'SELECT 1');
PREPARE p FROM @stmt; EXECUTE p; DEALLOCATE PREPARE p;

UPDATE jobs j
INNER JOIN companies c ON c.id = j.company_id
SET j.company_commercial_name = c.commercial_name
WHERE j.company_commercial_name IS NULL;

UPDATE jobs SET company_commercial_name = '-' WHERE company_commercial_name IS NULL OR TRIM(company_commercial_name) = '';

ALTER TABLE jobs MODIFY COLUMN company_commercial_name VARCHAR(180) NOT NULL;
