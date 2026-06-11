package com.gabriel.plannerapi.task;

import com.gabriel.plannerapi.exception.ResourceNotFoundException;
import com.gabriel.plannerapi.user.UserAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    TaskRepository taskRepository;

    @InjectMocks
    TaskService taskService;

    private UserAccount user;
    private Task task;
    private UUID taskId;

    @BeforeEach
    void setUp(){
        taskId = UUID.randomUUID();

        user = new UserAccount();
        user.setId(UUID.randomUUID());

        task = Task.builder()
                .id(taskId)
                .title("Estudar JUNIT")
                .description("Aprendendo sobre Mockito")
                .completed(false)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // --- getTaskById ---

    @Test
    void getTaskById_deveRetornarTask_quandoExiste(){
        when(taskRepository.findByIdAndUser(taskId, user))
                .thenReturn(Optional.of(task));

        TaskReponse response = taskService.getTaskById(taskId, user);

        assertThat(response.id()).isEqualTo(taskId);
        assertThat(response.title()).isEqualTo("Estudar JUNIT");
    }

    @Test
    void getTaskById_deveLancarExcecao_quandoNaoExiste(){
        when(taskRepository.findByIdAndUser(taskId, user))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTaskById(taskId, user))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(taskId.toString());
    }

    // --- createTask ---

    @Test
    void createTask_deveSalvarERetornarTask(){
        TaskRequest request = new TaskRequest("Nova task", "descrição");

        Task taskSalva = Task.builder()
                .id(UUID.randomUUID())
                .title(request.title())
                .description(request.description())
                .completed(false)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        when(taskRepository.save(any(Task.class))).thenReturn(taskSalva);

        TaskReponse response = taskService.createTask(request, user);

        assertThat(response.title()).isEqualTo("Nova task");
        assertThat(response.completed()).isFalse();
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    // --- updateTask ---

    @Test
    void updateTask_deveAtualizarCamposEnviados(){
        TaskPatchRequest request = new TaskPatchRequest("Titulo atualizado", null, true);

        when(taskRepository.findByIdAndUser(taskId, user)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskReponse response = taskService.updateTask(taskId, request, user);

        assertThat(response.title()).isEqualTo("Titulo atualizado");
        verify(taskRepository).save(task);
    }

    @Test
    void updateTask_deveLancarExcecao_quandoNaoExiste() {
        TaskPatchRequest request = new TaskPatchRequest("Título", null, null);

        when(taskRepository.findByIdAndUser(taskId, user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.updateTask(taskId, request, user))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // --- deleteTask ---

    @Test
    void deleteTask_deveDeletar_quandoExiste() {
        when(taskRepository.findByIdAndUser(taskId, user)).thenReturn(Optional.of(task));

        taskService.deleteTask(taskId, user);

        verify(taskRepository, times(1)).delete(task);
    }

    @Test
    void deleteTask_deveLancarExcecao_quandoNaoExiste() {
        when(taskRepository.findByIdAndUser(taskId, user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.deleteTask(taskId, user))
                .isInstanceOf(ResourceNotFoundException.class);
    }

}
