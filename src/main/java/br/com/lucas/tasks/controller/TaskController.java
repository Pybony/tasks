package br.com.lucas.tasks.controller;

import br.com.lucas.tasks.controller.converter.TaskConverter;
import br.com.lucas.tasks.controller.converter.TaskInsertDTOConverter;
import br.com.lucas.tasks.controller.converter.TaskUpdateDTOConverter;
import br.com.lucas.tasks.controller.dto.TaskDTO;
import br.com.lucas.tasks.controller.dto.TaskInsertDTO;
import br.com.lucas.tasks.controller.dto.TaskUpdateDTO;
import br.com.lucas.tasks.model.TaskState;
import br.com.lucas.tasks.service.TaskService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/task")
public class TaskController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskController.class);

    private final TaskService service;
    private final TaskConverter converter;
    private final TaskInsertDTOConverter insertDTOConverter;
    private final TaskUpdateDTOConverter updateDTOConverter;

    public TaskController(TaskService service,
                          TaskConverter converter,
                          TaskInsertDTOConverter insertDTOConverter,
                          TaskUpdateDTOConverter updateDTOConverter) {
        this.service = service;
        this.converter = converter;
        this.insertDTOConverter = insertDTOConverter;
        this.updateDTOConverter = updateDTOConverter;
    }

    @GetMapping
    public Mono<Page<TaskDTO>> getTasks(@RequestParam(required = false) String id,
                                        @RequestParam(required = false) String title,
                                        @RequestParam(required = false) String description,
                                        @RequestParam(required = false, defaultValue = "0") int priority,
                                        @RequestParam(required = false) TaskState taskState,
                                        @RequestParam(value = "pageNumber", defaultValue = "0") Integer pagenumber,
                                        @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return service.findPaginated(converter.convert(id, title, description, priority, taskState), pagenumber, pageSize)
                .map(it -> it.map(converter::convert));
    }

    @PostMapping
    public Mono<TaskDTO> createTask(@RequestBody @Valid TaskInsertDTO taskInsertDTO) {
        return service.insert(insertDTOConverter.convert(taskInsertDTO))
                .doOnNext(task -> LOGGER.info("Saved task with id {}", task.getId()))
                .map(converter::convert);
    }

    @PutMapping
    public Mono<TaskDTO> updateTask(@RequestBody @Valid TaskUpdateDTO taskUpdateDTO) {
        return service.updateTask(updateDTOConverter.convert(taskUpdateDTO))
                .doOnNext(it -> LOGGER.info("Update task with id {}", it.getId()))
                .map(converter::convert);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable String id) {
        return Mono.just(id)
                .doOnNext(it -> LOGGER.info("Deleting task with id {}", it))
                .flatMap(service::deleteById);
    }

}
