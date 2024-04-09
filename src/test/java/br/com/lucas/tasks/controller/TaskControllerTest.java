package br.com.lucas.tasks.controller;

import br.com.lucas.tasks.controller.converter.TaskConverter;
import br.com.lucas.tasks.controller.converter.TaskInsertDTOConverter;
import br.com.lucas.tasks.controller.dto.TaskDTO;
import br.com.lucas.tasks.controller.dto.TaskInsertDTO;
import br.com.lucas.tasks.model.Task;
import br.com.lucas.tasks.service.TaskService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@SpringBootTest
public class TaskControllerTest {

    @InjectMocks
    private TaskController controller;

    @Mock
    private TaskService service;

    @Mock
    private TaskConverter converter;

    @Mock
    private TaskInsertDTOConverter insertDTOConverter;

    @Test
    public void controller_mustReturnOK_whenSaveSucessfully() {
        when(converter.convert(any(Task.class)))
                .thenReturn(new TaskDTO());
        when(service.insert(any()))
                .thenReturn(Mono.just(new Task()));

        WebTestClient client = WebTestClient.bindToController(controller).build();

        client.post()
                .uri("/task")
                .bodyValue(new TaskInsertDTO())
                .exchange()
                .expectStatus().isOk()
                .expectBody(TaskDTO.class);
    }

    @Test
    public void controller_mustReturnNoContent_whenDeleteSucessfully() {
        String taskId = "any-id";

        when(service.deleteById(any()))
                .thenReturn(Mono.empty());

        WebTestClient client = WebTestClient.bindToController(controller).build();

        client.delete()
                .uri("/task/" + taskId)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    public void controller_mustReturnOK_whenFindSucessfully() {
        when(service.findPaginated(any(), anyInt(), anyInt()))
                .thenReturn(Mono.just(Page.empty()));

        WebTestClient client = WebTestClient.bindToController(controller).build();

        client.get()
                .uri("/task")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Task.class);
    }

}
