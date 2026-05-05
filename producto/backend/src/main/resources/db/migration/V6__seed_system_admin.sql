INSERT INTO users (role, email, password_hash, is_active, failed_login_attempts, password_reset_required)
SELECT 'ADMIN', 'admin@dominio.cl', '$2a$12$N3CGBVmOUArrRUfeb6CD0O4wKsnc0jpdqdGx.H3Y7GDj8stY67XXK', 1, 0, 0
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@dominio.cl');
