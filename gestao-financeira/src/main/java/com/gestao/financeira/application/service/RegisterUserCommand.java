package com.gestao.financeira.application.service;

/**
 * Comando de entrada para o caso de uso de registro.
 *
 * CAMADA: Application
 * POR QUE EXISTE: Transporta os dados do controller para o use case
 *   sem que o domínio conheça DTOs da camada web.
 *
 * A senha aqui é TEXTO PURO. O UserService fará o hash com BCrypt
 * antes de criar a entidade User.
 */
public class RegisterUserCommand {

    private final String name;
    private final String email;
    private final String password; // texto puro — hash é feito no service

    public RegisterUserCommand(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public String getName()     { return name; }
    public String getEmail()    { return email; }
    public String getPassword() { return password; }
}
