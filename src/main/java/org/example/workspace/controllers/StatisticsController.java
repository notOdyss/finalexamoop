package org.example.workspace.controllers;

import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import org.example.workspace.dao.TaskDAO;
import org.example.workspace.models.Task;
import org.example.workspace.utils.SceneManager;
import org.example.workspace.utils.Session;

import java.util.List;
import java.util.Map;

public class StatisticsController {

    @FXML private Label totalTasksLabel;
    @FXML private Label todoTasksLabel;
    @FXML private Label inProgressTasksLabel;
    @FXML private Label completedTasksLabel;
    @FXML private Label overdueTasksLabel;
    @FXML private Label completionRateLabel;
    @FXML private PieChart tasksPieChart;

    private TaskDAO taskDAO;

    @FXML
    private void initialize() {
        taskDAO = new TaskDAO();
        loadStatistics();
    }

    private void loadStatistics() {
        Integer userId = Session.getInstance().getCurrentUserId();
        if (userId == null) {
            return;
        }

        Map<String, Integer> stats = taskDAO.getTaskStatistics(userId);
        List<Task> overdueTasks = taskDAO.getOverdueTasks(userId);

        int total = stats.getOrDefault("TOTAL", 0);
        int todo = stats.getOrDefault("TODO", 0);
        int inProgress = stats.getOrDefault("IN_PROGRESS", 0);
        int completed = stats.getOrDefault("COMPLETED", 0);
        int cancelled = stats.getOrDefault("CANCELLED", 0);
        int overdue = overdueTasks.size();

        totalTasksLabel.setText(String.valueOf(total));
        todoTasksLabel.setText(String.valueOf(todo));
        inProgressTasksLabel.setText(String.valueOf(inProgress));
        completedTasksLabel.setText(String.valueOf(completed));
        overdueTasksLabel.setText(String.valueOf(overdue));

        double completionRate = 0.0;
        if (total > 0) {
            completionRate = ((double) completed / total) * 100;
        }
        completionRateLabel.setText(String.format("%.1f%%", completionRate));

        tasksPieChart.getData().clear();
        if (todo > 0) {
            tasksPieChart.getData().add(new PieChart.Data("To Do (" + todo + ")", todo));
        }
        if (inProgress > 0) {
            tasksPieChart.getData().add(new PieChart.Data("In Progress (" + inProgress + ")", inProgress));
        }
        if (completed > 0) {
            tasksPieChart.getData().add(new PieChart.Data("Completed (" + completed + ")", completed));
        }
        if (cancelled > 0) {
            tasksPieChart.getData().add(new PieChart.Data("Cancelled (" + cancelled + ")", cancelled));
        }

        tasksPieChart.setTitle("Tasks Distribution");
        tasksPieChart.setLegendVisible(true);
    }

    @FXML
    private void handleBack() {
        SceneManager.switchScene("CalendarView.fxml");
    }

    @FXML
    private void handleBackToDashboard() {
        SceneManager.switchScene("Dashboard.fxml");
    }

    @FXML
    private void handleViewProfile() {
        SceneManager.switchScene("Profile.fxml");
    }

    @FXML
    private void handleLogout() {
        Session.getInstance().clearSession();
        SceneManager.switchScene("WelcomePage.fxml");
    }

    @FXML
    private void handleRefresh() {
        loadStatistics();
    }
}
