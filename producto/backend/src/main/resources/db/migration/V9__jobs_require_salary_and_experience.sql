UPDATE jobs SET salary_offered = 0 WHERE salary_offered IS NULL;
UPDATE jobs SET years_experience_required = 0 WHERE years_experience_required IS NULL;

ALTER TABLE jobs MODIFY COLUMN salary_offered INT UNSIGNED NOT NULL;
ALTER TABLE jobs MODIFY COLUMN years_experience_required TINYINT UNSIGNED NOT NULL;
