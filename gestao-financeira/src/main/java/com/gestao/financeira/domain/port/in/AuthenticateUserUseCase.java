package com.gestao.financeira.domain.port.in;

/**
 * Port IN — caso de uso de autenticação (login).
 *
 * Recebe email e senha em texto puro. Se válidos, retorna um token JWT.
 * Se inválidos, lança exceção.
 *
 * POR QUE retorna String (token) e não User?
 *   O controller precisa do TOKEN para enviar ao frontend, não do User.
 *   Retornar User exporia o hash da senha desnecessariamente.
 *   O token já contém as informações necessárias (email, expiração).
 */
public interface AuthenticateUserUseCase {

    String authenticate(String email, String password);
}
