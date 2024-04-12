package br.com.lucas.tasks.controller.converter;

import br.com.lucas.tasks.controller.dto.TaskDTO;
import br.com.lucas.tasks.model.Task;
import br.com.lucas.tasks.model.TaskState;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TaskConverter {

    public TaskDTO convert(Task task) {
        return Optional.ofNullable(task)
                .map(source -> {
                    TaskDTO dto = new TaskDTO();
                    dto.setId(source.getId());
                    dto.setTitle(source.getTitle());
                    dto.setDescription(source.getDescription());
                    dto.setPriority(source.getPriority());
                    dto.setState(source.getState());
                    dto.setAddress(source.getAddress());
                    return dto;
                })
                .orElse(null);
    }

    public Task convert(TaskDTO dto) {
        return Optional.ofNullable(dto)
                .map(source -> {
                    System.out.println("========> " + source.getId());
                    return Task.builder()
                        .withId(source.getId())
                        .withTitle(source.getTitle())
                        .withDescription(source.getDescription())
                        .withPriority(source.getPriority())
                        .withState(source.getState())
                        .build();})
                .orElse(null);
    }

    public Task convert(String id, String title, String description, int priority, TaskState taskState){
        return Task.builder()
                .withId(id)
                .withTitle(title)
                .withDescription(description)
                .withPriority(priority)
                .withState(taskState)
                .build();
    }

}
