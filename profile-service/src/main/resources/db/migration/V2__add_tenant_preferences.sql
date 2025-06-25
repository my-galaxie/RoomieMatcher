-- Add new columns for tenant preferences
ALTER TABLE tenant_profiles ADD COLUMN IF NOT EXISTS daily_schedule VARCHAR(50);
ALTER TABLE tenant_profiles ADD COLUMN IF NOT EXISTS guest_frequency VARCHAR(50);
ALTER TABLE tenant_profiles ADD COLUMN IF NOT EXISTS cooking_habits VARCHAR(50);
ALTER TABLE tenant_profiles ADD COLUMN IF NOT EXISTS music_noise VARCHAR(50);
ALTER TABLE tenant_profiles ADD COLUMN IF NOT EXISTS cleaning_frequency VARCHAR(50);
ALTER TABLE tenant_profiles ADD COLUMN IF NOT EXISTS social_style VARCHAR(50);
ALTER TABLE tenant_profiles ADD COLUMN IF NOT EXISTS temperature_preference VARCHAR(50);
ALTER TABLE tenant_profiles ADD COLUMN IF NOT EXISTS lighting_preference VARCHAR(50);
ALTER TABLE tenant_profiles ADD COLUMN IF NOT EXISTS pet_compatibility VARCHAR(50);
ALTER TABLE tenant_profiles ADD COLUMN IF NOT EXISTS smoking_preference VARCHAR(50);
ALTER TABLE tenant_profiles ADD COLUMN IF NOT EXISTS parking_needs VARCHAR(50);
ALTER TABLE tenant_profiles ADD COLUMN IF NOT EXISTS overnight_guests VARCHAR(50);
ALTER TABLE tenant_profiles ADD COLUMN IF NOT EXISTS work_from_home VARCHAR(50);
ALTER TABLE tenant_profiles ADD COLUMN IF NOT EXISTS allergies VARCHAR(50);

-- Create indexes for better performance and searching
CREATE INDEX IF NOT EXISTS idx_tenant_profiles_daily_schedule ON tenant_profiles(daily_schedule);
CREATE INDEX IF NOT EXISTS idx_tenant_profiles_cleaning_frequency ON tenant_profiles(cleaning_frequency);
CREATE INDEX IF NOT EXISTS idx_tenant_profiles_social_style ON tenant_profiles(social_style);
CREATE INDEX IF NOT EXISTS idx_tenant_profiles_smoking_preference ON tenant_profiles(smoking_preference);
CREATE INDEX IF NOT EXISTS idx_tenant_profiles_pet_compatibility ON tenant_profiles(pet_compatibility); 