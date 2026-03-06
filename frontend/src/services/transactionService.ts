import axios from 'axios';
import type { CreateTransactionRequest, TransactionResponse } from '../types/transaction';

/**
 * Serviço de comunicação com a API do backend.
 *
 * POR QUE EXISTE: Centraliza toda a lógica de chamadas HTTP em um lugar.
 *   Os componentes React não fazem fetch/axios diretamente —
 *   eles chamam funções deste serviço.
 *   É o "adapter" do frontend: isola os componentes do protocolo HTTP.
 *
 * Se a URL da API mudar, ou se trocar axios por fetch nativo,
 * só muda aqui — nenhum componente precisa ser alterado.
 */

/**
 * Instância configurada do axios.
 * baseURL aponta para o backend Spring Boot (porta 8080).
 * Nos componentes: transactionService.create(data) → já usa a URL base.
 */
const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

const transactionService = {

  /**
   * Cria uma nova transação.
   * POST /api/transactions
   * Retorna a transação criada com o ID gerado pelo backend.
   */
  create: async (data: CreateTransactionRequest): Promise<TransactionResponse> => {
    const response = await api.post<TransactionResponse>('/transactions', data);
    return response.data;
  },

  /**
   * Lista todas as transações.
   * GET /api/transactions
   */
  listAll: async (): Promise<TransactionResponse[]> => {
    const response = await api.get<TransactionResponse[]>('/transactions');
    return response.data;
  },

  /**
   * Calcula o saldo atual (receitas - despesas).
   * GET /api/transactions/balance
   */
  getBalance: async (): Promise<number> => {
    const response = await api.get<number>('/transactions/balance');
    return response.data;
  },
};

export default transactionService;
