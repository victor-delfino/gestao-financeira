package com.gestao.financeira.domain.exception;

/**
 * Exceção base para violações de regras de negócio.
 *
 * CAMADA: Domain
 * POR QUE EXISTE: Distingue erros de NEGÓCIO de erros técnicos.
 *   Quando o domínio rejeita um estado inválido (ex: valor negativo),
 *   ele lança esta exceção. O adapter de entrada (Controller) saberá
 *   que deve responder com HTTP 400 Bad Request.
 *   Se fosse um NullPointerException, seria HTTP 500 (erro do sistema).
 *
 * DEPENDÊNCIAS PERMITIDAS: nenhuma (estende RuntimeException do Java)
 *
 * RuntimeException: não exige try/catch forçado (unchecked exception).
 *   Exceções de domínio geralmente são unchecked porque representam
 *   estados que "não deveriam acontecer" se o cliente validar corretamente.
 */
public class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }
}
