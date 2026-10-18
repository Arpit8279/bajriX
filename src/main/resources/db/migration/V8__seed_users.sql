-- ============================================================
-- V8: Seed Predefined Users (4 Sellers & 2 Buyers)
-- All users have password: "BajriX@123"
-- Password hash: $2a$10$pmNxbCgDbpc3/RYAGaQEkOjLun/8UaDyNTDr.a.HZPxQPM7x3l9N.
-- ============================================================

INSERT INTO users (id, email, password_hash, full_name, role, seller_id) VALUES
-- 4 Sellers (who are also Buyers)
(1, 'sahuarpit33@gmail.com',       '$2a$10$pmNxbCgDbpc3/RYAGaQEkOjLun/8UaDyNTDr.a.HZPxQPM7x3l9N.', 'Arpit Sahu',           'SELLER', 1),
(2, 'buildmart.seller@gmail.com',  '$2a$10$pmNxbCgDbpc3/RYAGaQEkOjLun/8UaDyNTDr.a.HZPxQPM7x3l9N.', 'BuildMart India Rep',  'SELLER', 2),
(3, 'raj.seller@gmail.com',        '$2a$10$pmNxbCgDbpc3/RYAGaQEkOjLun/8UaDyNTDr.a.HZPxQPM7x3l9N.', 'Rajesh Kumar',         'SELLER', 3),
(4, 'mehta.seller@gmail.com',      '$2a$10$pmNxbCgDbpc3/RYAGaQEkOjLun/8UaDyNTDr.a.HZPxQPM7x3l9N.', 'Kirit Mehta',          'SELLER', 5),

-- 2 Buyers (Buyer only)
(5, 'arpitsahu8279@gmail.com',     '$2a$10$pmNxbCgDbpc3/RYAGaQEkOjLun/8UaDyNTDr.a.HZPxQPM7x3l9N.', 'Arpit Sahu (Buyer)',   'BUYER',  NULL),
(6, 'priya.buyer@gmail.com',       '$2a$10$pmNxbCgDbpc3/RYAGaQEkOjLun/8UaDyNTDr.a.HZPxQPM7x3l9N.', 'Priya Sharma',         'BUYER',  NULL);

-- Reset sequence for users_id_seq
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
