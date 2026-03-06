import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import authService from '../services/authService';
import { useAuth } from '../contexts/AuthContext';

/**
 * Página de Registro.
 *
 * POR QUE EXISTE: Interface para novos usuários criarem conta.
 *   Envia nome + email + senha para POST /api/auth/register.
 *   Se bem-sucedido, salva o token JWT e redireciona para o dashboard.
 *   Se o email já existir, mostra erro.
 */
export function RegisterPage() {
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const navigate = useNavigate();
  const { login } = useAuth();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    // Validação no frontend — senhas devem coincidir
    if (password !== confirmPassword) {
      setError('As senhas não coincidem.');
      return;
    }

    if (password.length < 6) {
      setError('A senha deve ter no mínimo 6 caracteres.');
      return;
    }

    setLoading(true);

    try {
      const response = await authService.register({ name, email, password });
      login(response.token, response.name, response.email);
      navigate('/');
    } catch (err: unknown) {
      if (err && typeof err === 'object' && 'response' in err) {
        const axiosErr = err as { response?: { status?: number; data?: { message?: string } } };
        if (axiosErr.response?.status === 409) {
          setError('Este email já está cadastrado.');
        } else if (axiosErr.response?.data?.message) {
          setError(axiosErr.response.data.message);
        } else {
          setError('Erro ao criar conta. Tente novamente.');
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
          <p className="text-gray-500 mt-2">Crie sua conta gratuita</p>
        </div>

        {/* Card do formulário */}
        <div className="bg-white rounded-2xl shadow-lg p-8">
          <h2 className="text-xl font-semibold text-gray-800 mb-6">Criar Conta</h2>

          {error && (
            <div className="bg-red-50 border border-red-200 text-red-700 text-sm rounded-lg p-3 mb-4">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Nome
              </label>
              <input
                type="text"
                value={name}
                onChange={(e) => setName(e.target.value)}
                required
                placeholder="Seu nome completo"
                className="w-full px-4 py-2.5 border border-gray-300 rounded-lg
                           focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500
                           text-gray-900 placeholder-gray-400"
              />
            </div>

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
                placeholder="Mínimo 6 caracteres"
                className="w-full px-4 py-2.5 border border-gray-300 rounded-lg
                           focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500
                           text-gray-900 placeholder-gray-400"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Confirmar Senha
              </label>
              <input
                type="password"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                required
                placeholder="Repita a senha"
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
              {loading ? 'Criando conta...' : 'Criar Conta'}
            </button>
          </form>

          <p className="text-center text-sm text-gray-500 mt-6">
            Já tem conta?{' '}
            <Link to="/login" className="text-indigo-600 font-medium hover:text-indigo-700">
              Fazer login
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
}
