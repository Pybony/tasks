package br.com.lucas.tasks.controller;

import br.com.lucas.tasks.controller.converter.TaskConverter;
import br.com.lucas.tasks.controller.dto.TaskDTO;
import br.com.lucas.tasks.model.Task;
import br.com.lucas.tasks.model.TaskState;
import br.com.lucas.tasks.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/task")
public class TaskController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskController.class);

    private final TaskService service;
    private final TaskConverter converter;

    public TaskController(TaskService service, TaskConverter converter) {
        this.service = service;
        this.converter = converter;
    }

    @GetMapping
    public Page<TaskDTO> getTasks(@RequestParam(required = false) String id,
                                   @RequestParam(required = false) String title,
                                   @RequestParam(required = false) String description,
                                   @RequestParam(required = false, defaultValue = "0") int priority,
                                   @RequestParam(required = false) TaskState taskState,
                                   @RequestParam(value = "pageNumber", defaultValue = "0") Integer pagenumber,
                                   @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize){
        return service.findPaginated(converter.convert(id, title, description, priority, taskState), pagenumber, pageSize)
                .map(converter::convert);
    }

    @PostMapping
    public Mono<TaskDTO> createTask(@RequestBody TaskDTO taskDTO) {
        return service.insert(converter.convert(taskDTO))
                .doOnNext(task -> LOGGER.info("Saved task with id {}", task.getId()))
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
