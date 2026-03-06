import { createContext, useContext, useState, useEffect } from 'react';
import type { ReactNode } from 'react';
import authService from '../services/authService';

/**
 * Contexto de autenticação — gerencia o estado logado/deslogado.
 *
 * POR QUE EXISTE: Vários componentes precisam saber se o usuário
 *   está autenticado (header, rotas protegidas, etc.).
 *   O Context do React evita "prop drilling" — passar props
 *   por vários níveis de componentes.
 *
 * COMO FUNCIONA:
 *   1. AuthProvider envolve toda a aplicação (em main.tsx)
 *   2. Qualquer componente filho pode chamar useAuth()
 *   3. Se o token existir no localStorage, o estado já inicia como logado
 *   4. login() e logout() atualizam o estado e o localStorage
 */

interface AuthContextType {
  isAuthenticated: boolean;
  userName: string | null;
  userEmail: string | null;
  login: (token: string, name: string, email: string) => void;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {

  // Inicializa o estado a partir do localStorage
  const [isAuthenticated, setIsAuthenticated] = useState(authService.isAuthenticated());
  const [userName, setUserName] = useState(authService.getUserName());
  const [userEmail, setUserEmail] = useState(authService.getUserEmail());

  // Verifica o estado na montagem (caso o localStorage mude em outra aba)
  useEffect(() => {
    setIsAuthenticated(authService.isAuthenticated());
    setUserName(authService.getUserName());
    setUserEmail(authService.getUserEmail());
  }, []);

  const login = (token: string, name: string, email: string) => {
    authService.saveAuth({ token, name, email });
    setIsAuthenticated(true);
    setUserName(name);
    setUserEmail(email);
  };

  const logout = () => {
    authService.logout();
    setIsAuthenticated(false);
    setUserName(null);
    setUserEmail(null);
  };

  return (
    <AuthContext.Provider value={{ isAuthenticated, userName, userEmail, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

/**
 * Hook customizado para acessar o contexto de autenticação.
 * Qualquer componente pode usar: const { isAuthenticated, logout } = useAuth();
 */
export function useAuth(): AuthContextType {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth deve ser usado dentro de um AuthProvider');
  }
  return context;
}
