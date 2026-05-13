SET @db := DATABASE();
SET @exists := (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'jobs' AND COLUMN_NAME = 'corporate_email'
);
SET @stmt := IF(@exists > 0, 'ALTER TABLE jobs DROP COLUMN corporate_email', 'SELECT 1');
PREPARE ps FROM @stmt;
EXECUTE ps;
DEALLOCATE PREPARE ps;
