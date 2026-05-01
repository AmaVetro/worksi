SET @db := DATABASE();

SET @cnt := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'company_profiles' AND COLUMN_NAME = 'contact_full_name');
SET @stmt := IF(@cnt > 0, 'ALTER TABLE company_profiles DROP COLUMN contact_full_name', 'SELECT 1');
PREPARE p FROM @stmt; EXECUTE p; DEALLOCATE PREPARE p;

SET @cnt := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'company_profiles' AND COLUMN_NAME = 'annual_vacancies_approx');
SET @stmt := IF(@cnt > 0, 'ALTER TABLE company_profiles DROP COLUMN annual_vacancies_approx', 'SELECT 1');
PREPARE p FROM @stmt; EXECUTE p; DEALLOCATE PREPARE p;
