-- Insert sample admin user
INSERT INTO users (username, email, role, enabled) 
VALUES ('Chalkim', 'jiangyi2012@outlook.com', 'ROLE_ADMIN', true);

-- Insert sample regular user
INSERT INTO users (username, email, role, enabled) 
VALUES ('testuser', 'test@example.com', 'ROLE_USER', true);

-- Note: To bind GitHub accounts, use the admin API:
-- POST /admin/users/{userId}/bind-github with body {"providerUserId": "your-github-user-id"}