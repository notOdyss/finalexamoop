package org.example.workspace.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import org.example.workspace.dao.TaskDAO;
import org.example.workspace.models.Task;
import org.example.workspace.models.TaskStatus;
import org.example.workspace.utils.AlertUtil;
import org.example.workspace.utils.SceneManager;
import org.example.workspace.utils.Session;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class CalendarController {

    @FXML private Label monthYearLabel;
    @FXML private GridPane calendarGrid;
    @FXML private Label welcomeLabel;

    private TaskDAO taskDAO;
    private YearMonth currentYearMonth;
    private Map<LocalDate, List<Task>> tasksByDate;

    @FXML
    private void initialize() {
        taskDAO = new TaskDAO();
        currentYearMonth = YearMonth.now();

        welcomeLabel.setText("Welcome, " + Session.getInstance().getCurrentUsername() + "!");

        loadCalendar();
    }

    private void loadCalendar() {
        Integer userId = Session.getInstance().getCurrentUserId();
        if (userId != null) {
            List<Task> allTasks = taskDAO.getTasksByUserId(userId);

            tasksByDate = allTasks.stream()
                    .filter(task -> task.getDeadline() != null)
                    .collect(Collectors.groupingBy(Task::getDeadline));
        }

        updateCalendarView();
    }

    private void updateCalendarView() {
        calendarGrid.getChildren().clear();

        monthYearLabel.setText(currentYearMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                              + " " + currentYearMonth.getYear());

        String[] dayNames = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (int i = 0; i < 7; i++) {
            Label dayLabel = new Label(dayNames[i]);
            dayLabel.getStyleClass().add("calendar-header");
            dayLabel.setMaxWidth(Double.MAX_VALUE);
            dayLabel.setAlignment(Pos.CENTER);
            calendarGrid.add(dayLabel, i, 0);
        }

        LocalDate firstOfMonth = currentYearMonth.atDay(1);
        int daysInMonth = currentYearMonth.lengthOfMonth();
        int startDayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        int row = 1;
        int col = startDayOfWeek - 1;

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = currentYearMonth.atDay(day);
            VBox dayBox = createDayBox(date, day);

            calendarGrid.add(dayBox, col, row);

            col++;
            if (col == 7) {
                col = 0;
                row++;
            }
        }
    }

    private VBox createDayBox(LocalDate date, int dayNumber) {
        VBox dayBox = new VBox(5);
        dayBox.setAlignment(Pos.TOP_LEFT);
        dayBox.getStyleClass().add("calendar-day");
        dayBox.setMinHeight(120);
        dayBox.setMaxWidth(Double.MAX_VALUE);

        Label dayLabel = new Label(String.valueOf(dayNumber));
        dayLabel.getStyleClass().add("day-number");

        if (date.equals(LocalDate.now())) {
            dayBox.getStyleClass().add("calendar-day-today");
        }

        HBox headerBox = new HBox(5);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.getChildren().add(dayLabel);
        dayBox.getChildren().add(headerBox);

        if (tasksByDate != null) {
            List<Task> allTasks = tasksByDate.values().stream()
                    .flatMap(List::stream)
                    .distinct()
                    .filter(task -> task.getStartDate() != null && task.getDeadline() != null)
                    .filter(task -> !date.isBefore(task.getStartDate()) && !date.isAfter(task.getDeadline()))
                    .toList();

            for (Task task : allTasks) {
                LocalDate middleDate = getMiddleDate(task.getStartDate(), task.getDeadline());

                if (date.equals(middleDate)) {
                    VBox trailWithTitle = createTrailWithTitle(task);
                    dayBox.getChildren().add(trailWithTitle);
                } else if (date.equals(task.getDeadline())) {
                    VBox taskCard = createTrelloCard(task);
                    dayBox.getChildren().add(taskCard);
                } else {
                    HBox trailBar = createTrailBar(task);
                    dayBox.getChildren().add(trailBar);
                }
            }
        }

        ContextMenu contextMenu = createDayContextMenu(date);

        dayBox.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                handleDayClick(date);
            } else if (e.getButton() == MouseButton.SECONDARY) {
                contextMenu.show(dayBox, e.getScreenX(), e.getScreenY());
            }
        });

        return dayBox;
    }

    private VBox createTrelloCard(Task task) {
        VBox card = new VBox(4);
        card.getStyleClass().add("trello-card");
        card.setMaxWidth(Double.MAX_VALUE);
        card.setAlignment(Pos.TOP_LEFT);

        HBox labelBox = new HBox(3);
        labelBox.setAlignment(Pos.CENTER_LEFT);

        Label priorityLabel = new Label(task.getPriority());
        priorityLabel.getStyleClass().add("trello-label");
        priorityLabel.setStyle(
            "-fx-background-color: " + getPriorityColor(task.getPriority()) + "; " +
            "-fx-background-radius: 3px; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 9px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 2px 6px;"
        );
        labelBox.getChildren().add(priorityLabel);

        Label statusLabel = new Label(getStatusDisplayName(task.getStatus()));
        statusLabel.getStyleClass().add("trello-label");
        statusLabel.setStyle(
            "-fx-background-color: " + getStatusColor(task.getStatus()) + "; " +
            "-fx-background-radius: 3px; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 9px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 2px 6px;"
        );
        labelBox.getChildren().add(statusLabel);

        card.getChildren().add(labelBox);

        Label titleLabel = new Label(task.getTitle());
        titleLabel.getStyleClass().add("trello-card-title");
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        card.getChildren().add(titleLabel);

        card.setOnMouseClicked(e -> {
            if (e.getButton() == javafx.scene.input.MouseButton.PRIMARY) {
                handleTaskCardClick(task);
            }
        });

        return card;
    }

    private HBox createTrailBar(Task task) {
        HBox trailBar = new HBox();
        trailBar.getStyleClass().add("task-trail");
        trailBar.setPrefHeight(8);
        trailBar.setMaxWidth(Double.MAX_VALUE);

        String color = getPriorityColor(task.getPriority());
        trailBar.setStyle(
            "-fx-background-color: linear-gradient(to right, " + color + " 0%, " + color + "AA 100%); " +
            "-fx-background-radius: 3px; " +
            "-fx-border-radius: 3px; " +
            "-fx-opacity: 0.6;"
        );

        javafx.scene.control.Tooltip tooltip = new javafx.scene.control.Tooltip(
            task.getTitle() + "\n" +
            "Start: " + task.getStartDate() + "\n" +
            "Deadline: " + task.getDeadline()
        );
        javafx.scene.control.Tooltip.install(trailBar, tooltip);

        trailBar.setOnMouseClicked(e -> {
            if (e.getButton() == javafx.scene.input.MouseButton.PRIMARY) {
                handleTaskCardClick(task);
            }
        });

        return trailBar;
    }

    private VBox createTrailWithTitle(Task task) {
        VBox container = new VBox(2);
        container.setMaxWidth(Double.MAX_VALUE);
        container.setAlignment(Pos.TOP_LEFT);

        // Trail bar
        HBox trailBar = new HBox();
        trailBar.getStyleClass().add("task-trail-with-title");
        trailBar.setPrefHeight(8);
        trailBar.setMaxWidth(Double.MAX_VALUE);

        String color = getPriorityColor(task.getPriority());
        trailBar.setStyle(
            "-fx-background-color: linear-gradient(to right, " + color + " 0%, " + color + "AA 100%); " +
            "-fx-background-radius: 3px; " +
            "-fx-opacity: 0.7;"
        );

        container.getChildren().add(trailBar);

        Label titleLabel = new Label(task.getTitle());
        titleLabel.getStyleClass().add("trail-title");
        titleLabel.setStyle(
            "-fx-text-fill: #ffffff; " +
            "-fx-font-size: 11px; " +
            "-fx-font-weight: 600; " +
            "-fx-padding: 2px 4px; " +
            "-fx-background-color: " + color + "; " +
            "-fx-background-radius: 3px;"
        );
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        titleLabel.setWrapText(true);
        container.getChildren().add(titleLabel);

        javafx.scene.control.Tooltip tooltip = new javafx.scene.control.Tooltip(
            task.getTitle() + "\n" +
            "Priority: " + task.getPriority() + "\n" +
            "Status: " + getStatusDisplayName(task.getStatus()) + "\n" +
            "Start: " + task.getStartDate() + "\n" +
            "Deadline: " + task.getDeadline()
        );
        javafx.scene.control.Tooltip.install(container, tooltip);

        container.setOnMouseClicked(e -> {
            if (e.getButton() == javafx.scene.input.MouseButton.PRIMARY) {
                handleTaskCardClick(task);
            }
        });
        container.getStyleClass().add("trail-container-clickable");

        return container;
    }

    private LocalDate getMiddleDate(LocalDate start, LocalDate end) {
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(start, end);
        long middleDays = daysBetween / 2;
        return start.plusDays(middleDays);
    }

    private String getPriorityColor(String priority) {
        return switch (priority) {
            case "HIGH" -> "#f85149";
            case "MEDIUM" -> "#d29922";
            case "LOW" -> "#3fb950";
            default -> "#8b949e";
        };
    }

    private String getStatusColor(TaskStatus status) {
        return switch (status) {
            case TODO -> "#58a6ff";
            case IN_PROGRESS -> "#d29922";
            case COMPLETED -> "#3fb950";
            case CANCELLED -> "#8b949e";
        };
    }

    private String getStatusDisplayName(TaskStatus status) {
        return switch (status) {
            case TODO -> "TO DO";
            case IN_PROGRESS -> "IN PROGRESS";
            case COMPLETED -> "COMPLETED";
            case CANCELLED -> "CANCELLED";
        };
    }

    private void handleTaskCardClick(Task task) {
        SceneManager.switchSceneWithData("TaskForm.fxml", (TaskFormController controller) -> {
            controller.setTask(task);
        });
    }

    private ContextMenu createDayContextMenu(LocalDate date) {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem addTaskItem = new MenuItem("+ Add Task for " + date.toString());
        addTaskItem.setOnAction(e -> handleAddTaskForDate(date));
        addTaskItem.setStyle("-fx-font-size: 13px; -fx-font-weight: 600;");

        MenuItem markHolidayItem = new MenuItem("🎉 Mark as Holiday");
        markHolidayItem.setOnAction(e -> handleMarkAsHoliday(date));
        markHolidayItem.setStyle("-fx-font-size: 13px;");

        if (tasksByDate != null && tasksByDate.containsKey(date)) {
            MenuItem viewTasksItem = new MenuItem("📋 View Tasks (" + tasksByDate.get(date).size() + ")");
            viewTasksItem.setOnAction(e -> handleViewTasksForDate(date));
            viewTasksItem.setStyle("-fx-font-size: 13px; -fx-font-weight: 600;");
            contextMenu.getItems().add(viewTasksItem);
            contextMenu.getItems().add(new SeparatorMenuItem());
        }

        contextMenu.getItems().addAll(addTaskItem, markHolidayItem);

        contextMenu.setStyle("-fx-background-color: #161b22; -fx-border-color: #30363d; -fx-border-width: 1px;");

        return contextMenu;
    }

    private void handleAddTaskForDate(LocalDate date) {
        SceneManager.switchSceneWithData("TaskForm.fxml", (TaskFormController controller) -> {
            controller.setPrefilledStartDate(date);
        });
    }

    private void handleMarkAsHoliday(LocalDate date) {
        boolean confirm = AlertUtil.showConfirmation("Mark as Holiday",
                "Do you want to mark " + date.toString() + " as a holiday?");

        if (confirm) {
            AlertUtil.showSuccess("Holiday Marked",
                    date.toString() + " has been marked as a holiday!\n\n" +
                    "(This feature would save to database in production)");
        }
    }

    private void handleViewTasksForDate(LocalDate date) {
        if (tasksByDate != null && tasksByDate.containsKey(date)) {
            List<Task> tasksOnDate = tasksByDate.get(date);

            StringBuilder message = new StringBuilder("Tasks on " + date.toString() + ":\n\n");
            int count = 1;
            for (Task task : tasksOnDate) {
                message.append(count++).append(". ")
                       .append(task.getTitle())
                       .append(" [").append(task.getStatus().getDisplayName()).append("]\n");
            }

            AlertUtil.showInfo("Tasks for " + date.toString(), message.toString());
        }
    }

    private void handleDayClick(LocalDate date) {
        List<Task> tasksForDay = getTasksForDate(date);

        if (tasksForDay.isEmpty()) {
            AlertUtil.showInfo("No Tasks",
                "No tasks found for " + date.toString() + "\n\nRight-click to add a new task.");
            return;
        }

        showTasksDialog(date, tasksForDay);
    }

    private List<Task> getTasksForDate(LocalDate date) {
        if (tasksByDate == null) {
            return List.of();
        }

        return tasksByDate.values().stream()
                .flatMap(List::stream)
                .distinct()
                .filter(task -> task.getStartDate() != null && task.getDeadline() != null)
                .filter(task -> !date.isBefore(task.getStartDate()) && !date.isAfter(task.getDeadline()))
                .toList();
    }

    private void showTasksDialog(LocalDate date, List<Task> tasks) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Tasks for " + date.toString());
        dialog.setHeaderText(tasks.size() + " task" + (tasks.size() > 1 ? "s" : "") + " on this date");

        VBox content = new VBox(10);
        content.setStyle("-fx-background-color: #0d1117; -fx-padding: 20;");

        for (Task task : tasks) {
            VBox taskCard = createDialogTaskCard(task, date);
            content.getChildren().add(taskCard);
        }

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(400);
        scrollPane.setPrefWidth(600);
        scrollPane.setStyle("-fx-background: #0d1117; -fx-background-color: #0d1117;");

        dialog.getDialogPane().setContent(scrollPane);
        dialog.getDialogPane().setStyle("-fx-background-color: #0d1117;");

        ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType addTaskButton = new ButtonType("+ Add Task", ButtonBar.ButtonData.OTHER);
        dialog.getDialogPane().getButtonTypes().addAll(addTaskButton, closeButton);

        Button addButton = (Button) dialog.getDialogPane().lookupButton(addTaskButton);
        addButton.setOnAction(e -> {
            dialog.close();
            handleAddTaskForDate(date);
        });

        dialog.showAndWait();
    }

    private VBox createDialogTaskCard(Task task, LocalDate currentDate) {
        VBox card = new VBox(8);
        card.setStyle(
            "-fx-background-color: #161b22; " +
            "-fx-border-color: #30363d; " +
            "-fx-border-width: 1px; " +
            "-fx-border-radius: 6px; " +
            "-fx-background-radius: 6px; " +
            "-fx-padding: 15; " +
            "-fx-cursor: hand;"
        );

        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);

        Label priorityLabel = new Label(task.getPriority());
        priorityLabel.setStyle(
            "-fx-background-color: " + getPriorityColor(task.getPriority()) + "; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 11px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 4px 8px; " +
            "-fx-background-radius: 3px;"
        );

        Label statusLabel = new Label(getStatusDisplayName(task.getStatus()));
        statusLabel.setStyle(
            "-fx-background-color: " + getStatusColor(task.getStatus()) + "; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 11px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 4px 8px; " +
            "-fx-background-radius: 3px;"
        );

        header.getChildren().addAll(priorityLabel, statusLabel);

        Label titleLabel = new Label(task.getTitle());
        titleLabel.setStyle(
            "-fx-text-fill: #c9d1d9; " +
            "-fx-font-size: 16px; " +
            "-fx-font-weight: 600;"
        );
        titleLabel.setWrapText(true);

        Label descLabel = new Label(task.getDescription());
        descLabel.setStyle(
            "-fx-text-fill: #8b949e; " +
            "-fx-font-size: 13px;"
        );
        descLabel.setWrapText(true);

        HBox dateBox = new HBox(10);
        dateBox.setAlignment(Pos.CENTER_LEFT);

        Label dateLabel = new Label(
            "📅 " + task.getStartDate() + " → " + task.getDeadline()
        );
        dateLabel.setStyle("-fx-text-fill: #58a6ff; -fx-font-size: 12px;");

        String dayType = "";
        if (currentDate.equals(task.getStartDate())) {
            dayType = " (Start Date)";
        } else if (currentDate.equals(task.getDeadline())) {
            dayType = " (Deadline)";
        } else {
            dayType = " (In Progress)";
        }

        Label dayTypeLabel = new Label(dayType);
        dayTypeLabel.setStyle(
            "-fx-text-fill: #f0883e; " +
            "-fx-font-size: 11px; " +
            "-fx-font-weight: bold;"
        );

        dateBox.getChildren().addAll(dateLabel, dayTypeLabel);

        card.getChildren().addAll(header, titleLabel, descLabel, dateBox);

        card.setOnMouseClicked(e -> {
            handleTaskCardClick(task);
        });

        card.setOnMouseEntered(e -> {
            card.setStyle(
                "-fx-background-color: #1c2128; " +
                "-fx-border-color: #58a6ff; " +
                "-fx-border-width: 1px; " +
                "-fx-border-radius: 6px; " +
                "-fx-background-radius: 6px; " +
                "-fx-padding: 15; " +
                "-fx-cursor: hand;"
            );
        });

        card.setOnMouseExited(e -> {
            card.setStyle(
                "-fx-background-color: #161b22; " +
                "-fx-border-color: #30363d; " +
                "-fx-border-width: 1px; " +
                "-fx-border-radius: 6px; " +
                "-fx-background-radius: 6px; " +
                "-fx-padding: 15; " +
                "-fx-cursor: hand;"
            );
        });

        return card;
    }

    @FXML
    private void handlePreviousMonth() {
        currentYearMonth = currentYearMonth.minusMonths(1);
        updateCalendarView();
    }

    @FXML
    private void handleNextMonth() {
        currentYearMonth = currentYearMonth.plusMonths(1);
        updateCalendarView();
    }

    @FXML
    private void handleToday() {
        currentYearMonth = YearMonth.now();
        updateCalendarView();
    }

    @FXML
    private void handleBackToDashboard() {
        SceneManager.switchScene("Dashboard.fxml");
    }

    @FXML
    private void handleViewStatistics() {
        SceneManager.switchScene("Statistics.fxml");
    }

    @FXML
    private void handleViewProfile() {
        SceneManager.switchScene("Profile.fxml");
    }

    @FXML
    private void handleAddTask() {
        SceneManager.switchScene("TaskForm.fxml");
    }

    @FXML
    private void handleLogout() {
        Session.getInstance().clearSession();
        SceneManager.switchScene("WelcomePage.fxml");
    }

    @FXML
    private void handleRefresh() {
        loadCalendar();
    }
}
