-- =====================================================
-- Migration: Add start_date column to tasks table
-- Purpose: Support task duration visualization (Trello-style trails)
-- =====================================================

\c taskmanager_db

-- Add start_date column to tasks table
ALTER TABLE tasks ADD COLUMN IF NOT EXISTS start_date DATE;

-- Create index for performance
CREATE INDEX IF NOT EXISTS idx_tasks_start_date ON tasks(start_date);

-- Add comment for documentation
COMMENT ON COLUMN tasks.start_date IS 'Task start date (for duration visualization)';

-- Optional: Set existing tasks' start_date to their created_at date
UPDATE tasks
SET start_date = created_at::date
WHERE start_date IS NULL;

-- =====================================================
-- VERIFICATION
-- =====================================================
SELECT 'Migration completed successfully! start_date column added.' AS message;

-- View updated table structure
\d tasks
