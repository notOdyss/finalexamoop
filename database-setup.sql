-- =====================================================
-- Task Manager Database Setup Script
-- PostgreSQL Database Creation and Schema Definition
-- =====================================================

-- Create Database (run this separately if database doesn't exist)
-- CREATE DATABASE taskmanager_db;

-- Connect to the database
\c taskmanager_db

-- Drop existing tables if they exist (careful in production!)
DROP TABLE IF EXISTS tasks CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- =====================================================
-- USERS TABLE
-- =====================================================
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for faster queries
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);

-- Add comments for documentation
COMMENT ON TABLE users IS 'Stores user account information';
COMMENT ON COLUMN users.id IS 'Unique user identifier';
COMMENT ON COLUMN users.username IS 'Unique username for login';
COMMENT ON COLUMN users.email IS 'User email address';
COMMENT ON COLUMN users.password IS 'BCrypt hashed password';
COMMENT ON COLUMN users.created_at IS 'Account creation timestamp';

-- =====================================================
-- TASKS TABLE
-- =====================================================
CREATE TABLE tasks (
    id SERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'TODO',
    priority VARCHAR(10) DEFAULT 'MEDIUM',
    start_date DATE,
    deadline DATE,
    user_id INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Foreign key constraint
    CONSTRAINT fk_user
        FOREIGN KEY(user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    -- Check constraints
    CONSTRAINT chk_status
        CHECK (status IN ('TODO', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED')),
    CONSTRAINT chk_priority
        CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH'))
);

-- Create indexes for better performance
CREATE INDEX idx_tasks_user_id ON tasks(user_id);
CREATE INDEX idx_tasks_status ON tasks(status);
CREATE INDEX idx_tasks_start_date ON tasks(start_date);
CREATE INDEX idx_tasks_deadline ON tasks(deadline);
CREATE INDEX idx_tasks_priority ON tasks(priority);

-- Add comments for documentation
COMMENT ON TABLE tasks IS 'Stores user tasks and their details';
COMMENT ON COLUMN tasks.id IS 'Unique task identifier';
COMMENT ON COLUMN tasks.title IS 'Task title/name';
COMMENT ON COLUMN tasks.description IS 'Detailed task description';
COMMENT ON COLUMN tasks.status IS 'Current task status';
COMMENT ON COLUMN tasks.priority IS 'Task priority level';
COMMENT ON COLUMN tasks.start_date IS 'Task start date (for duration visualization)';
COMMENT ON COLUMN tasks.deadline IS 'Task deadline date';
COMMENT ON COLUMN tasks.user_id IS 'ID of the task owner';
COMMENT ON COLUMN tasks.created_at IS 'Task creation timestamp';
COMMENT ON COLUMN tasks.updated_at IS 'Last update timestamp';

-- =====================================================
-- SAMPLE DATA (Optional - for testing)
-- =====================================================

-- Insert sample user (password is "password123" hashed with BCrypt)
INSERT INTO users (username, email, password) VALUES
('testuser', 'test@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy');

-- Insert sample tasks for testing
INSERT INTO tasks (title, description, status, priority, start_date, deadline, user_id) VALUES
('Setup Project', 'Initialize project structure and dependencies', 'COMPLETED', 'HIGH', '2025-01-10', '2025-01-15', 1),
('Design Database', 'Create ERD and database schema', 'COMPLETED', 'HIGH', '2025-01-16', '2025-01-18', 1),
('Implement Backend', 'Create models, DAO, and services', 'IN_PROGRESS', 'HIGH', '2025-01-19', '2025-01-25', 1),
('Create UI', 'Design and implement user interface', 'TODO', 'MEDIUM', '2025-01-26', '2025-01-30', 1),
('Write Documentation', 'Create README and user guide', 'TODO', 'MEDIUM', '2025-02-01', '2025-02-05', 1);

-- =====================================================
-- VERIFICATION QUERIES
-- =====================================================

-- View all users
SELECT * FROM users;

-- View all tasks
SELECT * FROM tasks;

-- View tasks with user information
SELECT
    t.id,
    t.title,
    t.status,
    t.priority,
    t.deadline,
    u.username
FROM tasks t
JOIN users u ON t.user_id = u.id
ORDER BY t.created_at DESC;

-- Count tasks by status
SELECT status, COUNT(*) as count
FROM tasks
GROUP BY status;

-- =====================================================
-- USEFUL MAINTENANCE QUERIES
-- =====================================================

-- Delete all tasks
-- DELETE FROM tasks;

-- Delete all users (will cascade delete tasks)
-- DELETE FROM users;

-- Reset sequences
-- ALTER SEQUENCE users_id_seq RESTART WITH 1;
-- ALTER SEQUENCE tasks_id_seq RESTART WITH 1;

-- =====================================================
-- SUCCESS MESSAGE
-- =====================================================
SELECT 'Database setup completed successfully!' AS message;
