import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import authService from '../services/authService';
import { useAuth } from '../contexts/AuthContext';

/**
 * Página de Login.
 *
 * POR QUE EXISTE: Interface para o usuário se autenticar no sistema.
 *   Envia email + senha para POST /api/auth/login.
 *   Se bem-sucedido, salva o token JWT e redireciona para o dashboard.
 *   Se falhar, mostra mensagem de erro.
 */
export function LoginPage() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const navigate = useNavigate();
  const { login } = useAuth();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const response = await authService.login({ email, password });
      login(response.token, response.name, response.email);
      navigate('/');
    } catch (err: unknown) {
      if (err && typeof err === 'object' && 'response' in err) {
        const axiosErr = err as { response?: { status?: number } };
        if (axiosErr.response?.status === 401 || axiosErr.response?.status === 403) {
          setError('Email ou senha inválidos.');
        } else {
          setError('Erro ao fazer login. Tente novamente.');
        }
      } else {
        setError('Erro de conexão. Verifique se o servidor está rodando.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-100 flex items-center justify-center px-4">
      <div className="w-full max-w-md">

        {/* Logo / Título */}
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-indigo-700">💰 Gestão Financeira</h1>
          <p className="text-gray-500 mt-2">Faça login para acessar sua conta</p>
        </div>

        {/* Card do formulário */}
        <div className="bg-white rounded-2xl shadow-lg p-8">
          <h2 className="text-xl font-semibold text-gray-800 mb-6">Entrar</h2>

          {error && (
            <div className="bg-red-50 border border-red-200 text-red-700 text-sm rounded-lg p-3 mb-4">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Email
              </label>
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
                placeholder="seu@email.com"
                className="w-full px-4 py-2.5 border border-gray-300 rounded-lg
                           focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500
                           text-gray-900 placeholder-gray-400"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Senha
              </label>
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
                placeholder="••••••"
                className="w-full px-4 py-2.5 border border-gray-300 rounded-lg
                           focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500
                           text-gray-900 placeholder-gray-400"
              />
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full bg-indigo-600 text-white py-2.5 rounded-lg font-medium
                         hover:bg-indigo-700 transition-colors disabled:opacity-50
                         disabled:cursor-not-allowed"
            >
              {loading ? 'Entrando...' : 'Entrar'}
            </button>
          </form>

          <p className="text-center text-sm text-gray-500 mt-6">
            Não tem conta?{' '}
            <Link to="/register" className="text-indigo-600 font-medium hover:text-indigo-700">
              Criar conta
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
}
