USE apexbank_account_db;

INSERT INTO account_opening_requests
(title, first_name, middle_name, last_name, father_name, mobile_number, email, aadhar_number, date_of_birth,
 residential_address_line1, residential_address_line2, residential_landmark, residential_state, residential_city, residential_pincode,
 permanent_address_line1, permanent_address_line2, permanent_landmark, permanent_state, permanent_city, permanent_pincode,
 occupation_type, source_of_income, gross_annual_income, wants_debit_card, opt_for_net_banking, status)
VALUES
('Mr', 'Arjun', 'K', 'Rao', 'Krishna Rao', '9876543210', 'arjun.rao@example.com', '123456789012', '1995-05-14',
 'Flat 302, Sunrise Apartments', 'MG Road', 'Near City Mall', 'Telangana', 'Hyderabad', '500081',
 'Flat 302, Sunrise Apartments', 'MG Road', 'Near City Mall', 'Telangana', 'Hyderabad', '500081',
 'Salaried', 'Salary', '10-15 LPA', TRUE, TRUE, 'APPROVED'),
('Ms', 'Priya', '', 'Sharma', 'Ramesh Sharma', '9123456780', 'priya.sharma@example.com', '234567890123', '1998-09-22',
 'House No 12, Green Valley', 'Sector 5', 'Opp Park', 'Karnataka', 'Bengaluru', '560034',
 'House No 12, Green Valley', 'Sector 5', 'Opp Park', 'Karnataka', 'Bengaluru', '560034',
 'Self Employed', 'Business', '5-10 LPA', TRUE, TRUE, 'PENDING');

INSERT INTO accounts
(account_number, request_id, title, first_name, middle_name, last_name, mobile_number, email, aadhar_number, date_of_birth,
 residential_address_line1, residential_address_line2, residential_landmark, residential_state, residential_city, residential_pincode,
 permanent_address_line1, permanent_address_line2, permanent_landmark, permanent_state, permanent_city, permanent_pincode,
 occupation_type, account_type, balance, has_debit_card, net_banking_enabled, status)
VALUES
('100000000001', 1, 'Mr', 'Arjun', 'K', 'Rao', '9876543210', 'arjun.rao@example.com', '123456789012', '1995-05-14',
 'Flat 302, Sunrise Apartments', 'MG Road', 'Near City Mall', 'Telangana', 'Hyderabad', '500081',
 'Flat 302, Sunrise Apartments', 'MG Road', 'Near City Mall', 'Telangana', 'Hyderabad', '500081',
 'Salaried', 'SAVINGS', 50000.00, TRUE, TRUE, 'ACTIVE'),
('100000000002', NULL, 'Mr', 'Rahul', '', 'Verma', '9988776655', 'rahul.verma@example.com', '345678901234', '1990-01-30',
 'Plot 45, Lakeview Colony', 'Jubilee Hills', '', 'Telangana', 'Hyderabad', '500033',
 'Plot 45, Lakeview Colony', 'Jubilee Hills', '', 'Telangana', 'Hyderabad', '500033',
 'Salaried', 'SAVINGS', 75000.00, TRUE, TRUE, 'ACTIVE');

INSERT INTO upi_ids (upi_id, account_id, active) VALUES
('9876543210@apex', 1, TRUE),
('9988776655@apex', 2, TRUE);
