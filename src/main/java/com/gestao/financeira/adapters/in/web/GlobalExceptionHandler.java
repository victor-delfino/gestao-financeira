package com.gestao.financeira.adapters.in.web;

import com.gestao.financeira.domain.exception.DomainException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Tratamento centralizado de exceções para os controllers REST.
 *
 * CAMADA: Adapter IN (adapters/in/web)
 * POR QUE EXISTE: Cada tipo de exceção deve gerar uma resposta HTTP diferente.
 *   Sem este handler, qualquer erro retornaria HTTP 500 indiscriminadamente.
 *   Aqui traduzimos exceções para respostas HTTP semânticas.
 *
 * @RestControllerAdvice: intercepta exceções lançadas por qualquer
 *   @RestController da aplicação antes de chegar ao cliente.
 *
 * MAPEAMENTO DE EXCEÇÕES:
 *   DomainException              → 400 Bad Request  (regra de negócio violada)
 *   MethodArgumentNotValidException → 400 Bad Request  (@Valid falhou no DTO)
 *   Exception (genérica)         → 500 Internal Server Error (erro inesperado)
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // =========================================================
    // Erros de regras de negócio (domínio)
    // =========================================================

    /**
     * Captura DomainException — violação de regra de negócio.
     *
     * Exemplo: tentar criar transaction com amount negativo.
     * O domínio lança DomainException, chegamos aqui, retornamos 400.
     */
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<Map<String, Object>> handleDomainException(DomainException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // =========================================================
    // Erros de validação do DTO (@Valid no Controller)
    // =========================================================

    /**
     * Captura erros de validação Bean Validation (@NotNull, @NotBlank, etc.)
     * Extrai todas as mensagens de erro dos campos inválidos.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            MethodArgumentNotValidException ex) {

        // Coleta todos os erros de campo em uma lista de mensagens
        String errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .reduce("", (a, b) -> a.isEmpty() ? b : a + " | " + b);

        return buildErrorResponse(HttpStatus.BAD_REQUEST, errors);
    }

    // =========================================================
    // Erros inesperados (fallback)
    // =========================================================

    /**
     * Captura qualquer outra exceção não tratada.
     * Retorna 500 sem expor detalhes internos ao cliente.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro interno. Tente novamente mais tarde."
        );
    }

    // =========================================================
    // Helper — monta o corpo padrão de erro
    // =========================================================

    private ResponseEntity<Map<String, Object>> buildErrorResponse(
            HttpStatus status, String message) {

        // Corpo JSON padronizado para todos os erros:
        // { "timestamp": "...", "status": 400, "error": "Bad Request", "message": "..." }
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);

        return ResponseEntity.status(status).body(body);
    }
}
