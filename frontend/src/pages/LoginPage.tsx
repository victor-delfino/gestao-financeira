import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import authService from '../services/authService';
import { useAuth } from '../contexts/AuthContext';
import { useTheme } from '../contexts/ThemeContext';

export function LoginPage() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const navigate = useNavigate();
  const { login } = useAuth();
  const { dark, toggle } = useTheme();

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
    <div className="min-h-screen flex items-center justify-center px-4"
         style={{ backgroundColor: 'var(--color-surface-secondary)' }}>

      {/* Theme toggle — canto superior direito */}
      <button
        onClick={toggle}
        className="fixed top-5 right-5 p-2.5 rounded-xl transition-all hover:scale-105 active:scale-95 cursor-pointer"
        style={{ backgroundColor: 'var(--color-surface)', border: '1px solid var(--color-border)', color: 'var(--color-text-secondary)' }}
        aria-label="Alternar tema"
      >
        {dark ? (
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><circle cx="12" cy="12" r="5"/><path d="M12 1v2M12 21v2M4.22 4.22l1.42 1.42M18.36 18.36l1.42 1.42M1 12h2M21 12h2M4.22 19.78l1.42-1.42M18.36 5.64l1.42-1.42"/></svg>
        ) : (
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"/></svg>
        )}
      </button>

      <div className="w-full max-w-sm">
        {/* Branding */}
        <div className="text-center mb-10">
          <div className="inline-flex items-center justify-center w-14 h-14 rounded-2xl mb-5"
               style={{ backgroundColor: 'var(--color-accent-subtle)' }}>
            <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="var(--color-accent)" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <line x1="12" y1="1" x2="12" y2="23"/><path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/>
            </svg>
          </div>
          <h1 className="text-2xl font-bold tracking-tight" style={{ color: 'var(--color-text-primary)' }}>
            Gestão Financeira
          </h1>
          <p className="text-sm mt-1.5" style={{ color: 'var(--color-text-tertiary)' }}>
            Entre na sua conta para continuar
          </p>
        </div>

        {/* Card */}
        <div className="rounded-2xl p-7 shadow-sm"
             style={{ backgroundColor: 'var(--color-surface)', border: '1px solid var(--color-border)' }}>

          {error && (
            <div className="rounded-xl px-4 py-3 mb-5 text-sm font-medium"
                 style={{ backgroundColor: 'var(--color-danger-bg)', color: 'var(--color-danger)', border: '1px solid var(--color-danger)' }}>
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-5">
            <div>
              <label className="block text-xs font-semibold uppercase tracking-wider mb-2"
                     style={{ color: 'var(--color-text-secondary)' }}>
                Email
              </label>
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
                placeholder="nome@email.com"
                className="w-full px-4 py-2.5 rounded-xl text-sm transition-all focus:outline-none focus:ring-2"
                style={{
                  backgroundColor: 'var(--color-surface-secondary)',
                  border: '1px solid var(--color-border)',
                  color: 'var(--color-text-primary)',
                  '--tw-ring-color': 'var(--color-accent)',
                } as React.CSSProperties}
              />
            </div>

            <div>
              <label className="block text-xs font-semibold uppercase tracking-wider mb-2"
                     style={{ color: 'var(--color-text-secondary)' }}>
                Senha
              </label>
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
                placeholder="••••••••"
                className="w-full px-4 py-2.5 rounded-xl text-sm transition-all focus:outline-none focus:ring-2"
                style={{
                  backgroundColor: 'var(--color-surface-secondary)',
                  border: '1px solid var(--color-border)',
                  color: 'var(--color-text-primary)',
                  '--tw-ring-color': 'var(--color-accent)',
                } as React.CSSProperties}
              />
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full py-2.5 rounded-xl text-sm font-semibold text-white transition-all
                         hover:opacity-90 active:scale-[0.98] disabled:opacity-50 disabled:cursor-not-allowed cursor-pointer"
              style={{ backgroundColor: 'var(--color-accent)' }}
            >
              {loading ? (
                <span className="inline-flex items-center gap-2">
                  <svg className="animate-spin h-4 w-4" viewBox="0 0 24 24"><circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" fill="none"/><path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"/></svg>
                  Entrando...
                </span>
              ) : 'Entrar'}
            </button>
          </form>

          <div className="mt-6 pt-5 text-center text-sm" style={{ borderTop: '1px solid var(--color-border)' }}>
            <span style={{ color: 'var(--color-text-tertiary)' }}>Não tem conta? </span>
            <Link to="/register" className="font-semibold transition-colors hover:opacity-80"
                  style={{ color: 'var(--color-accent)' }}>
              Criar conta
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
}
