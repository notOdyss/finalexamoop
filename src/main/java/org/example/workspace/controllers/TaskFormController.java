package org.example.workspace.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ListCell;
import org.example.workspace.dao.TaskDAO;
import org.example.workspace.models.Task;
import org.example.workspace.models.TaskStatus;
import org.example.workspace.utils.AlertUtil;
import org.example.workspace.utils.SceneManager;
import org.example.workspace.utils.Session;

import java.time.LocalDate;

public class TaskFormController {

    @FXML private TextField titleField;
    @FXML private TextArea descriptionArea;
    @FXML private ComboBox<TaskStatus> statusComboBox;
    @FXML private ComboBox<String> priorityComboBox;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker deadlinePicker;

    private TaskDAO taskDAO;
    private Task currentTask;
    private boolean isEditMode = false;
    private LocalDate prefilledStartDate = null;

    @FXML
    private void initialize() {
        taskDAO = new TaskDAO();

        statusComboBox.setItems(FXCollections.observableArrayList(
                TaskStatus.TODO,
                TaskStatus.IN_PROGRESS,
                TaskStatus.COMPLETED,
                TaskStatus.CANCELLED
        ));

        statusComboBox.setButtonCell(new ListCell<TaskStatus>() {
            @Override
            protected void updateItem(TaskStatus item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getDisplayName());
                    setStyle("-fx-text-fill: #ffffff; -fx-font-size: 15px; -fx-font-weight: 500;");
                }
            }
        });

        statusComboBox.setCellFactory(lv -> new ListCell<TaskStatus>() {
            @Override
            protected void updateItem(TaskStatus item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getDisplayName());
                }
            }
        });

        statusComboBox.setValue(TaskStatus.TODO);

        priorityComboBox.setItems(FXCollections.observableArrayList(
                "LOW", "MEDIUM", "HIGH"
        ));

        priorityComboBox.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: #ffffff; -fx-font-size: 15px; -fx-font-weight: 500;");
                }
            }
        });

        priorityComboBox.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item);
            }
        });

        priorityComboBox.setValue("MEDIUM");

        if (prefilledStartDate != null) {
            startDatePicker.setValue(prefilledStartDate);
        }
    }

    public void setPrefilledStartDate(LocalDate date) {
        this.prefilledStartDate = date;
        if (startDatePicker != null) {
            startDatePicker.setValue(date);
        }
    }

    public void setTask(Task task) {
        this.currentTask = task;
        this.isEditMode = true;

        titleField.setText(task.getTitle());
        descriptionArea.setText(task.getDescription());
        statusComboBox.setValue(task.getStatus());
        priorityComboBox.setValue(task.getPriority());
        if (task.getStartDate() != null) {
            startDatePicker.setValue(task.getStartDate());
        }
        if (task.getDeadline() != null) {
            deadlinePicker.setValue(task.getDeadline());
        }
    }

    @FXML
    private void handleSave() {
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        TaskStatus status = statusComboBox.getValue();
        String priority = priorityComboBox.getValue();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate deadline = deadlinePicker.getValue();

        if (title.isEmpty()) {
            AlertUtil.showError("Validation Error", "Please enter a task title");
            return;
        }

        if (title.length() < 3) {
            AlertUtil.showError("Validation Error", "Title must be at least 3 characters long");
            return;
        }

        if (startDate != null && deadline != null && startDate.isAfter(deadline)) {
            AlertUtil.showError("Validation Error", "Start date cannot be after deadline date");
            return;
        }

        if (deadline != null && deadline.isBefore(LocalDate.now())) {
            boolean confirm = AlertUtil.showConfirmation("Past Deadline",
                    "The deadline you selected is in the past. Do you want to continue?");
            if (!confirm) {
                return;
            }
        }

        boolean success;

        if (isEditMode && currentTask != null) {
            currentTask.setTitle(title);
            currentTask.setDescription(description);
            currentTask.setStatus(status);
            currentTask.setPriority(priority);
            currentTask.setStartDate(startDate);
            currentTask.setDeadline(deadline);

            success = taskDAO.updateTask(currentTask);

            if (success) {
                AlertUtil.showSuccess("Success", "Task updated successfully!");
                handleBack();
            } else {
                AlertUtil.showError("Error", "Failed to update task. Please try again.");
            }
        } else {
            Task newTask = new Task();
            newTask.setTitle(title);
            newTask.setDescription(description);
            newTask.setStatus(status);
            newTask.setPriority(priority);
            newTask.setStartDate(startDate);
            newTask.setDeadline(deadline);
            newTask.setUserId(Session.getInstance().getCurrentUserId());

            success = taskDAO.createTask(newTask);

            if (success) {
                AlertUtil.showSuccess("Success", "Task created successfully!");
                handleBack();
            } else {
                AlertUtil.showError("Error", "Failed to create task. Please try again.");
            }
        }
    }

    @FXML
    private void handleClear() {
        titleField.clear();
        descriptionArea.clear();
        statusComboBox.setValue(TaskStatus.TODO);
        priorityComboBox.setValue("MEDIUM");
        startDatePicker.setValue(null);
        deadlinePicker.setValue(null);
        prefilledStartDate = null;
    }

    @FXML
    private void handleBack() {
        SceneManager.switchScene("CalendarView.fxml");
    }
}
