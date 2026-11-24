package com.gabriel.plannerapi.task;

import com.gabriel.plannerapi.exception.ResourceNotFoundException;
import com.gabriel.plannerapi.user.UserAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    public List<TaskResponse> getAllTasks(UserAccount user){
        return taskRepository.findByUser(user)
                .stream()
                .map(task -> new TaskResponse(task.getId(), task.getTitle(), task.getDescription(), task.isCompleted(), task.getCreatedAt()))
                .toList();
    }

    public TaskResponse getTaskById(UUID id, UserAccount user){
        Task task = taskRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Task com id " + id + " não encontrada"));

        return new TaskResponse(task.getId(), task.getTitle(), task.getDescription(), task.isCompleted(), task.getCreatedAt());
    }

    public TaskResponse createTask(TaskRequest request, UserAccount user){
        Task task = Task.builder()
                .title(request.title())
                .description(request.description())
                .user(user)
                .build();

        Task saved = taskRepository.save(task);

        return new TaskResponse(saved.getId(), saved.getTitle(), saved.getDescription(), saved.isCompleted(), saved.getCreatedAt());
    }

    public TaskResponse updateTask(UUID id, TaskPatchRequest request, UserAccount user){
        Task task = taskRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Task com id " + id + " não encontrada"));

        if (request.title() != null) task.setTitle(request.title());
        if (request.description() != null) task.setDescription(request.description());
        if (request.completed() != null) task.setCompleted(request.completed());

        Task saved = taskRepository.save(task);

        return new TaskResponse(saved.getId(), saved.getTitle(), saved.getDescription(), saved.isCompleted(), saved.getCreatedAt());
    }

    public void deleteTask(UUID id, UserAccount user){
        Task task = taskRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Task com id " + id + " não encontrada"));

        taskRepository.delete(task);
    }

}
