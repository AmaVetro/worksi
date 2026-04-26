CREATE TABLE regions (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  code VARCHAR(10) NOT NULL,
  name VARCHAR(120) NOT NULL,
  is_active TINYINT(1) NOT NULL DEFAULT 1,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_regions_code (code),
  UNIQUE KEY uk_regions_name (name)
) ENGINE=InnoDB;

CREATE TABLE communes (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  region_id BIGINT UNSIGNED NOT NULL,
  code VARCHAR(10) NOT NULL,
  name VARCHAR(120) NOT NULL,
  is_active TINYINT(1) NOT NULL DEFAULT 1,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_communes_id_region (id, region_id),
  UNIQUE KEY uk_communes_region_code (region_id, code),
  UNIQUE KEY uk_communes_region_name (region_id, name),
  KEY idx_communes_region (region_id),
  CONSTRAINT fk_communes_region
    FOREIGN KEY (region_id) REFERENCES regions(id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE sectors (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  code VARCHAR(30) NOT NULL,
  name VARCHAR(120) NOT NULL,
  is_active TINYINT(1) NOT NULL DEFAULT 1,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_sectors_code (code),
  UNIQUE KEY uk_sectors_name (name)
) ENGINE=InnoDB;

CREATE TABLE skills (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  code VARCHAR(60) NOT NULL,
  name VARCHAR(120) NOT NULL,
  is_active TINYINT(1) NOT NULL DEFAULT 1,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_skills_code (code),
  UNIQUE KEY uk_skills_name (name)
) ENGINE=InnoDB;

CREATE TABLE sector_skills (
  sector_id BIGINT UNSIGNED NOT NULL,
  skill_id BIGINT UNSIGNED NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (sector_id, skill_id),
  KEY idx_sector_skills_skill (skill_id),
  CONSTRAINT fk_sector_skills_sector
    FOREIGN KEY (sector_id) REFERENCES sectors(id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT fk_sector_skills_skill
    FOREIGN KEY (skill_id) REFERENCES skills(id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE users (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  role ENUM('CANDIDATE','COMPANY') NOT NULL,
  email VARCHAR(190) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  is_active TINYINT(1) NOT NULL DEFAULT 1,
  failed_login_attempts SMALLINT UNSIGNED NOT NULL DEFAULT 0,
  lock_until DATETIME NULL,
  last_login_at DATETIME NULL,
  password_reset_required TINYINT(1) NOT NULL DEFAULT 0,
  password_reset_requested_at DATETIME NULL,
  password_reset_code_hash VARCHAR(255) NULL,
  password_reset_code_expires_at DATETIME NULL,
  password_changed_at DATETIME NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_users_email (email),
  KEY idx_users_role (role),
  KEY idx_users_active (is_active),
  KEY idx_users_lock_until (lock_until)
) ENGINE=InnoDB;

CREATE TABLE onboarding_candidate_sessions (
  id CHAR(36) NOT NULL,
  email VARCHAR(190) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  first_name VARCHAR(100) NOT NULL,
  middle_name VARCHAR(100) NULL,
  last_name_paternal VARCHAR(100) NOT NULL,
  last_name_maternal VARCHAR(100) NOT NULL,
  phone VARCHAR(25) NOT NULL,
  rut VARCHAR(15) NOT NULL,
  document_number VARCHAR(40) NOT NULL,
  street VARCHAR(160) NULL,
  region_id BIGINT UNSIGNED NOT NULL,
  commune_id BIGINT UNSIGNED NOT NULL,
  status ENUM('OPEN','COMPLETED','EXPIRED','CANCELLED') NOT NULL DEFAULT 'OPEN',
  expires_at DATETIME NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_onboarding_candidate_sessions_email (email),
  KEY idx_onboarding_candidate_sessions_status (status),
  KEY idx_onboarding_candidate_sessions_expires_at (expires_at),
  KEY idx_onboarding_candidate_sessions_commune_region (commune_id, region_id),
  CONSTRAINT fk_onboarding_candidate_sessions_region
    FOREIGN KEY (region_id) REFERENCES regions(id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT fk_onboarding_candidate_sessions_commune_region
    FOREIGN KEY (commune_id, region_id) REFERENCES communes(id, region_id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE onboarding_candidate_profiles (
  onboarding_id CHAR(36) NOT NULL,
  sector_id BIGINT UNSIGNED NULL,
  profile_summary TEXT NULL,
  salary_expected_min INT UNSIGNED NULL,
  salary_expected_max INT UNSIGNED NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (onboarding_id),
  KEY idx_onboarding_candidate_profiles_sector (sector_id),
  CONSTRAINT fk_onboarding_candidate_profiles_onboarding
    FOREIGN KEY (onboarding_id) REFERENCES onboarding_candidate_sessions(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  CONSTRAINT fk_onboarding_candidate_profiles_sector
    FOREIGN KEY (sector_id) REFERENCES sectors(id)
    ON UPDATE CASCADE
    ON DELETE SET NULL,
  CONSTRAINT chk_onboarding_candidate_salary_range
    CHECK (
      (salary_expected_min IS NULL AND salary_expected_max IS NULL)
      OR (salary_expected_min IS NOT NULL AND salary_expected_max IS NOT NULL AND salary_expected_min <= salary_expected_max)
    )
) ENGINE=InnoDB;

CREATE TABLE onboarding_candidate_preferred_modalities (
  onboarding_id CHAR(36) NOT NULL,
  modality ENUM('REMOTE','HYBRID','ONSITE') NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (onboarding_id, modality),
  CONSTRAINT fk_onboarding_candidate_pref_modalities_onboarding
    FOREIGN KEY (onboarding_id) REFERENCES onboarding_candidate_sessions(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE onboarding_candidate_preferred_workloads (
  onboarding_id CHAR(36) NOT NULL,
  workload ENUM('FULL_TIME','PART_TIME','OTHER') NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (onboarding_id, workload),
  CONSTRAINT fk_onboarding_candidate_pref_workloads_onboarding
    FOREIGN KEY (onboarding_id) REFERENCES onboarding_candidate_sessions(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE onboarding_candidate_skills (
  onboarding_id CHAR(36) NOT NULL,
  skill_id BIGINT UNSIGNED NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (onboarding_id, skill_id),
  KEY idx_onboarding_candidate_skills_skill (skill_id),
  CONSTRAINT fk_onboarding_candidate_skills_onboarding
    FOREIGN KEY (onboarding_id) REFERENCES onboarding_candidate_sessions(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  CONSTRAINT fk_onboarding_candidate_skills_skill
    FOREIGN KEY (skill_id) REFERENCES skills(id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE onboarding_candidate_cvs (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  onboarding_id CHAR(36) NOT NULL,
  original_filename VARCHAR(255) NOT NULL,
  storage_path VARCHAR(700) NOT NULL,
  file_size_bytes INT UNSIGNED NOT NULL,
  mime_type VARCHAR(100) NOT NULL DEFAULT 'application/pdf',
  file_sha256 CHAR(64) NULL,
  extracted_text MEDIUMTEXT NULL,
  normalized_text MEDIUMTEXT NULL,
  is_current TINYINT(1) NOT NULL DEFAULT 1,
  current_onboarding_id CHAR(36)
    GENERATED ALWAYS AS (CASE WHEN is_current = 1 THEN onboarding_id ELSE NULL END) STORED,
  uploaded_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_onboarding_candidate_cvs_onboarding (onboarding_id),
  KEY idx_onboarding_candidate_cvs_onboarding_current (onboarding_id, is_current),
  UNIQUE KEY uk_onboarding_candidate_cvs_one_current (current_onboarding_id),
  KEY idx_onboarding_candidate_cvs_uploaded_at (uploaded_at),
  CONSTRAINT fk_onboarding_candidate_cvs_onboarding
    FOREIGN KEY (onboarding_id) REFERENCES onboarding_candidate_sessions(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  CONSTRAINT chk_onboarding_candidate_cvs_pdf
    CHECK (LOWER(mime_type) = 'application/pdf'),
  CONSTRAINT chk_onboarding_candidate_cvs_max_1mb
    CHECK (file_size_bytes <= 1048576)
) ENGINE=InnoDB;

CREATE TABLE candidate_profiles (
  user_id BIGINT UNSIGNED NOT NULL,
  first_name VARCHAR(100) NOT NULL,
  middle_name VARCHAR(100) NULL,
  last_name_paternal VARCHAR(100) NOT NULL,
  last_name_maternal VARCHAR(100) NOT NULL,
  phone VARCHAR(25) NOT NULL,
  rut VARCHAR(15) NOT NULL,
  document_number VARCHAR(40) NOT NULL,
  street VARCHAR(160) NULL,
  region_id BIGINT UNSIGNED NOT NULL,
  commune_id BIGINT UNSIGNED NOT NULL,
  sector_id BIGINT UNSIGNED NULL,
  profile_summary TEXT NULL,
  salary_expected_min INT UNSIGNED NULL,
  salary_expected_max INT UNSIGNED NULL,
  consent_given TINYINT(1) NOT NULL DEFAULT 0,
  consent_given_at DATETIME NULL,
  onboarding_completed TINYINT(1) NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (user_id),
  UNIQUE KEY uk_candidate_profiles_rut (rut),
  KEY idx_candidate_profiles_region (region_id),
  KEY idx_candidate_profiles_commune (commune_id),
  KEY idx_candidate_profiles_commune_region (commune_id, region_id),
  KEY idx_candidate_profiles_sector (sector_id),
  KEY idx_candidate_profiles_consent (consent_given),
  CONSTRAINT fk_candidate_profiles_user
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  CONSTRAINT fk_candidate_profiles_region
    FOREIGN KEY (region_id) REFERENCES regions(id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT fk_candidate_profiles_commune_region
    FOREIGN KEY (commune_id, region_id) REFERENCES communes(id, region_id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT fk_candidate_profiles_sector
    FOREIGN KEY (sector_id) REFERENCES sectors(id)
    ON UPDATE CASCADE
    ON DELETE SET NULL,
  CONSTRAINT chk_candidate_salary_range
    CHECK (
      (salary_expected_min IS NULL AND salary_expected_max IS NULL)
      OR (salary_expected_min IS NOT NULL AND salary_expected_max IS NOT NULL AND salary_expected_min <= salary_expected_max)
    ),
  CONSTRAINT chk_candidate_consent_consistency
    CHECK (
      (consent_given = 0 AND consent_given_at IS NULL)
      OR (consent_given = 1 AND consent_given_at IS NOT NULL)
    )
) ENGINE=InnoDB;

CREATE TABLE candidate_preferred_modalities (
  candidate_user_id BIGINT UNSIGNED NOT NULL,
  modality ENUM('REMOTE','HYBRID','ONSITE') NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (candidate_user_id, modality),
  CONSTRAINT fk_candidate_pref_modalities_candidate
    FOREIGN KEY (candidate_user_id) REFERENCES candidate_profiles(user_id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE candidate_preferred_workloads (
  candidate_user_id BIGINT UNSIGNED NOT NULL,
  workload ENUM('FULL_TIME','PART_TIME','OTHER') NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (candidate_user_id, workload),
  CONSTRAINT fk_candidate_pref_workloads_candidate
    FOREIGN KEY (candidate_user_id) REFERENCES candidate_profiles(user_id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE company_profiles (
  user_id BIGINT UNSIGNED NOT NULL,
  contact_full_name VARCHAR(180) NOT NULL,
  phone VARCHAR(25) NOT NULL,
  commercial_name VARCHAR(180) NOT NULL,
  legal_name VARCHAR(220) NOT NULL,
  rut VARCHAR(15) NOT NULL,
  region_id BIGINT UNSIGNED NOT NULL,
  commune_id BIGINT UNSIGNED NOT NULL,
  sector_id BIGINT UNSIGNED NOT NULL,
  worker_count_approx INT UNSIGNED NOT NULL,
  annual_vacancies_approx INT UNSIGNED NOT NULL,
  image_url VARCHAR(500) NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (user_id),
  UNIQUE KEY uk_company_profiles_rut (rut),
  KEY idx_company_profiles_region (region_id),
  KEY idx_company_profiles_commune (commune_id),
  KEY idx_company_profiles_commune_region (commune_id, region_id),
  KEY idx_company_profiles_sector (sector_id),
  CONSTRAINT fk_company_profiles_user
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  CONSTRAINT fk_company_profiles_region
    FOREIGN KEY (region_id) REFERENCES regions(id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT fk_company_profiles_commune_region
    FOREIGN KEY (commune_id, region_id) REFERENCES communes(id, region_id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT fk_company_profiles_sector
    FOREIGN KEY (sector_id) REFERENCES sectors(id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE candidate_skills (
  candidate_user_id BIGINT UNSIGNED NOT NULL,
  skill_id BIGINT UNSIGNED NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (candidate_user_id, skill_id),
  KEY idx_candidate_skills_skill (skill_id),
  CONSTRAINT fk_candidate_skills_candidate
    FOREIGN KEY (candidate_user_id) REFERENCES candidate_profiles(user_id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  CONSTRAINT fk_candidate_skills_skill
    FOREIGN KEY (skill_id) REFERENCES skills(id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE candidate_cvs (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  candidate_user_id BIGINT UNSIGNED NOT NULL,
  original_filename VARCHAR(255) NOT NULL,
  storage_path VARCHAR(700) NOT NULL,
  file_size_bytes INT UNSIGNED NOT NULL,
  mime_type VARCHAR(100) NOT NULL DEFAULT 'application/pdf',
  file_sha256 CHAR(64) NULL,
  extracted_text MEDIUMTEXT NULL,
  normalized_text MEDIUMTEXT NULL,
  is_current TINYINT(1) NOT NULL DEFAULT 1,
  current_candidate_user_id BIGINT UNSIGNED
    GENERATED ALWAYS AS (CASE WHEN is_current = 1 THEN candidate_user_id ELSE NULL END) STORED,
  uploaded_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_candidate_cvs_candidate (candidate_user_id),
  KEY idx_candidate_cvs_candidate_current (candidate_user_id, is_current),
  UNIQUE KEY uk_candidate_cvs_one_current (current_candidate_user_id),
  KEY idx_candidate_cvs_uploaded_at (uploaded_at),
  CONSTRAINT fk_candidate_cvs_candidate
    FOREIGN KEY (candidate_user_id) REFERENCES candidate_profiles(user_id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  CONSTRAINT chk_candidate_cvs_pdf
    CHECK (LOWER(mime_type) = 'application/pdf'),
  CONSTRAINT chk_candidate_cvs_max_1mb
    CHECK (file_size_bytes <= 1048576)
) ENGINE=InnoDB;

CREATE TABLE jobs (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  company_user_id BIGINT UNSIGNED NOT NULL,
  title VARCHAR(180) NOT NULL,
  description TEXT NOT NULL,
  requirements TEXT NOT NULL,
  city VARCHAR(120) NOT NULL,
  region_id BIGINT UNSIGNED NOT NULL,
  commune_id BIGINT UNSIGNED NOT NULL,
  salary_offered INT UNSIGNED NULL,
  years_experience_required TINYINT UNSIGNED NULL,
  modality ENUM('REMOTE','HYBRID','ONSITE') NOT NULL,
  workload ENUM('FULL_TIME','PART_TIME','OTHER') NOT NULL,
  image_url VARCHAR(500) NULL,
  status ENUM('ACTIVE') NOT NULL DEFAULT 'ACTIVE',
  published_at DATETIME NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_jobs_company (company_user_id),
  KEY idx_jobs_status (status),
  KEY idx_jobs_region (region_id),
  KEY idx_jobs_commune (commune_id),
  KEY idx_jobs_commune_region (commune_id, region_id),
  KEY idx_jobs_modality (modality),
  KEY idx_jobs_workload (workload),
  KEY idx_jobs_created_at (created_at),
  CONSTRAINT fk_jobs_company
    FOREIGN KEY (company_user_id) REFERENCES company_profiles(user_id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT fk_jobs_region
    FOREIGN KEY (region_id) REFERENCES regions(id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT fk_jobs_commune_region
    FOREIGN KEY (commune_id, region_id) REFERENCES communes(id, region_id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE job_skills (
  job_id BIGINT UNSIGNED NOT NULL,
  skill_id BIGINT UNSIGNED NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (job_id, skill_id),
  KEY idx_job_skills_skill (skill_id),
  CONSTRAINT fk_job_skills_job
    FOREIGN KEY (job_id) REFERENCES jobs(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  CONSTRAINT fk_job_skills_skill
    FOREIGN KEY (skill_id) REFERENCES skills(id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE applications (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  candidate_user_id BIGINT UNSIGNED NOT NULL,
  job_id BIGINT UNSIGNED NOT NULL,
  status ENUM('APPLIED','VIEWED','CANCELLED') NOT NULL DEFAULT 'APPLIED',
  viewed_at DATETIME NULL,
  cancelled_at DATETIME NULL,
  match_score DECIMAL(5,2) NULL,
  match_explanation VARCHAR(500) NULL,
  matched_at DATETIME NULL,
  applied_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_applications_candidate_job (candidate_user_id, job_id),
  KEY idx_applications_job (job_id),
  KEY idx_applications_candidate (candidate_user_id),
  KEY idx_applications_status (status),
  KEY idx_applications_job_score (job_id, match_score),
  KEY idx_applications_applied_at (applied_at),
  CONSTRAINT fk_applications_candidate
    FOREIGN KEY (candidate_user_id) REFERENCES candidate_profiles(user_id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  CONSTRAINT fk_applications_job
    FOREIGN KEY (job_id) REFERENCES jobs(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  CONSTRAINT chk_applications_status_dates
    CHECK (
      (status <> 'VIEWED' OR viewed_at IS NOT NULL)
      AND (status <> 'CANCELLED' OR cancelled_at IS NOT NULL)
    ),
  CONSTRAINT chk_applications_score_range
    CHECK (match_score IS NULL OR (match_score >= 0.00 AND match_score <= 100.00))
) ENGINE=InnoDB;

CREATE TABLE candidate_job_swipes (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    candidate_user_id BIGINT UNSIGNED NOT NULL,
    job_id BIGINT UNSIGNED NOT NULL,
    action ENUM('PASS','APPLY') NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_candidate_job_swipes_unique (candidate_user_id, job_id),
    KEY idx_candidate_job_swipes_job (job_id),
    KEY idx_candidate_job_swipes_action (action),
    CONSTRAINT fk_candidate_job_swipes_candidate
        FOREIGN KEY (candidate_user_id) REFERENCES candidate_profiles(user_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_candidate_job_swipes_job
        FOREIGN KEY (job_id) REFERENCES jobs(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE saved_jobs (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    candidate_user_id BIGINT UNSIGNED NOT NULL,
    job_id BIGINT UNSIGNED NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_saved_jobs_candidate_job (candidate_user_id, job_id),
    KEY idx_saved_jobs_job (job_id),
    CONSTRAINT fk_saved_jobs_candidate
        FOREIGN KEY (candidate_user_id) REFERENCES candidate_profiles(user_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_saved_jobs_job
        FOREIGN KEY (job_id) REFERENCES jobs(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
) ENGINE=InnoDB;

