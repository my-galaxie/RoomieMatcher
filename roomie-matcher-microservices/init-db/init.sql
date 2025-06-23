-- Create databases
CREATE DATABASE roomie_auth;
CREATE DATABASE roomie_profile;
CREATE DATABASE roomie_match;

-- Connect to auth database and create schema
\c roomie_auth;

-- We don't need to create tables here as Flyway will handle that

-- Connect to profile database and create schema
\c roomie_profile;

-- We don't need to create tables here as Flyway will handle that

-- Connect to match database and create schema
\c roomie_match;

-- We don't need to create tables here as Flyway will handle that 