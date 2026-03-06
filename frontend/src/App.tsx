import { useEffect, useState } from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { BalanceCard } from './components/BalanceCard';
import { TransactionForm } from './components/TransactionForm';
import { TransactionList } from './components/TransactionList';
import { LoginPage } from './pages/LoginPage';
import { RegisterPage } from './pages/RegisterPage';
import { useAuth } from './contexts/AuthContext';
import { useTheme } from './contexts/ThemeContext';
import transactionService from './services/transactionService';
import type { CreateTransactionRequest, TransactionResponse } from './types/transaction';

function App() {
  return (
    <Routes>
      <Route path="/login" element={<PublicRoute><LoginPage /></PublicRoute>} />
      <Route path="/register" element={<PublicRoute><RegisterPage /></PublicRoute>} />
      <Route path="/" element={<PrivateRoute><Dashboard /></PrivateRoute>} />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}

function PrivateRoute({ children }: { children: React.ReactNode }) {
  const { isAuthenticated } = useAuth();
  return isAuthenticated ? <>{children}</> : <Navigate to="/login" replace />;
}

function PublicRoute({ children }: { children: React.ReactNode }) {
  const { isAuthenticated } = useAuth();
  return isAuthenticated ? <Navigate to="/" replace /> : <>{children}</>;
}

function Dashboard() {
  const [transactions, setTransactions] = useState<TransactionResponse[]>([]);
  const [balance, setBalance] = useState<number>(0);
  const [loading, setLoading] = useState(true);
  const { userName, logout } = useAuth();
  const { dark, toggle } = useTheme();

  useEffect(() => { loadData(); }, []);

  const loadData = async () => {
    setLoading(true);
    try {
      const [txList, bal] = await Promise.all([
        transactionService.listAll(),
        transactionService.getBalance(),
      ]);
      setTransactions(txList);
      setBalance(bal);
    } catch (err) {
      console.error('Erro ao carregar dados:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleCreate = async (data: CreateTransactionRequest) => {
    await transactionService.create(data);
    await loadData();
  };

  // Pega as iniciais do nome para avatar
  const initials = userName
    ? userName.split(' ').map(n => n[0]).slice(0, 2).join('').toUpperCase()
    : '?';

  return (
    <div className="min-h-screen" style={{ backgroundColor: 'var(--color-surface-secondary)' }}>
      {/* ── Header ── */}
      <header style={{ backgroundColor: 'var(--color-surface)', borderBottom: '1px solid var(--color-border)' }}>
        <div className="max-w-3xl mx-auto px-5 py-4 flex items-center justify-between">
          {/* Logo */}
          <div className="flex items-center gap-3">
            <div className="w-9 h-9 rounded-xl flex items-center justify-center"
                 style={{ backgroundColor: 'var(--color-accent-subtle)' }}>
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="var(--color-accent)" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                <line x1="12" y1="1" x2="12" y2="23"/><path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/>
              </svg>
            </div>
            <div>
              <h1 className="text-base font-bold tracking-tight" style={{ color: 'var(--color-text-primary)' }}>
                Gestão Financeira
              </h1>
              <p className="text-xs" style={{ color: 'var(--color-text-tertiary)' }}>
                Controle de receitas e despesas
              </p>
            </div>
          </div>

          {/* Actions */}
          <div className="flex items-center gap-3">
            {/* Theme toggle */}
            <button onClick={toggle}
              className="p-2 rounded-xl transition-all hover:scale-105 active:scale-95 cursor-pointer"
              style={{ backgroundColor: 'var(--color-surface-secondary)', border: '1px solid var(--color-border)', color: 'var(--color-text-secondary)' }}
              aria-label="Alternar tema">
              {dark ? (
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><circle cx="12" cy="12" r="5"/><path d="M12 1v2M12 21v2M4.22 4.22l1.42 1.42M18.36 18.36l1.42 1.42M1 12h2M21 12h2M4.22 19.78l1.42-1.42M18.36 5.64l1.42-1.42"/></svg>
              ) : (
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"/></svg>
              )}
            </button>

            {/* User avatar + name */}
            <div className="flex items-center gap-2.5 pl-3" style={{ borderLeft: '1px solid var(--color-border)' }}>
              <div className="w-8 h-8 rounded-full flex items-center justify-center text-xs font-bold text-white"
                   style={{ backgroundColor: 'var(--color-accent)' }}>
                {initials}
              </div>
              <span className="text-sm font-medium hidden sm:block" style={{ color: 'var(--color-text-primary)' }}>
                {userName}
              </span>
              <button onClick={logout}
                className="text-xs font-medium px-3 py-1.5 rounded-lg transition-all hover:opacity-80 active:scale-95 cursor-pointer"
                style={{ color: 'var(--color-text-secondary)', backgroundColor: 'var(--color-surface-secondary)', border: '1px solid var(--color-border)' }}>
                Sair
              </button>
            </div>
          </div>
        </div>
      </header>

      {/* ── Content ── */}
      <main className="max-w-3xl mx-auto px-5 py-8 space-y-6">
        <BalanceCard balance={balance} transactions={transactions} />
        <TransactionForm onSubmit={handleCreate} />
        <TransactionList transactions={transactions} loading={loading} />
      </main>
    </div>
  );
}

export default App;

