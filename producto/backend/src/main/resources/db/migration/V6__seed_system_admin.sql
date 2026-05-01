INSERT INTO users (role, email, password_hash, is_active, failed_login_attempts, password_reset_required)
SELECT 'ADMIN', 'admin@dominio.cl', '$2b$10$KV2Lkb/kuznnHiPsBpazhO8eHbcHNmskAJxhkA.h1cLaZkkBpk37.', 1, 0, 0
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@dominio.cl');
