package com.gestao.financeira.adapters.in.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO de entrada para login (autenticação).
 *
 * CAMADA: Adapter IN (web/dto)
 * Apenas email e senha — sem nome (já está cadastrado).
 */
public class LoginRequest {

    @NotBlank(message = "O email é obrigatório.")
    @Email(message = "O email deve ser válido.")
    private String email;

    @NotBlank(message = "A senha é obrigatória.")
    private String password;

    public LoginRequest() {}

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail()    { return email; }
    public String getPassword() { return password; }
}
