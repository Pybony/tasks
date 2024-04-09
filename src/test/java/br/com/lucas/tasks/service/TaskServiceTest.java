package br.com.lucas.tasks.service;

import br.com.lucas.tasks.model.Task;
import br.com.lucas.tasks.repository.TaskCustomRepository;
import br.com.lucas.tasks.repository.TaskRepository;
import br.com.lucas.tasks.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static reactor.test.StepVerifier.create;

@SpringBootTest
public class TaskServiceTest {

    @InjectMocks
    private TaskService service;

    @Mock
    private TaskRepository repository;

    @Mock
    private TaskCustomRepository customRepository;

    @Test
    void service_mustReturnTask_whenInsertSucessfully() {
        Task task = TestUtils.buildValidTask();

        when(repository.save(any())).thenReturn(Mono.just(task));

        create(service.insert(task))
                .then(() -> verify(repository, times(1))
                        .save(any()))
                .expectNext(task)
                .expectComplete();

    }

    @Test
    void service_mustReturnVoid_whenDeleteTaskSucessfully() {
        when(repository.deleteById(anyString()))
                .thenReturn(Mono.empty());

        create(service.deleteById("someId"))
                .then(() -> verify(repository, times(1))
                        .deleteById(anyString()))
                .expectComplete();
    }

    @Test
    void service_mustReturnTaskPage_whenFindPaginated() {
        Task task = TestUtils.buildValidTask();

        when(customRepository.findPaginated(any(), anyInt(), anyInt()))
                .thenReturn(Mono.just(Page.empty()));

        Mono<Page<Task>> result = service.findPaginated(task, 0, 10);

        assertNotNull(result);

        verify(customRepository, times(1))
                .findPaginated(any(), anyInt(), anyInt());
    }

}
