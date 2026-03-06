package com.gestao.financeira.infrastructure.security;

import com.gestao.financeira.domain.port.out.PasswordEncoderPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Adapter que conecta o PasswordEncoderPort (domínio) ao BCryptPasswordEncoder (Spring Security).
 *
 * CAMADA: Infrastructure
 * IMPLEMENTA: PasswordEncoderPort (domain port out)
 *
 * POR QUE existe esta classe "wrapper"?
 *   O BCryptPasswordEncoder é do Spring Security. A camada application/domain
 *   não deve importá-lo diretamente. Este adapter faz a ponte:
 *
 *   UserService → PasswordEncoderPort (interface) → BcryptPasswordEncoderAdapter → BCryptPasswordEncoder
 *
 *   Se um dia você trocar BCrypt por Argon2, muda APENAS esta classe.
 */
@Component
public class BcryptPasswordEncoderAdapter implements PasswordEncoderPort {

    private final BCryptPasswordEncoder encoder;

    public BcryptPasswordEncoderAdapter(BCryptPasswordEncoder encoder) {
        this.encoder = encoder;
    }

    @Override
    public String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}
