INSERT INTO roles (id, name, description)
VALUES
(1, 'ROLE_ADMIN', 'Usuario administrador'),
(2, 'ROLE_USER', 'Usuario normal');

INSERT INTO users (id, username, password, email, enabled, created_at)
VALUES
(1, 'zabieru363', '$2a$10$xgGhRIifHjcQN66omn1llO6fNnGb5vnMq904hlySJRk2qzuJaCgbO', 'zabierujlc@gmail.com', true, CURRENT_DATE),
(2, 'prueba', '$2a$10$xgGhRIifHjcQN66omn1llO6fNnGb5vnMq904hlySJRk2qzuJaCgbO', 'zabierujlc2@gmail.com', true, CURRENT_DATE);

INSERT INTO users_roles (user_id, role_id)
VALUES (1, 1), (1, 2), (2, 1), (2, 2);

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO pg_database_owner;

INSERT INTO exercises (id, name, main_muscle, secondary_muscle, description, require_equipment, added_at, enabled)
VALUES
(1, 'Push Up', 'Chest', null, 'A basic exercise for upper body strength.', false, CURRENT_DATE, true),
(2, 'Squat', 'Legs', null, 'A fundamental exercise for lower body strength.', false, CURRENT_DATE, true),
(3, 'Deadlift', 'Back', 'Chest', 'A compound exercise for overall strength.', true, CURRENT_DATE, true);