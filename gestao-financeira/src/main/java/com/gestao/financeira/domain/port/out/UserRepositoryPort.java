package com.gestao.financeira.domain.port.out;

import com.gestao.financeira.domain.model.User;

import java.util.Optional;

/**
 * Port OUT — interface de persistência de usuários.
 *
 * CAMADA: Domain (porta de saída)
 * IMPLEMENTAÇÃO: UserJpaAdapter em adapters/out/persistence
 *
 * Métodos:
 *   - save: persiste um novo usuário
 *   - findByEmail: busca por email (usado no login e para evitar duplicatas)
 *   - existsByEmail: verifica existência sem carregar o objeto completo
 *
 * POR QUE Optional<User>?
 *   findByEmail pode não encontrar ninguém. Optional força quem chama
 *   a tratar o caso "não encontrado" explicitamente, evitando NullPointerException.
 */
public interface UserRepositoryPort {

    User save(User user);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
