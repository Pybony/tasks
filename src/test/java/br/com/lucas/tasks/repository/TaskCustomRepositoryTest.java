package br.com.lucas.tasks.repository;

import br.com.lucas.tasks.model.Task;
import br.com.lucas.tasks.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.MongoOperations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class TaskCustomRepositoryTest {

    @InjectMocks
    private TaskCustomRepository repository;

    @Mock
    private MongoOperations mongoOperations;

    @Test
    void customRepository_mustReturnPageWithOneElement_whenSendTask() {
        Task task = TestUtils.buildValidTask();

        when(mongoOperations.find(any(), any())).thenReturn(List.of(task));

        Page<Task> result = repository.findPaginated(task, 0 , 10);

        assertNotNull(result);
        assertEquals(1, result.getNumberOfElements());
    }
}
