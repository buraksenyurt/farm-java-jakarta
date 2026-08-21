package com.lectures.entity;

import jakarta.json.bind.annotation.JsonbDateFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

// Plain Old Java Object (POJO)
@Entity
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotEmpty(message = "Task cannot be empty")
    @Size(min = 20, message = "Task should not be less than 20 chars")
    @Size(max = 50, message = "Task should not be more than 50 chars")
    private String task;
    
    private LocalDate dateCreated;
    
    @NotNull(message = "Due date cannot be empty")
    @FutureOrPresent(message = "Due date must be in present or feature")
    @JsonbDateFormat(value = "yyyy-MM-dd")
    private LocalDate dueDate;
    
    private LocalDate dateCompleted;
    
    private boolean isCompleted;

    @PrePersist
    private void init() {
        setDateCreated(LocalDate.now());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public LocalDate getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDate dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getDateCompleted() {
        return dateCompleted;
    }

    public void setDateCompleted(LocalDate dateCompleted) {
        this.dateCompleted = dateCompleted;
    }

    public boolean isIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }
}
