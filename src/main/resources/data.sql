-- Insert sample tenants with different preferences only if they don't exist
INSERT INTO users (name, email, password, role, budget, location, smoking, pets, cleanliness_level, noise_tolerance, gender, is_active)
SELECT 'Amit Sharma', 'amit.sharma@example.com', '$2a$10$rYMqPxp8JJm1xvVNPmJrAOSQWbKiYRZUESBnHDx/IKD3ZVQgbVp.e', 'TENANT', 15000, 'Bangalore', FALSE, TRUE, 4, 3, 'Male', TRUE
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'amit.sharma@example.com');

INSERT INTO users (name, email, password, role, budget, location, smoking, pets, cleanliness_level, noise_tolerance, gender, is_active)
SELECT 'Neha Verma', 'neha.verma@example.com', '$2a$10$rYMqPxp8JJm1xvVNPmJrAOSQWbKiYRZUESBnHDx/IKD3ZVQgbVp.e', 'TENANT', 18000, 'Mumbai', TRUE, FALSE, 5, 2, 'Female', TRUE
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'neha.verma@example.com');

-- Insert sample users with encrypted passwords (password is 'password') only if they don't exist
INSERT INTO users (name, email, password, role, budget, location, cleanliness_level, noise_tolerance, smoking, pets, gender, is_active)
SELECT 'John Doe', 'john@example.com', '$2a$10$rYMqPxp8JJm1xvVNPmJrAOSQWbKiYRZUESBnHDx/IKD3ZVQgbVp.e', 'TENANT', 15000, 'Bangalore', 4, 3, false, true, 'Male', TRUE
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'john@example.com');

INSERT INTO users (name, email, password, role, budget, location, cleanliness_level, noise_tolerance, smoking, pets, gender, is_active)
SELECT 'Jane Smith', 'jane@example.com', '$2a$10$rYMqPxp8JJm1xvVNPmJrAOSQWbKiYRZUESBnHDx/IKD3ZVQgbVp.e', 'TENANT', 18000, 'Bangalore', 5, 2, false, false, 'Female', TRUE
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'jane@example.com');

-- Insert preferred genders for tenants only if they don't exist
INSERT INTO tenant_preferred_genders (tenant_id, preferred_gender)
SELECT u.id, 'Male'
FROM users u
WHERE u.email = 'neha.verma@example.com'
AND NOT EXISTS (
    SELECT 1 FROM tenant_preferred_genders tpg 
    WHERE tpg.tenant_id = u.id AND tpg.preferred_gender = 'Male'
);

INSERT INTO tenant_preferred_genders (tenant_id, preferred_gender)
SELECT u.id, 'Female'
FROM users u
WHERE u.email = 'amit.sharma@example.com'
AND NOT EXISTS (
    SELECT 1 FROM tenant_preferred_genders tpg 
    WHERE tpg.tenant_id = u.id AND tpg.preferred_gender = 'Female'
);

-- Insert sample testimonials only if they don't exist
INSERT INTO testimonials (author_name, content, rating, display_order)
SELECT 'Rahul Mehta', 'RoomieMatcher helped me find the perfect roommate in just a week! We have similar cleanliness habits and both love quiet evenings. Couldn''t be happier with my living situation now.', 5, 1
WHERE NOT EXISTS (SELECT 1 FROM testimonials WHERE author_name = 'Rahul Mehta');

INSERT INTO testimonials (author_name, content, rating, display_order)
SELECT 'Anjali Desai', 'After struggling to find a compatible roommate for months, RoomieMatcher matched me with someone who has the same budget and lifestyle preferences. The gender preference filter was especially helpful!', 5, 2
WHERE NOT EXISTS (SELECT 1 FROM testimonials WHERE author_name = 'Anjali Desai');

-- Sample matches will be generated automatically by the application logic
