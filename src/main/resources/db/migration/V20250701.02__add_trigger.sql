CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = CURRENT_TIMESTAMP;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 给 notes 表创建触发器
CREATE TRIGGER update_notes_updated_at
BEFORE UPDATE ON notes
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

-- 给 summaries 表创建触发器
CREATE TRIGGER update_summaries_updated_at
BEFORE UPDATE ON summaries
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();