package com.gabriel.plannerapi.auth;

import com.gabriel.plannerapi.exception.EmailAlreadyExistsException;
import com.gabriel.plannerapi.security.JwtService;
import com.gabriel.plannerapi.user.UserAccount;
import com.gabriel.plannerapi.user.UserAccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    UserAccountRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    JwtService jwtService;

    @Mock
    AuthenticationManager authenticationManager;

    @InjectMocks
    AuthService authService;

    @Test
    void register_deveLancarExcecao_quandoEmailJaExiste() {
        RegisterRequest request = new RegisterRequest("Gabriel", "gabriel@email.com", "123456");
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(new UserAccount()));

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining(request.email());

        verify(userRepository, never()).save(any());
        verify(jwtService, never()).generateToken(anyString());
    }
}
