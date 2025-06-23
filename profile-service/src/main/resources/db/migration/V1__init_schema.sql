CREATE TABLE IF NOT EXISTS tenant_profiles (
    id BIGSERIAL PRIMARY KEY,
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

CREATE TABLE IF NOT EXISTS tenant_preferred_genders (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT REFERENCES tenant_profiles(id) ON DELETE CASCADE,
    gender VARCHAR(50) NOT NULL
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_tenant_profiles_user_id ON tenant_profiles(user_id);
CREATE INDEX IF NOT EXISTS idx_tenant_profiles_location ON tenant_profiles(location);
CREATE INDEX IF NOT EXISTS idx_tenant_profiles_cleanliness ON tenant_profiles(cleanliness_level);
CREATE INDEX IF NOT EXISTS idx_tenant_profiles_noise ON tenant_profiles(noise_tolerance); 