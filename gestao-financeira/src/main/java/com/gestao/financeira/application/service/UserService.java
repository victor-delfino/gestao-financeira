package com.gestao.financeira.application.service;

import com.gestao.financeira.domain.exception.DomainException;
import com.gestao.financeira.domain.model.User;
import com.gestao.financeira.domain.port.in.AuthenticateUserUseCase;
import com.gestao.financeira.domain.port.in.RegisterUserUseCase;
import com.gestao.financeira.domain.port.out.PasswordEncoderPort;
import com.gestao.financeira.domain.port.out.TokenPort;
import com.gestao.financeira.domain.port.out.UserRepositoryPort;

/**
 * Implementação dos casos de uso de autenticação.
 *
 * CAMADA: Application
 * IMPLEMENTA: RegisterUserUseCase, AuthenticateUserUseCase
 *
 * DEPENDÊNCIAS (todas via Port — inversão de dependência):
 *   - UserRepositoryPort: salvar/buscar usuários
 *   - PasswordEncoderPort: hash BCrypt (sem importar Spring Security)
 *   - TokenPort: gerar JWT (sem importar JJWT)
 *
 * POR QUE não tem @Service?
 *   Mesma razão do TransactionService: é a BeanConfiguration (infra)
 *   que cria este bean e injeta as dependências. O Service não conhece Spring.
 *
 * FLUXO DE REGISTRO:
 *   1. Verifica se email já existe → lança exceção se sim
 *   2. Faz hash da senha com BCrypt via PasswordEncoderPort
 *   3. Cria entidade User (validação de domínio acontece aqui)
 *   4. Salva no banco via UserRepositoryPort
 *
 * FLUXO DE LOGIN:
 *   1. Busca usuário por email → lança exceção se não encontrado
 *   2. Compara senha fornecida com hash armazenado via PasswordEncoderPort
 *   3. Se válido, gera token JWT via TokenPort
 *   4. Retorna o token (String)
 */
public class UserService implements RegisterUserUseCase, AuthenticateUserUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenPort tokenPort;

    public UserService(UserRepositoryPort userRepository,
                       PasswordEncoderPort passwordEncoder,
                       TokenPort tokenPort) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenPort = tokenPort;
    }

    // ─── REGISTRO ────────────────────────────────────────────────────

    @Override
    public User execute(RegisterUserCommand command) {
        // 1. Verifica duplicata de email
        if (userRepository.existsByEmail(command.getEmail())) {
            throw new DomainException("Já existe um usuário com este email.");
        }

        // 2. Hash da senha com BCrypt
        // NUNCA salvamos a senha em texto puro no banco!
        String hashedPassword = passwordEncoder.encode(command.getPassword());

        // 3. Cria a entidade de domínio (validações do construtor rodam aqui)
        User user = new User(
            command.getName(),
            command.getEmail(),
            hashedPassword
        );

        // 4. Persiste e retorna
        return userRepository.save(user);
    }

    // ─── LOGIN ───────────────────────────────────────────────────────

    @Override
    public String authenticate(String email, String password) {
        // 1. Busca o usuário pelo email
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new DomainException("Email ou senha inválidos."));

        // 2. Compara a senha fornecida com o hash armazenado
        // passwordEncoder.matches() faz: BCrypt.check(rawPassword, hashedPassword)
        if (!passwordEncoder.matches(password, user.getPassword())) {
            // ATENÇÃO: mesma mensagem genérica para email e senha errados.
            // Nunca diga "senha incorreta" ou "email não encontrado" separadamente.
            // Isso evita que atacantes descubram quais emails estão cadastrados.
            throw new DomainException("Email ou senha inválidos.");
        }

        // 3. Credenciais válidas — gera o token JWT
        return tokenPort.generateToken(user.getEmail());
    }
}
