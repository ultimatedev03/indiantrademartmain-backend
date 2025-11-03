-- V13: Create otp_verification table
-- For OTP-based authentication and verification

CREATE TABLE IF NOT EXISTS otp_verification (
    id BIGSERIAL PRIMARY KEY,
    email_or_phone VARCHAR(255) NOT NULL,
    otp VARCHAR(10) NOT NULL,
    expiry_time TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for better performance
CREATE INDEX IF NOT EXISTS idx_otp_email_or_phone ON otp_verification(email_or_phone);
CREATE INDEX IF NOT EXISTS idx_otp_expiry_time ON otp_verification(expiry_time);

-- Comments
COMMENT ON TABLE otp_verification IS 'OTP verification codes for authentication';
COMMENT ON COLUMN otp_verification.email_or_phone IS 'Email or phone number to verify';
COMMENT ON COLUMN otp_verification.otp IS 'One-time password code';
COMMENT ON COLUMN otp_verification.expiry_time IS 'When the OTP expires';
