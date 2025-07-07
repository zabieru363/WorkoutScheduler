INSERT INTO roles (id, name, description)
VALUES
(1, 'ROLE_ADMIN', 'Usuario administrador'),
(2, 'ROLE_USER', 'Usuario normal');

INSERT INTO users (id, username, password, email, enabled, created_at)
VALUES
(1, 'zabieru363', '$2a$10$xgGhRIifHjcQN66omn1llO6fNnGb5vnMq904hlySJRk2qzuJaCgbO', 'zabierujlc@gmail.com', true, CURRENT_DATE);

INSERT INTO users_roles (user_id, role_id)
VALUES (1, 1), (1, 2);