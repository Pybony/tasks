package br.com.lucas.tasks.service;

import br.com.lucas.tasks.exception.TaskNotFoundException;
import br.com.lucas.tasks.message.TaskNotificationProducer;
import br.com.lucas.tasks.model.Address;
import br.com.lucas.tasks.model.Task;
import br.com.lucas.tasks.repository.TaskCustomRepository;
import br.com.lucas.tasks.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;
    private final TaskCustomRepository taskCustomRepository;
    private final AddressService addressService;
    private final TaskNotificationProducer producer;

    public TaskService(TaskRepository taskRepository, TaskCustomRepository taskCustomRepository, AddressService addressService, TaskNotificationProducer producer) {
        this.taskRepository = taskRepository;
        this.taskCustomRepository = taskCustomRepository;
        this.addressService = addressService;
        this.producer = producer;
    }

    public Mono<Task> insert(Task task) {
        return Mono.just(task)
                .map(Task::insert)
                .flatMap(this::save)
                .doOnError(error -> LOGGER.error("Error during save task. Tittle: {}", task.getTitle(), error));
    }

    public Mono<Page<Task>> findPaginated(Task task, Integer pageNumber, Integer pageSize){
        return taskCustomRepository.findPaginated(task, pageNumber, pageSize);
    }

    public Mono<Task> updateTask(Task task) {
        return taskRepository.findById(task.getId())
                .map(task::update)
                .flatMap(taskRepository::save)
                .switchIfEmpty(Mono.error(TaskNotFoundException::new))
                .doOnError(error -> LOGGER.error("Error during update task with id: {}. Message: {}", task.getId(), error.getMessage()));
    }

    public Mono<Void> deleteById(String id) {
        return taskRepository.deleteById(id);
    }

    public Mono<Task> start(String id, String zipcode) {
        return taskRepository.findById(id)
                .zipWhen(it -> addressService.getAddress(zipcode))
                .flatMap(it -> updateAddress(it.getT1(), it.getT2()))
                .map(Task::start)
                .flatMap(taskRepository::save)
                .flatMap(producer::sendNotification)
                .switchIfEmpty(Mono.error(TaskNotFoundException::new))
                .doOnError(error -> LOGGER.error("Error on start task. ID: {}", id, error));
    }

    public Mono<Task> done(Task task) {
        return Mono.just(task)
                .doOnNext(it -> LOGGER.info("Finishing task. ID:{}", task.getId()))
                .map(Task::done)
                .flatMap(taskRepository::save);
    }

    public Mono<List<Task>> doneMany(List<String> ids) {
        return Flux.fromIterable(ids)
                .flatMap(id -> taskRepository.findById(id)
                        .map(Task::done)
                        .flatMap(taskRepository::save))
                .doOnNext(it -> LOGGER.info("Done task. ID: {}", it.getId()))
                .collectList();
    }

    public Flux<Task> refreshCreated() {
        return taskRepository.findAll()
                .filter(Task::createdIsEmpty)
                .map(Task::createdNow)
                .flatMap(taskRepository::save);
    }

    private Mono<Task> updateAddress(Task task, Address address) {
        return Mono.just(task)
                .map(it -> task.updateAddress(address));
    }

    private Mono<Task> save(Task task) {
        return Mono.just(task)
                .doOnNext(t -> LOGGER.info("Saving task with title {}", t.getTitle()))
                .flatMap(taskRepository::save);
    }



}
