CREATE TABLE recruiter_profiles (
  user_id BIGINT UNSIGNED NOT NULL,
  company_user_id BIGINT UNSIGNED NOT NULL,
  first_name VARCHAR(100) NOT NULL,
  last_name_paternal VARCHAR(100) NOT NULL,
  last_name_maternal VARCHAR(100) NOT NULL,
  rut VARCHAR(15) NOT NULL,
  phone VARCHAR(25) NULL,
  mobile VARCHAR(25) NOT NULL,
  birth_date DATE NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (user_id),
  UNIQUE KEY uk_recruiter_profiles_rut (rut),
  KEY idx_recruiter_profiles_company (company_user_id),
  CONSTRAINT fk_recruiter_profiles_user
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  CONSTRAINT fk_recruiter_profiles_company
    FOREIGN KEY (company_user_id) REFERENCES company_profiles(user_id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
) ENGINE=InnoDB;
