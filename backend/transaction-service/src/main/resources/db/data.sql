USE apexbank_txn_db;

-- Note: account_id values here correspond to accounts seeded in
-- apexbank_account_db (Arjun Rao = id 1 / 100000000001, Rahul Verma = id 2 / 100000000002)
INSERT INTO payees (owner_account_id, payee_name, payee_account_number, nickname) VALUES
(1, 'Rahul Verma', '100000000002', 'Rahul');

INSERT INTO transactions (reference_id, from_account_id, from_account_number, to_account_id, to_account_number, mode, amount, remarks, upi_id_used, status, transaction_datetime)
VALUES
('REF1000000001', 1, '100000000001', 2, '100000000002', 'NEFT', 2500.00, 'Rent', NULL, 'SUCCESS', '2026-07-01 10:15:00'),
('REF1000000002', 1, '100000000001', 2, '100000000002', 'UPI', 500.00, 'Lunch', '9988776655@apex', 'SUCCESS', '2026-07-15 13:30:00');
