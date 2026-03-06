package com.gestao.financeira.domain.port.in;

import com.gestao.financeira.application.service.RegisterUserCommand;
import com.gestao.financeira.domain.model.User;

/**
 * Port IN — caso de uso de registro de usuário.
 *
 * O controller chama esta interface para registrar um novo usuário.
 * A implementação (UserService) faz o hash da senha e salva.
 */
public interface RegisterUserUseCase {

    User execute(RegisterUserCommand command);
}
