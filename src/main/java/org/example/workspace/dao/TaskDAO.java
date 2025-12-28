package org.example.workspace.dao;

import org.example.workspace.database.DatabaseManager;
import org.example.workspace.models.Task;
import org.example.workspace.models.TaskStatus;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class TaskDAO {

    private Map<Integer, Task> taskCache = new HashMap<>();
    private boolean cacheEnabled = true;

    public boolean createTask(Task task) {
        String sql = "INSERT INTO tasks (title, description, status, priority, start_date, deadline, user_id, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, task.getTitle());
            pstmt.setString(2, task.getDescription());
            pstmt.setString(3, task.getStatus().name());
            pstmt.setString(4, task.getPriority());
            pstmt.setDate(5, task.getStartDate() != null ? java.sql.Date.valueOf(task.getStartDate()) : null);
            pstmt.setDate(6, task.getDeadline() != null ? java.sql.Date.valueOf(task.getDeadline()) : null);
            pstmt.setInt(7, task.getUserId());
            pstmt.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now()));

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        task.setId(generatedKeys.getInt(1));
                        if (cacheEnabled) {
                            taskCache.put(task.getId(), task);
                        }
                    }
                }
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Optional<Task> findById(Integer id) {
        if (cacheEnabled && taskCache.containsKey(id)) {
            return Optional.of(taskCache.get(id));
        }

        String sql = "SELECT * FROM tasks WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Task task = mapResultSetToTask(rs);
                if (cacheEnabled) {
                    taskCache.put(id, task);
                }
                return Optional.of(task);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public List<Task> getTasksByUserId(Integer userId) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE user_id = ? ORDER BY created_at DESC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Task task = mapResultSetToTask(rs);
                tasks.add(task);
                if (cacheEnabled) {
                    taskCache.put(task.getId(), task);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public List<Task> searchTasks(Integer userId, String searchQuery) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE user_id = ? AND " +
                     "(LOWER(title) LIKE ? OR LOWER(description) LIKE ?) " +
                     "ORDER BY created_at DESC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            String query = "%" + searchQuery.toLowerCase() + "%";
            pstmt.setString(2, query);
            pstmt.setString(3, query);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                tasks.add(mapResultSetToTask(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public List<Task> getTasksByStatus(Integer userId, TaskStatus status) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE user_id = ? AND status = ? ORDER BY deadline ASC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, status.name());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                tasks.add(mapResultSetToTask(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public List<Task> getTasksByPriority(Integer userId, String priority) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE user_id = ? AND priority = ? ORDER BY deadline ASC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, priority);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                tasks.add(mapResultSetToTask(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public List<Task> getOverdueTasks(Integer userId) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE user_id = ? AND deadline < CURRENT_DATE " +
                     "AND status != 'COMPLETED' ORDER BY deadline ASC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                tasks.add(mapResultSetToTask(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public boolean updateTask(Task task) {
        String sql = "UPDATE tasks SET title = ?, description = ?, status = ?, " +
                     "priority = ?, start_date = ?, deadline = ?, updated_at = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, task.getTitle());
            pstmt.setString(2, task.getDescription());
            pstmt.setString(3, task.getStatus().name());
            pstmt.setString(4, task.getPriority());
            pstmt.setDate(5, task.getStartDate() != null ? java.sql.Date.valueOf(task.getStartDate()) : null);
            pstmt.setDate(6, task.getDeadline() != null ? java.sql.Date.valueOf(task.getDeadline()) : null);
            pstmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(8, task.getId());

            boolean updated = pstmt.executeUpdate() > 0;
            if (updated && cacheEnabled) {
                taskCache.put(task.getId(), task);
            }
            return updated;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteTask(Integer id) {
        String sql = "DELETE FROM tasks WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            boolean deleted = pstmt.executeUpdate() > 0;
            if (deleted && cacheEnabled) {
                taskCache.remove(id);
            }
            return deleted;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Map<String, Integer> getTaskStatistics(Integer userId) {
        Map<String, Integer> stats = new HashMap<>();
        String sql = "SELECT status, COUNT(*) as count FROM tasks WHERE user_id = ? GROUP BY status";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                stats.put(rs.getString("status"), rs.getInt("count"));
            }

            int total = stats.values().stream().mapToInt(Integer::intValue).sum();
            stats.put("TOTAL", total);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    public void clearCache() {
        taskCache.clear();
    }

    private Task mapResultSetToTask(ResultSet rs) throws SQLException {
        Task task = new Task();
        task.setId(rs.getInt("id"));
        task.setTitle(rs.getString("title"));
        task.setDescription(rs.getString("description"));
        task.setStatus(TaskStatus.valueOf(rs.getString("status")));
        task.setPriority(rs.getString("priority"));

        java.sql.Date startDate = rs.getDate("start_date");
        if (startDate != null) {
            task.setStartDate(startDate.toLocalDate());
        }

        java.sql.Date deadline = rs.getDate("deadline");
        if (deadline != null) {
            task.setDeadline(deadline.toLocalDate());
        }

        task.setUserId(rs.getInt("user_id"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            task.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            task.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return task;
    }
}
