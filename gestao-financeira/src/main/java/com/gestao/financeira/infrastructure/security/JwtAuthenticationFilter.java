package com.gestao.financeira.infrastructure.security;

import com.gestao.financeira.domain.port.out.TokenPort;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * Filtro JWT que intercepta TODAS as requisições HTTP.
 *
 * CAMADA: Infrastructure (security)
 *
 * ═══════════════════════════════════════════════════════════════
 *  COMO FUNCIONA O FILTRO
 * ═══════════════════════════════════════════════════════════════
 *
 *  O Spring Security funciona com uma CADEIA DE FILTROS.
 *  Cada request HTTP passa por uma série de filtros antes de
 *  chegar ao controller.
 *
 *  Este filtro faz:
 *  1. Lê o header "Authorization" da requisição
 *  2. Se contém "Bearer <token>", extrai o token
 *  3. Valida o token (assinatura + expiração)
 *  4. Se válido, seta a autenticação no SecurityContext
 *  5. O request continua para o próximo filtro/controller
 *
 *  Se o token for inválido ou ausente, o request continua
 *  SEM autenticação. O Spring Security vai barrar depois se
 *  o endpoint exigir autenticação.
 *
 *  OncePerRequestFilter: garante que o filtro roda UMA vez por request,
 *  mesmo em forwards/includes internos.
 *
 *  UsernamePasswordAuthenticationToken: é a forma padrão do Spring Security
 *  de representar "este usuário está autenticado". O nome é confuso,
 *  mas pode ser usado com qualquer mecanismo de autenticação (incluindo JWT).
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenPort tokenPort;

    public JwtAuthenticationFilter(TokenPort tokenPort) {
        this.tokenPort = tokenPort;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
                                    throws ServletException, IOException {

        // 1. Extrai o header "Authorization"
        String authHeader = request.getHeader("Authorization");

        // 2. Verifica se é um token Bearer
        //    Formato esperado: "Bearer eyJhbGci..."
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // remove "Bearer "

            // 3. Valida o token
            if (tokenPort.isTokenValid(token)) {
                String email = tokenPort.extractEmail(token);

                // 4. Cria o objeto de autenticação do Spring Security
                //    - principal: o email do usuário
                //    - credentials: null (não precisamos da senha após autenticação)
                //    - authorities: lista vazia (sem roles por enquanto)
                var authentication = new UsernamePasswordAuthenticationToken(
                    email,                    // principal — quem é o usuário
                    null,                     // credentials — não necessário aqui
                    Collections.emptyList()   // authorities — roles/permissões
                );

                // 5. Registra a autenticação no contexto do Spring Security
                //    A partir daqui, SecurityContextHolder.getContext()
                //    .getAuthentication() retorna este objeto.
                //    O Spring Security sabe que o usuário está autenticado.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // 6. Continua a cadeia de filtros (chega no controller)
        filterChain.doFilter(request, response);
    }
}
