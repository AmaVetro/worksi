ALTER TABLE applications
  ADD COLUMN description_score DECIMAL(5,2) NULL AFTER match_explanation,
  ADD COLUMN title_score DECIMAL(5,2) NULL AFTER description_score,
  ADD COLUMN modality_score DECIMAL(5,2) NULL AFTER title_score,
  ADD COLUMN workload_score DECIMAL(5,2) NULL AFTER modality_score,
  ADD COLUMN experience_score DECIMAL(5,2) NULL AFTER workload_score;
