package br.com.lucas.tasks.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Range;

public class TaskUpdateDTO {

    @NotBlank(message = "{invalid.id}")
    private String id;

    @NotBlank(message = "{blank.title}")
    @Size(min = 3, max = 20, message = "{size.title}")
    private String title;

    @NotBlank(message = "{blank.description}")
    @Size(min = 10, max = 50, message = "{size.description}")
    private String description;

    @Range(min = 1, message = "{range.priority}")
    private int priority;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

}
