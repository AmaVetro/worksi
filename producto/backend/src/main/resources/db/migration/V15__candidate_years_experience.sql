ALTER TABLE candidate_profiles
  ADD COLUMN years_experience TINYINT UNSIGNED NOT NULL DEFAULT 0
  AFTER salary_expected_max;
