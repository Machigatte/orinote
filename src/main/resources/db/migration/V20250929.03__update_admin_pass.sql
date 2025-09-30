-- update admin password from users table

UPDATE users SET passwd = '$2a$10$.zeJ751Gk0JonSmIML8Y9O.IWipW1RLVBYZeQBvLJPimzbf6aAMAu' WHERE username = 'admin';