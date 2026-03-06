package com.gestao.financeira.adapters.in.web;

import com.gestao.financeira.adapters.in.web.dto.AuthResponse;
import com.gestao.financeira.adapters.in.web.dto.LoginRequest;
import com.gestao.financeira.adapters.in.web.dto.RegisterRequest;
import com.gestao.financeira.application.service.RegisterUserCommand;
import com.gestao.financeira.domain.model.User;
import com.gestao.financeira.domain.port.in.AuthenticateUserUseCase;
import com.gestao.financeira.domain.port.in.RegisterUserUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller de autenticação — endpoints públicos.
 *
 * CAMADA: Adapter IN (web)
 *
 * Endpoints:
 *   POST /api/auth/register → cria novo usuário, retorna token
 *   POST /api/auth/login    → autentica, retorna token
 *
 * Ambos retornam um AuthResponse com o JWT + dados do usuário.
 * O frontend guarda o token e usa em todas as requisições.
 *
 * Estes endpoints são PÚBLICOS (configurados em SecurityConfig):
 *   .requestMatchers("/api/auth/**").permitAll()
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RegisterUserUseCase registerUseCase;
    private final AuthenticateUserUseCase authenticateUseCase;

    // Injeta as interfaces (Port IN), não a implementação concreta
    public AuthController(RegisterUserUseCase registerUseCase,
                          AuthenticateUserUseCase authenticateUseCase) {
        this.registerUseCase = registerUseCase;
        this.authenticateUseCase = authenticateUseCase;
    }

    /**
     * POST /api/auth/register
     *
     * Fluxo:
     * 1. @Valid valida o RegisterRequest (Bean Validation)
     * 2. Converte DTO → Command (padrão da hexagonal)
     * 3. Chama o use case (RegisterUserUseCase)
     * 4. Se sucesso, também faz login automático (gera token)
     * 5. Retorna 201 Created com AuthResponse
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        // 1. DTO → Command
        RegisterUserCommand command = new RegisterUserCommand(
            request.getName(),
            request.getEmail(),
            request.getPassword()
        );

        // 2. Registra o usuário
        User user = registerUseCase.execute(command);

        // 3. Login automático — gera token JWT logo após o registro
        String token = authenticateUseCase.authenticate(
            request.getEmail(),
            request.getPassword()
        );

        // 4. Retorna 201 com token + dados
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(new AuthResponse(token, user.getName(), user.getEmail()));
    }

    /**
     * POST /api/auth/login
     *
     * Fluxo:
     * 1. @Valid valida o LoginRequest
     * 2. Chama o use case (AuthenticateUserUseCase)
     * 3. Se email/senha válidos, retorna token JWT
     * 4. Se inválidos, o service lança DomainException → 400
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        // 1. Autentica e recebe o token
        String token = authenticateUseCase.authenticate(
            request.getEmail(),
            request.getPassword()
        );

        // 2. Retorna 200 OK com token
        // NOTA: para exibir o nome no frontend, precisamos buscar o usuário.
        // Simplificamos usando o email como nome no login flow.
        // O frontend pode extrair o nome do token ou de um endpoint /api/users/me
        return ResponseEntity.ok(new AuthResponse(token, request.getEmail(), request.getEmail()));
    }
}
