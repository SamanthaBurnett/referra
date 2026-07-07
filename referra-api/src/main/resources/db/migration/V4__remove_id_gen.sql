-- ================================================
-- Id generation should happen at the entity level
-- ================================================

ALTER TABLE users ALTER COLUMN id DROP DEFAULT;
ALTER TABLE user_roles ALTER COLUMN id DROP DEFAULT;
ALTER TABLE companies ALTER COLUMN id DROP DEFAULT;
ALTER TABLE candidate_profiles ALTER COLUMN id DROP DEFAULT;
ALTER TABLE referrer_profiles ALTER COLUMN id DROP DEFAULT;
ALTER TABLE resumes ALTER COLUMN id DROP DEFAULT;
ALTER TABLE referral_requests ALTER COLUMN id DROP DEFAULT;
ALTER TABLE feedback ALTER COLUMN id DROP DEFAULT;
ALTER TABLE reports ALTER COLUMN id DROP DEFAULT;
ALTER TABLE blocks ALTER COLUMN id DROP DEFAULT;
ALTER TABLE audit_logs ALTER COLUMN id DROP DEFAULT;
