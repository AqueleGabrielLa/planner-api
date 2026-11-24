package com.gabriel.plannerapi.user;

import com.gabriel.plannerapi.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class UserAccountController {
    private final UserAccountService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserAccount usuarioAutenticado
    ){
        if (!usuarioAutenticado.getId().equals(id)) {
            throw new ResourceNotFoundException("Usuário não encontrado");
        }

        return userService.findById(id)
                .map(user -> new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getCreatedAt()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
