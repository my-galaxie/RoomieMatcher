CREATE TABLE IF NOT EXISTS matches (
    id BIGSERIAL PRIMARY KEY,
    tenant1_id BIGINT NOT NULL,
    tenant2_id BIGINT NOT NULL,
    match_score FLOAT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_matches_tenant1 ON matches(tenant1_id);
CREATE INDEX IF NOT EXISTS idx_matches_tenant2 ON matches(tenant2_id);
CREATE INDEX IF NOT EXISTS idx_matches_score ON matches(match_score); 