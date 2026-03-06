package com.gestao.financeira.adapters.in.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para registro de novo usuário.
 *
 * CAMADA: Adapter IN (web/dto)
 *
 * Validações de FORMATO (Bean Validation):
 *   - @NotBlank: campo obrigatório e não pode ser espaços em branco
 *   - @Email: formato de email válido
 *   - @Size(min = 6): senha com no mínimo 6 caracteres
 *
 * Validações de NEGÓCIO ficam no domínio (User) e no service (email duplicado).
 */
public class RegisterRequest {

    @NotBlank(message = "O nome é obrigatório.")
    private String name;

    @NotBlank(message = "O email é obrigatório.")
    @Email(message = "O email deve ser válido.")
    private String email;

    @NotBlank(message = "A senha é obrigatória.")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.")
    private String password;

    // Construtor vazio para Jackson (deserialização JSON)
    public RegisterRequest() {}

    public RegisterRequest(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public String getName()     { return name; }
    public String getEmail()    { return email; }
    public String getPassword() { return password; }
}
