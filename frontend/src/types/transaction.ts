/**
 * Tipos TypeScript que espelham os DTOs do backend.
 *
 * POR QUE EXISTE: Define contratos de tipo no frontend, alinhados
 *   com os DTOs Java do backend. Se o backend mudar um campo,
 *   o TypeScript aponta o erro em tempo de compilação.
 *
 * DECISÃO: Tipos separados para Request e Response — mesma razão
 *   do backend: evoluem independentemente.
 */

/** Espelha TransactionType.java (enum do backend) */
export type TransactionType = 'INCOME' | 'EXPENSE';

/**
 * Espelha CreateTransactionRequest.java
 * O que o frontend ENVIA ao backend (sem id).
 */
export interface CreateTransactionRequest {
  description: string;
  amount: number;
  type: TransactionType;
  category: string;
  date: string; // formato ISO: "2026-03-06" (LocalDate no Java)
}

/**
 * Espelha TransactionResponse.java
 * O que o frontend RECEBE do backend (com id).
 */
export interface TransactionResponse {
  id: string;
  description: string;
  amount: number;
  type: TransactionType;
  category: string;
  date: string;
}
