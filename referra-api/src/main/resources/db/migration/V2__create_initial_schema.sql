CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TYPE user_status AS ENUM (
  'ACTIVE',
  'DISABLED',
  'DELETED'
);

CREATE TYPE user_role AS ENUM (
  'CANDIDATE',
  'REFERRER',
  'ADMIN'
);

CREATE TYPE referral_status AS ENUM (
  'OPEN',
  'LIMITED',
  'PAUSED',
  'CLOSED'
);

CREATE TYPE resume_upload_status AS ENUM (
  'PENDING',
  'UPLOADED',
  'FAILED'
);

CREATE TYPE referral_request_status AS ENUM (
  'PENDING',
  'ACCEPTED',
  'DECLINED',
  'COMPLETED',
  'REFERRED',
  'NOT_REFERRED',
  'EXPIRED',
  'CANCELED'
);

CREATE TYPE report_status AS ENUM (
  'OPEN',
  'REVIEWED',
  'DISMISSED',
  'ACTION_TAKEN'
);

CREATE TYPE report_reason AS ENUM (
  'SPAM',
  'HARASSMENT',
  'FAKE_PROFILE',
  'INAPPROPRIATE_BEHAVIOR',
  'SCAM',
  'OTHER'
);

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cognito_sub VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    status user_status NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE user_roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role user_role NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_user_role UNIQUE (user_id, role)
);

CREATE TABLE companies (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) UNIQUE NOT NULL,
    domain VARCHAR(255),
    website_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE candidate_profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    full_name VARCHAR(255) NOT NULL,
    headline VARCHAR(255),
    skills TEXT[] NOT NULL DEFAULT '{}',
    target_roles TEXT[] NOT NULL DEFAULT '{}',
    linkedin_url VARCHAR(500),
    github_url VARCHAR(500),
    portfolio_url VARCHAR(500),
    has_references_available BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE referrer_profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    company_id UUID NOT NULL REFERENCES companies(id),
    job_title VARCHAR(255) NOT NULL,
    seniority VARCHAR(100),
    skills TEXT[] NOT NULL DEFAULT '{}',
    bio TEXT,
    referral_status referral_status NOT NULL DEFAULT 'OPEN',
    max_requests_per_week INTEGER NOT NULL DEFAULT 5,
    last_responded_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_max_requests_per_week CHECK (max_requests_per_week >= 0)
);

CREATE TABLE resumes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    candidate_profile_id UUID UNIQUE NOT NULL REFERENCES candidate_profiles(id) ON DELETE CASCADE,
    s3_key VARCHAR(500) NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    file_size_bytes BIGINT NOT NULL,
    upload_status resume_upload_status NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_resume_file_size CHECK (file_size_bytes > 0 AND file_size_bytes <= 5242880),
    CONSTRAINT chk_resume_content_type CHECK (content_type = 'application/pdf')
);

CREATE TABLE referral_requests (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    candidate_profile_id UUID NOT NULL REFERENCES candidate_profiles(id),
    referrer_profile_id UUID NOT NULL REFERENCES referrer_profiles(id),
    company_id UUID NOT NULL REFERENCES companies(id),
    resume_id UUID NOT NULL REFERENCES resumes(id),
    target_role VARCHAR(255) NOT NULL,
    message TEXT,
    status referral_request_status NOT NULL DEFAULT 'PENDING',
    accepted_at TIMESTAMP,
    completed_at TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE feedback (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    referral_request_id UUID UNIQUE NOT NULL REFERENCES referral_requests(id) ON DELETE CASCADE,
    strengths TEXT[] NOT NULL DEFAULT '{}',
    improvement_areas TEXT[] NOT NULL DEFAULT '{}',
    written_feedback TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE reports (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    reporter_user_id UUID NOT NULL REFERENCES users(id),
    reported_user_id UUID NOT NULL REFERENCES users(id),
    referral_request_id UUID REFERENCES referral_requests(id),
    reason report_reason NOT NULL,
    details TEXT,
    status report_status NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    reviewed_at TIMESTAMP,
    CONSTRAINT chk_report_not_self CHECK (reporter_user_id <> reported_user_id)
);

CREATE TABLE blocks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    blocker_user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    blocked_user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_block_not_self CHECK (blocker_user_id <> blocked_user_id),
    CONSTRAINT uq_block_pair UNIQUE (blocker_user_id, blocked_user_id)
);

CREATE TABLE audit_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    actor_user_id UUID REFERENCES users(id),
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(100) NOT NULL,
    entity_id UUID,
    metadata JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_cognito_sub ON users(cognito_sub);

CREATE INDEX idx_candidate_profiles_user_id ON candidate_profiles(user_id);

CREATE INDEX idx_referrer_profiles_user_id ON referrer_profiles(user_id);
CREATE INDEX idx_referrer_profiles_company_id ON referrer_profiles(company_id);
CREATE INDEX idx_referrer_profiles_status ON referrer_profiles(referral_status);

CREATE INDEX idx_referral_requests_candidate ON referral_requests(candidate_profile_id);
CREATE INDEX idx_referral_requests_referrer ON referral_requests(referrer_profile_id);
CREATE INDEX idx_referral_requests_status ON referral_requests(status);
CREATE INDEX idx_referral_requests_expires_at ON referral_requests(expires_at);

CREATE INDEX idx_reports_reporter ON reports(reporter_user_id);
CREATE INDEX idx_reports_reported ON reports(reported_user_id);
CREATE INDEX idx_reports_status ON reports(status);

CREATE INDEX idx_blocks_blocker ON blocks(blocker_user_id);
CREATE INDEX idx_blocks_blocked ON blocks(blocked_user_id);

CREATE INDEX idx_audit_logs_actor ON audit_logs(actor_user_id);
CREATE INDEX idx_audit_logs_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX idx_audit_logs_created_at ON audit_logs(created_at);
