package org.example.workspace.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import org.example.workspace.dao.TaskDAO;
import org.example.workspace.models.Task;
import org.example.workspace.models.TaskStatus;
import org.example.workspace.utils.AlertUtil;
import org.example.workspace.utils.SceneManager;
import org.example.workspace.utils.Session;

import java.time.LocalDate;
import java.util.List;

public class DashboardController {

    @FXML private Label welcomeLabel;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterComboBox;
    @FXML private TableView<Task> tasksTable;
    @FXML private TableColumn<Task, Integer> idColumn;
    @FXML private TableColumn<Task, String> titleColumn;
    @FXML private TableColumn<Task, String> statusColumn;
    @FXML private TableColumn<Task, String> priorityColumn;
    @FXML private TableColumn<Task, LocalDate> deadlineColumn;
    @FXML private TableColumn<Task, Void> actionsColumn;

    private TaskDAO taskDAO;
    private ObservableList<Task> tasksList;

    @FXML
    private void initialize() {
        taskDAO = new TaskDAO();
        tasksList = FXCollections.observableArrayList();

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        priorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));
        deadlineColumn.setCellValueFactory(new PropertyValueFactory<>("deadline"));

        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox pane = new HBox(5, editBtn, deleteBtn);

            {
                editBtn.setOnAction(event -> {
                    Task task = getTableView().getItems().get(getIndex());
                    editTask(task);
                });

                deleteBtn.setOnAction(event -> {
                    Task task = getTableView().getItems().get(getIndex());
                    deleteTask(task);
                });

                editBtn.getStyleClass().add("btn-primary");
                deleteBtn.getStyleClass().add("btn-danger");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        filterComboBox.setItems(FXCollections.observableArrayList(
                "All Tasks", "To Do", "In Progress", "Completed", "Cancelled", "Overdue"
        ));
        filterComboBox.setValue("All Tasks");

        welcomeLabel.setText("Welcome, " + Session.getInstance().getCurrentUsername() + "!");

        setupKeyboardShortcuts();
        loadTasks();
    }

    private void setupKeyboardShortcuts() {
        tasksTable.setOnKeyPressed(event -> {
            KeyCombination newTaskShortcut = new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN);
            KeyCombination refreshShortcut = new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN);
            KeyCombination searchShortcut = new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN);

            if (newTaskShortcut.match(event)) {
                handleAddTask();
                event.consume();
            } else if (refreshShortcut.match(event)) {
                handleRefresh();
                event.consume();
            } else if (searchShortcut.match(event)) {
                searchField.requestFocus();
                event.consume();
            }
        });

        searchField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                searchField.clear();
                tasksTable.requestFocus();
                loadTasks();
            }
        });
    }

    private void loadTasks() {
        Integer userId = Session.getInstance().getCurrentUserId();
        if (userId != null) {
            List<Task> tasks = taskDAO.getTasksByUserId(userId);
            tasksList.clear();
            tasksList.addAll(tasks);
            tasksTable.setItems(tasksList);
        }
    }

    @FXML
    private void handleSearch() {
        String searchQuery = searchField.getText().trim();
        Integer userId = Session.getInstance().getCurrentUserId();

        if (searchQuery.isEmpty()) {
            loadTasks();
        } else {
            List<Task> results = taskDAO.searchTasks(userId, searchQuery);
            tasksList.clear();
            tasksList.addAll(results);
        }
    }

    @FXML
    private void handleFilter() {
        String filter = filterComboBox.getValue();
        Integer userId = Session.getInstance().getCurrentUserId();

        if (filter.equals("All Tasks")) {
            loadTasks();
        } else if (filter.equals("Overdue")) {
            List<Task> overdue = taskDAO.getOverdueTasks(userId);
            tasksList.clear();
            tasksList.addAll(overdue);
        } else {
            TaskStatus status = TaskStatus.fromString(filter);
            List<Task> filtered = taskDAO.getTasksByStatus(userId, status);
            tasksList.clear();
            tasksList.addAll(filtered);
        }
    }

    @FXML
    private void handleAddTask() {
        SceneManager.switchScene("TaskForm.fxml");
    }

    private void editTask(Task task) {
        Session.getInstance().getCurrentUser().toString();
        SceneManager.switchScene("TaskForm.fxml");
    }

    private void deleteTask(Task task) {
        boolean confirm = AlertUtil.showConfirmation("Delete Task",
                "Are you sure you want to delete this task?\n\n" + task.getTitle());

        if (confirm) {
            boolean success = taskDAO.deleteTask(task.getId());
            if (success) {
                AlertUtil.showSuccess("Success", "Task deleted successfully");
                loadTasks();
            } else {
                AlertUtil.showError("Error", "Failed to delete task");
            }
        }
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
    private void handleLogout() {
        boolean confirm = AlertUtil.showConfirmation("Logout", "Are you sure you want to logout?");
        if (confirm) {
            Session.getInstance().clearSession();
            SceneManager.switchScene("WelcomePage.fxml");
        }
    }

    @FXML
    private void handleRefresh() {
        loadTasks();
        AlertUtil.showInfo("Refreshed", "Tasks list has been refreshed");
    }

    @FXML
    private void handleCalendarView() {
        SceneManager.switchScene("CalendarView.fxml");
    }

    @FXML
    private void handleBackToCalendar() {
        SceneManager.switchScene("CalendarView.fxml");
    }
}
