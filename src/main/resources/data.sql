-- Delete existing data to prevent duplicates
DELETE FROM users;

-- Insert sample tenants with different preferences
-- Make sure the IDs are high enough to avoid conflicts with auto-generated IDs
INSERT INTO users (id, name, email, password, role, budget, location, smoking, pets, cleanliness_level, noise_tolerance, is_active, gender) VALUES
(1001, 'Amit Sharma', 'amit.sharma@example.com', 'password123', 'TENANT', 15000, 'Bangalore', FALSE, TRUE, 4, 3, TRUE, 'Male'),
(1002, 'Neha Verma', 'neha.verma@example.com', 'securepass', 'TENANT', 18000, 'Mumbai', TRUE, FALSE, 5, 2, TRUE, 'Female'),
(1003, 'Rohan Iyer', 'rohan.iyer@example.com', 'rohanpass', 'TENANT', 14000, 'Pune', FALSE, FALSE, 3, 4, TRUE, 'Male'),
(1004, 'Priya Das', 'priya.das@example.com', 'priya789', 'TENANT', 20000, 'Delhi', TRUE, TRUE, 4, 3, TRUE, 'Female'),
(1005, 'Vikas Nair', 'vikas.nair@example.com', 'vikaspass', 'TENANT', 17000, 'Hyderabad', FALSE, FALSE, 5, 2, TRUE, 'Male'),
(1006, 'Ananya Reddy', 'ananya.reddy@example.com', 'ananyapass', 'TENANT', 16000, 'Chennai', TRUE, TRUE, 3, 4, TRUE, 'Female'),
(1007, 'Rajesh Kulkarni', 'rajesh.kulkarni@example.com', 'kulkarni@123', 'TENANT', 19000, 'Pune', FALSE, TRUE, 4, 3, TRUE, 'Male'),
(1008, 'Sneha Patil', 'sneha.patil@example.com', 'snehapatil', 'TENANT', 21000, 'Mumbai', TRUE, FALSE, 5, 2, TRUE, 'Female'),
(1009, 'Arjun Mehta', 'arjun.mehta@example.com', 'arjun@456', 'TENANT', 15500, 'Delhi', FALSE, FALSE, 3, 4, TRUE, 'Male'),
(1010, 'Kavya Rao', 'kavya.rao@example.com', 'kavya321', 'TENANT', 17500, 'Bangalore', TRUE, TRUE, 4, 3, TRUE, 'Female'),
(1011, 'Manoj Gupta', 'manoj.gupta@example.com', 'manoj@789', 'TENANT', 14500, 'Chennai', FALSE, FALSE, 4, 3, TRUE, 'Male'),
(1012, 'Pooja Singh', 'pooja.singh@example.com', 'pooja@123', 'TENANT', 19500, 'Hyderabad', TRUE, TRUE, 5, 2, TRUE, 'Female'),
(1013, 'Aditya Joshi', 'aditya.joshi@example.com', 'adityajoshi', 'TENANT', 18500, 'Bangalore', FALSE, TRUE, 3, 4, TRUE, 'Male'),
(1014, 'Meera Krishnan', 'meera.krishnan@example.com', 'meerak', 'TENANT', 16500, 'Pune', TRUE, FALSE, 4, 3, TRUE, 'Female'),
(1015, 'Sandeep Chatterjee', 'sandeep.chatterjee@example.com', 'sandeepc', 'TENANT', 22000, 'Mumbai', FALSE, TRUE, 5, 2, TRUE, 'Male'),
-- Additional diverse sample data
(1016, 'Riya Patel', 'riya.patel@example.com', 'riyap123', 'TENANT', 16800, 'Ahmedabad', FALSE, TRUE, 4, 2, TRUE, 'Female'),
(1017, 'Karan Malhotra', 'karan.malhotra@example.com', 'karan456', 'TENANT', 19200, 'Delhi', TRUE, FALSE, 3, 5, TRUE, 'Male'),
(1018, 'Nisha Kapoor', 'nisha.kapoor@example.com', 'nisha789', 'TENANT', 17300, 'Jaipur', FALSE, TRUE, 5, 3, TRUE, 'Female'),
(1019, 'Vikram Singh', 'vikram.singh@example.com', 'vikram123', 'TENANT', 21500, 'Chandigarh', TRUE, TRUE, 4, 4, TRUE, 'Male'),
(1020, 'Anjali Desai', 'anjali.desai@example.com', 'anjali456', 'TENANT', 15800, 'Surat', FALSE, FALSE, 5, 2, TRUE, 'Female'),
(1021, 'Rahul Sharma', 'rahul.sharma@example.com', 'rahul789', 'TENANT', 18200, 'Lucknow', TRUE, FALSE, 3, 3, TRUE, 'Male'),
(1022, 'Divya Choudhary', 'divya.choudhary@example.com', 'divya123', 'TENANT', 16200, 'Indore', FALSE, TRUE, 4, 5, TRUE, 'Female'),
(1023, 'Sanjay Kumar', 'sanjay.kumar@example.com', 'sanjay456', 'TENANT', 20500, 'Bhopal', TRUE, TRUE, 5, 4, TRUE, 'Male'),
(1024, 'Preeti Agarwal', 'preeti.agarwal@example.com', 'preeti789', 'TENANT', 17800, 'Kolkata', FALSE, FALSE, 3, 2, TRUE, 'Female'),
(1025, 'Deepak Verma', 'deepak.verma@example.com', 'deepak123', 'TENANT', 19800, 'Nagpur', TRUE, FALSE, 4, 3, TRUE, 'Male'),
(1026, 'Shreya Mishra', 'shreya.mishra@example.com', 'shreya456', 'TENANT', 16000, 'Goa', FALSE, TRUE, 5, 5, TRUE, 'Female'),
(1027, 'Alok Tiwari', 'alok.tiwari@example.com', 'alok789', 'TENANT', 22500, 'Noida', TRUE, TRUE, 3, 4, TRUE, 'Male'),
(1028, 'Neeti Saxena', 'neeti.saxena@example.com', 'neeti123', 'TENANT', 17200, 'Gurgaon', FALSE, FALSE, 4, 2, TRUE, 'Female'),
(1029, 'Prakash Jha', 'prakash.jha@example.com', 'prakash456', 'TENANT', 18800, 'Patna', TRUE, FALSE, 5, 3, TRUE, 'Male'),
(1030, 'Shweta Khanna', 'shweta.khanna@example.com', 'shweta789', 'TENANT', 15500, 'Dehradun', FALSE, TRUE, 3, 5, TRUE, 'Female'),
-- Non-binary and other gender identities for diversity
(1031, 'Alex Sharma', 'alex.sharma@example.com', 'alex123', 'TENANT', 19000, 'Bangalore', FALSE, TRUE, 4, 3, TRUE, 'Non-binary'),
(1032, 'Sam Patel', 'sam.patel@example.com', 'sam456', 'TENANT', 17500, 'Mumbai', TRUE, FALSE, 5, 4, TRUE, 'Non-binary'),
(1033, 'Jordan Mehta', 'jordan.mehta@example.com', 'jordan789', 'TENANT', 20000, 'Delhi', FALSE, TRUE, 3, 2, TRUE, 'Other'),
(1034, 'Taylor Gupta', 'taylor.gupta@example.com', 'taylor123', 'TENANT', 18200, 'Pune', TRUE, FALSE, 4, 5, TRUE, 'Other');

-- Reset the sequence to start after our sample data
SELECT setval('users_id_seq', 1100, true);
