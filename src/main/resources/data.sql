
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

--WonderWoman@123(admin password)
--LazyMan@123(user password)
INSERT OR IGNORE INTO auth_table (id, pass_word, role, user_name)
VALUES
('8864d34d-e46d-4cae-91e9-7d2d30cb7602', '$2a$12$gqdi3aRSfIJfPzBm1ylh1uWVkLOdK6aDWV7V31UyFS6PfxCafv1ce', 'ADMIN', 'WonderWoman'),
('63281f7b-d68d-485b-a073-3109d0c49671', '$2a$12$B6.oSBbcAIEKJlsq6siq1uuapEnPquMfsLC8DHwGajq1YaJZP8mSa', 'USER', 'LazyMan');

INSERT OR IGNORE INTO users (
    id, name, email, phone, created_At, updated_At, role
) VALUES (
    '8864d34d-e46d-4cae-91e9-7d2d30cb7602', 'WonderWoman', 'wonderwomen001@gmail.com', '1234567890', '2025-09-03', '2025-09-03', 'ADMIN'
);


INSERT OR IGNORE INTO users (
    id, name, email, phone, created_At, updated_At, role
) VALUES (
    '63281f7b-d68d-485b-a073-3109d0c49671', 'LazyMan', 'lazyman420@gmail.com', '0987654321', '2025-09-03', '2025-09-03', 'USER'
);
