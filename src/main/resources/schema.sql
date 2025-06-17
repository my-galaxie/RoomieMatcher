-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(31) NOT NULL,
    budget FLOAT,
    location VARCHAR(255),
    cleanliness_level INTEGER,
    noise_tolerance INTEGER,
    smoking BOOLEAN,
    pets BOOLEAN,
    gender VARCHAR(50),
    is_active BOOLEAN DEFAULT FALSE,
    verification_code VARCHAR(10),
    verification_expiry TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create tenant_preferred_genders table
CREATE TABLE IF NOT EXISTS tenant_preferred_genders (
    tenant_id BIGINT REFERENCES users(id),
    preferred_gender VARCHAR(50),
    PRIMARY KEY (tenant_id, preferred_gender)
);

-- Create match table
CREATE TABLE IF NOT EXISTS match (
    id SERIAL PRIMARY KEY,
    tenant1_id BIGINT REFERENCES users(id),
    tenant2_id BIGINT REFERENCES users(id),
    match_score FLOAT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create testimonials table
CREATE TABLE IF NOT EXISTS testimonials (
    id SERIAL PRIMARY KEY,
    author_name VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    rating INTEGER NOT NULL,
    display_order INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);



-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_match_tenant1 ON match(tenant1_id);
CREATE INDEX IF NOT EXISTS idx_match_tenant2 ON match(tenant2_id);
CREATE INDEX IF NOT EXISTS idx_users_gender ON users(gender);
CREATE INDEX IF NOT EXISTS idx_users_is_active ON users(is_active);

 