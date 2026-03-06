import axios from 'axios';

/**
 * Serviço de autenticação — comunica com /api/auth do backend.
 *
 * POR QUE EXISTE: Centraliza login e registro em um lugar.
 *   Os componentes React (LoginPage, RegisterPage) chamam
 *   este serviço e recebem o token JWT de volta.
 *
 * O token é armazenado no localStorage para persistir entre
 * recarregamentos de página. O axios interceptor (transactionService)
 * lê o token daqui para enviar no header Authorization.
 */

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// ─── Tipos ────────────────────────────────────────────────────────

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  name: string;
  email: string;
}

// ─── Serviço ──────────────────────────────────────────────────────

const authService = {

  /**
   * Registra um novo usuário.
   * POST /api/auth/register
   * Retorna token + dados do usuário.
   */
  register: async (data: RegisterRequest): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>('/auth/register', data);
    return response.data;
  },

  /**
   * Autentica um usuário existente.
   * POST /api/auth/login
   * Retorna token + dados do usuário.
   */
  login: async (data: LoginRequest): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>('/auth/login', data);
    return response.data;
  },

  /**
   * Salva os dados de autenticação no localStorage.
   * Chamado após login ou registro bem-sucedido.
   */
  saveAuth: (authResponse: AuthResponse): void => {
    localStorage.setItem('token', authResponse.token);
    localStorage.setItem('userName', authResponse.name);
    localStorage.setItem('userEmail', authResponse.email);
  },

  /**
   * Remove os dados de autenticação do localStorage.
   * Chamado no logout.
   */
  logout: (): void => {
    localStorage.removeItem('token');
    localStorage.removeItem('userName');
    localStorage.removeItem('userEmail');
  },

  /**
   * Retorna o token JWT salvo, ou null se não estiver autenticado.
   */
  getToken: (): string | null => {
    return localStorage.getItem('token');
  },

  /**
   * Verifica se o usuário está autenticado (tem token salvo).
   */
  isAuthenticated: (): boolean => {
    return !!localStorage.getItem('token');
  },

  /**
   * Retorna o nome do usuário logado.
   */
  getUserName: (): string | null => {
    return localStorage.getItem('userName');
  },

  /**
   * Retorna o email do usuário logado.
   */
  getUserEmail: (): string | null => {
    return localStorage.getItem('userEmail');
  },
};

export default authService;
