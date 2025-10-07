-- 1. 新增 user_id 字段，允许 null 以便迁移
ALTER TABLE notes ADD COLUMN user_id BIGINT;

-- 2. 迁移现有数据（如无用户信息，临时填充为 1，可后续批量更新）
UPDATE notes SET user_id = 1 WHERE user_id IS NULL;

-- 3. 设置外键约束和非空
ALTER TABLE notes ALTER COLUMN user_id SET NOT NULL;
ALTER TABLE notes ADD CONSTRAINT fk_notes_user FOREIGN KEY (user_id) REFERENCES users(id);
