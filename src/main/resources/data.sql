
INSERT OR IGNORE INTO products (
  product_id, cooling_period, description, funding_window, interest_rate, product_name, tenure
) VALUES (
  'FD01', 1, '1 year plan with the interestRate of 6.1', 3, 6.1, '1 year plan', 10
);

INSERT OR IGNORE INTO products (
  product_id, cooling_period, description, funding_window, interest_rate, product_name, tenure
) VALUES (
  'FD02', 2, '2 year plan with the interestRate of 6.4', 3, 6.4, '2 year plan', 20
);

INSERT OR IGNORE INTO products (
  product_id, description, interest_rate, product_name, cooling_period, funding_window, tenure
) VALUES (
  'SAV01', 'saving account with the interestRate of 1', 1, 'Saving Account', 0, 0, 0
);


INSERT OR IGNORE INTO auth_table (id, pass_word, role, user_name)
VALUES
('110011001100', 'WonderWoman@123', 'ADMIN', 'WonderWoman'),
('110011001101', 'LazyMan@123', 'USER', 'LazyMan');

INSERT OR IGNORE INTO users (
    id, name, email, phone, created_At, updated_At, role
) VALUES (
    '110011001100', 'WonderWoman', 'wonderwomen001@gmail.com', '1234567890', '2025-09-03', '2025-09-03', 'ADMIN'
);


INSERT OR IGNORE INTO users (
    id, name, email, phone, created_At, updated_At, role
) VALUES (
    '110011001101', 'LazyMan', 'lazyman420@gmail.com', '0987654321', '2025-09-03', '2025-09-03', 'USER'
);
