USE apexbank_auth_db;

-- Insert test admin user
-- Admin Login Credentials: 
-- User ID: admin123
-- Password: admin123  
-- BCrypt hash for 'admin123' with rounds=10: $2a$10$UqOJlF/Xd1LQoqhH0xoGte7LvlGqJtNJtFTJvgODqGSJl8uPvLhW2
INSERT INTO users 
(user_id, account_id, account_number, full_name, login_password_hash, transaction_password_hash, role, email, mobile_number, failed_login_attempts, account_locked, enabled)
VALUES 
('admin123', 999999, '999999999999', 'Admin User', '$2a$10$UqOJlF/Xd1LQoqhH0xoGte7LvlGqJtNJtFTJvgODqGSJl8uPvLhW2', '$2a$10$UqOJlF/Xd1LQoqhH0xoGte7LvlGqJtNJtFTJvgODqGSJl8uPvLhW2', 'ADMIN', 'admin@apexbank.com', '9999999999', 0, FALSE, TRUE);

-- Insert regular test users from account data
-- Customer Login Credentials:
-- User ID: arjun123, Password: password123
-- User ID: rahul123, Password: password123
-- BCrypt hash for 'password123' with rounds=10: $2a$10$N9qo8uLOickgx2ZMRZoMye.JrAhHYXnFz7RJg1k0j/c8VXLpvhPTu 
INSERT INTO users 
(user_id, account_id, account_number, full_name, login_password_hash, transaction_password_hash, role, email, mobile_number, failed_login_attempts, account_locked, enabled)
VALUES 
('arjun123', 1, '100000000001', 'Arjun K Rao', '$2a$10$N9qo8uLOickgx2ZMRZoMye.JrAhHYXnFz7RJg1k0j/c8VXLpvhPTu', '$2a$10$N9qo8uLOickgx2ZMRZoMye.JrAhHYXnFz7RJg1k0j/c8VXLpvhPTu', 'CUSTOMER', 'arjun.rao@example.com', '9876543210', 0, FALSE, TRUE),

('rahul123', 2, '100000000002', 'Rahul Verma', '$2a$10$N9qo8uLOickgx2ZMRZoMye.JrAhHYXnFz7RJg1k0j/c8VXLpvhPTu', '$2a$10$N9qo8uLOickgx2ZMRZoMye.JrAhHYXnFz7RJg1k0j/c8VXLpvhPTu', 'CUSTOMER', 'rahul.verma@example.com', '9988776655', 0, FALSE, TRUE);