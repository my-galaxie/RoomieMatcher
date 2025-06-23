-- Create databases
CREATE DATABASE auth_db;
CREATE DATABASE profile_db;
CREATE DATABASE match_db;
CREATE DATABASE notification_db;

-- Connect to auth_db and create schema
\c auth_db;

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE user_roles (
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE otp_verification (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    email VARCHAR(100) NOT NULL,
    otp VARCHAR(6) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Insert default roles
INSERT INTO roles (name) VALUES ('ROLE_USER');
INSERT INTO roles (name) VALUES ('ROLE_ADMIN');

-- Connect to profile_db and create schema
\c profile_db;

CREATE TABLE tenant_profiles (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    budget FLOAT,
    location VARCHAR(255),
    cleanliness_level INTEGER,
    noise_tolerance INTEGER,
    smoking BOOLEAN,
    pets BOOLEAN,
    gender VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE tenant_preferred_genders (
    id SERIAL PRIMARY KEY,
    tenant_id BIGINT REFERENCES tenant_profiles(id) ON DELETE CASCADE,
    gender VARCHAR(50) NOT NULL
);

-- Connect to match_db and create schema
\c match_db;

CREATE TABLE matches (
    id SERIAL PRIMARY KEY,
    tenant1_id BIGINT NOT NULL,
    tenant2_id BIGINT NOT NULL,
    match_score FLOAT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Connect to notification_db and create schema
\c notification_db;

CREATE TABLE notifications (
    id SERIAL PRIMARY KEY,
    recipient_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    read BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE email_notifications (
    id SERIAL PRIMARY KEY,
    recipient_email VARCHAR(255) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    body TEXT NOT NULL,
    sent BOOLEAN NOT NULL DEFAULT false,
    sent_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Create indexes for better performance
\c auth_db;
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_otp_verification_email ON otp_verification(email);

\c profile_db;
CREATE INDEX idx_tenant_profiles_user_id ON tenant_profiles(user_id);
CREATE INDEX idx_tenant_profiles_location ON tenant_profiles(location);
CREATE INDEX idx_tenant_profiles_cleanliness ON tenant_profiles(cleanliness_level);
CREATE INDEX idx_tenant_profiles_noise ON tenant_profiles(noise_tolerance);

\c match_db;
CREATE INDEX idx_matches_tenant1 ON matches(tenant1_id);
CREATE INDEX idx_matches_tenant2 ON matches(tenant2_id);
CREATE INDEX idx_matches_score ON matches(match_score);

\c notification_db;
CREATE INDEX idx_notifications_recipient_id ON notifications(recipient_id);
CREATE INDEX idx_email_notifications_recipient_email ON email_notifications(recipient_email); 