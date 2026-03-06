import { useEffect, useState } from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { BalanceCard } from './components/BalanceCard';
import { TransactionForm } from './components/TransactionForm';
import { TransactionList } from './components/TransactionList';
import { LoginPage } from './pages/LoginPage';
import { RegisterPage } from './pages/RegisterPage';
import { useAuth } from './contexts/AuthContext';
import transactionService from './services/transactionService';
import type { CreateTransactionRequest, TransactionResponse } from './types/transaction';

/**
 * Componente raiz da aplicação.
 *
 * RESPONSABILIDADES:
 *   - Gerenciar rotas: login, register, dashboard
 *   - Proteger o dashboard: só acessível quando autenticado
 *   - Buscar e manter o estado global (transactions, balance)
 *   - Coordenar os componentes filhos via props
 */
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

/**
 * Rota protegida: redireciona para login se não estiver autenticado.
 */
function PrivateRoute({ children }: { children: React.ReactNode }) {
  const { isAuthenticated } = useAuth();
  return isAuthenticated ? <>{children}</> : <Navigate to="/login" replace />;
}

/**
 * Rota pública: redireciona para dashboard se já estiver autenticado.
 */
function PublicRoute({ children }: { children: React.ReactNode }) {
  const { isAuthenticated } = useAuth();
  return isAuthenticated ? <Navigate to="/" replace /> : <>{children}</>;
}

/**
 * Dashboard principal — exibe saldo, formulário e lista de transações.
 * Só é renderizado quando o usuário está autenticado.
 */
function Dashboard() {
  const [transactions, setTransactions] = useState<TransactionResponse[]>([]);
  const [balance, setBalance] = useState<number>(0);
  const [loading, setLoading] = useState(true);
  const { userName, logout } = useAuth();

  useEffect(() => {
    loadData();
  }, []);

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

  return (
    <div className="min-h-screen bg-gray-100">
      {/* Header com nome do usuário e botão de logout */}
      <header className="bg-indigo-700 text-white shadow-md">
        <div className="max-w-2xl mx-auto px-4 py-5 flex items-center justify-between">
          <div>
            <h1 className="text-xl font-bold tracking-tight">💰 Gestão Financeira</h1>
            <p className="text-indigo-200 text-sm mt-0.5">Controle suas receitas e despesas</p>
          </div>
          <div className="flex items-center gap-4">
            <span className="text-indigo-200 text-sm">
              Olá, <strong className="text-white">{userName}</strong>
            </span>
            <button
              onClick={logout}
              className="bg-indigo-600 hover:bg-indigo-500 text-white text-sm px-4 py-1.5
                         rounded-lg border border-indigo-500 transition-colors"
            >
              Sair
            </button>
          </div>
        </div>
      </header>

      {/* Conteúdo principal */}
      <main className="max-w-2xl mx-auto px-4 py-8">
        <BalanceCard balance={balance} transactions={transactions} />
        <TransactionForm onSubmit={handleCreate} />
        <TransactionList transactions={transactions} loading={loading} />
      </main>
    </div>
  );
}

export default App;

