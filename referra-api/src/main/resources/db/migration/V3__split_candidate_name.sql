-- ========================================
-- Split candidate name
-- ========================================

ALTER TABLE candidate_profiles
ADD COLUMN first_name VARCHAR(100) NOT NULL;

ALTER TABLE candidate_profiles
ADD COLUMN last_name VARCHAR(100) NOT NULL;

ALTER TABLE candidate_profiles
DROP COLUMN full_name;
