-- Delete existing data to prevent duplicates
DELETE FROM users;

-- Insert sample tenants with different preferences
-- Delete existing data to prevent duplicates
DELETE FROM users;

-- Insert sample tenants with low compatibility to the main tenant (ID=1)
INSERT INTO users (id, name, email, password, role, budget, location, smoking, pets, cleanliness_level, noise_tolerance) VALUES
(1, 'Amit Sharma', 'amit.sharma@example.com', 'password123', 'TENANT', 15000, 'Bangalore', FALSE, TRUE, 4, 3),
(2, 'Neha Verma', 'neha.verma@example.com', 'securepass', 'TENANT', 18000, 'Mumbai', TRUE, FALSE, 5, 2),
(3, 'Rohan Iyer', 'rohan.iyer@example.com', 'rohanpass', 'TENANT', 14000, 'Pune', FALSE, FALSE, 3, 4),
(4, 'Priya Das', 'priya.das@example.com', 'priya789', 'TENANT', 20000, 'Delhi', TRUE, TRUE, 4, 3),
(5, 'Vikas Nair', 'vikas.nair@example.com', 'vikaspass', 'TENANT', 17000, 'Hyderabad', FALSE, FALSE, 5, 2),
(6, 'Ananya Reddy', 'ananya.reddy@example.com', 'ananyapass', 'TENANT', 16000, 'Chennai', TRUE, TRUE, 3, 4),
(7, 'Rajesh Kulkarni', 'rajesh.kulkarni@example.com', 'kulkarni@123', 'TENANT', 19000, 'Pune', FALSE, TRUE, 4, 3),
(8, 'Sneha Patil', 'sneha.patil@example.com', 'snehapatil', 'TENANT', 21000, 'Mumbai', TRUE, FALSE, 5, 2),
(9, 'Arjun Mehta', 'arjun.mehta@example.com', 'arjun@456', 'TENANT', 15500, 'Delhi', FALSE, FALSE, 3, 4),
(10, 'Kavya Rao', 'kavya.rao@example.com', 'kavya321', 'TENANT', 17500, 'Bangalore', TRUE, TRUE, 4, 3),
(11, 'Manoj Gupta', 'manoj.gupta@example.com', 'manoj@789', 'TENANT', 14500, 'Chennai', FALSE, FALSE, 4, 3),
(12, 'Pooja Singh', 'pooja.singh@example.com', 'pooja@123', 'TENANT', 19500, 'Hyderabad', TRUE, TRUE, 5, 2),
(13, 'Aditya Joshi', 'aditya.joshi@example.com', 'adityajoshi', 'TENANT', 18500, 'Bangalore', FALSE, TRUE, 3, 4),
(14, 'Meera Krishnan', 'meera.krishnan@example.com', 'meerak', 'TENANT', 16500, 'Pune', TRUE, FALSE, 4, 3),
(15, 'Sandeep Chatterjee', 'sandeep.chatterjee@example.com', 'sandeepc', 'TENANT', 22000, 'Mumbai', FALSE, TRUE, 5, 2);
