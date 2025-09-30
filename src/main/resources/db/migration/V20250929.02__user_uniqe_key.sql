-- 创建users表username唯一键约束
ALTER TABLE users ADD CONSTRAINT unique_username UNIQUE (username);

-- 创建索引以加速用户名查询
CREATE INDEX idx_users_username ON users(username);
