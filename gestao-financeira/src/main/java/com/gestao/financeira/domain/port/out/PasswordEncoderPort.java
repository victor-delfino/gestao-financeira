package com.gestao.financeira.domain.port.out;

/**
 * Port OUT — abstração para codificação de senhas.
 *
 * CAMADA: Domain (porta de saída)
 * POR QUE EXISTE: O UserService precisa fazer hash de senhas, mas NÃO
 *   deve depender de Spring Security (PasswordEncoder) diretamente.
 *   Esta interface isola o domínio/application do framework.
 *
 * IMPLEMENTAÇÃO: A BeanConfiguration registra um adapter que delega
 *   para BCryptPasswordEncoder do Spring Security.
 *
 * POR QUE não usar PasswordEncoder do Spring diretamente?
 *   Porque nosso Service está na camada APPLICATION, que não deve
 *   importar classes de framework (Spring Security). A interface
 *   permite trocar BCrypt por Argon2 ou qualquer outro algoritmo
 *   sem modificar o Service.
 */
public interface PasswordEncoderPort {

    /**
     * Codifica uma senha em texto puro para um hash seguro.
     */
    String encode(String rawPassword);

    /**
     * Verifica se a senha em texto puro corresponde ao hash.
     */
    boolean matches(String rawPassword, String encodedPassword);
}
