package com.gabriel.plannerapi.task;

import com.gabriel.plannerapi.user.UserAccount;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "task")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/")
    public ResponseEntity<List<TaskReponse>> getAllTasks(@AuthenticationPrincipal UserAccount user){
        return ResponseEntity.ok(taskService.getAllTasks(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskReponse> getTaskById(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserAccount user
    ){
        return ResponseEntity.ok(taskService.getTaskById(id, user));
    }

    @PostMapping("/")
    public ResponseEntity<TaskReponse> createTask(
            @RequestBody @Valid TaskRequest request,
            @AuthenticationPrincipal UserAccount user
    ){
        return ResponseEntity.ok(taskService.createTask(request, user));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TaskReponse> updateTask(
            @PathVariable UUID id,
            @RequestBody TaskPatchRequest request,
            @AuthenticationPrincipal UserAccount user
    ) {
        return ResponseEntity.ok(taskService.updateTask(id, request, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserAccount user
    ) {
        taskService.deleteTask(id, user);
        return ResponseEntity.noContent().build();
    }

}
