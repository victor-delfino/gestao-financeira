import { useEffect, useState } from 'react';
import { BalanceCard } from './components/BalanceCard';
import { TransactionForm } from './components/TransactionForm';
import { TransactionList } from './components/TransactionList';
import transactionService from './services/transactionService';
import type { CreateTransactionRequest, TransactionResponse } from './types/transaction';

/**
 * Componente raiz da aplicação.
 *
 * RESPONSABILIDADES:
 *   - Buscar e manter o estado global (transactions, balance)
 *   - Coordenar os componentes filhos via props
 *   - Chamar o transactionService para operações de API
 *
 * É o equivalente ao "orquestrador" do frontend —
 * assim como o TransactionService orquestra o backend.
 */
function App() {
  const [transactions, setTransactions] = useState<TransactionResponse[]>([]);
  const [balance, setBalance] = useState<number>(0);
  const [loading, setLoading] = useState(true);

  // Carrega transações e saldo ao montar o componente
  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setLoading(true);
    try {
      // Busca em paralelo para não esperar uma requisição por vez
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

  /**
   * Cria uma nova transação e recarrega os dados.
   * Passado como prop ao TransactionForm via onSubmit.
   */
  const handleCreate = async (data: CreateTransactionRequest) => {
    await transactionService.create(data);
    await loadData(); // atualiza lista e saldo
  };

  return (
    <div className="min-h-screen bg-gray-100">
      {/* Header */}
      <header className="bg-indigo-700 text-white shadow-md">
        <div className="max-w-2xl mx-auto px-4 py-5 flex items-center justify-between">
          <div>
            <h1 className="text-xl font-bold tracking-tight">💰 Gestão Financeira</h1>
            <p className="text-indigo-200 text-sm mt-0.5">Controle suas receitas e despesas</p>
          </div>
        </div>
      </header>

      {/* Conteúdo principal */}
      <main className="max-w-2xl mx-auto px-4 py-8">

        {/* Card de saldo */}
        <BalanceCard balance={balance} transactions={transactions} />

        {/* Formulário de nova transação */}
        <TransactionForm onSubmit={handleCreate} />

        {/* Lista de transações */}
        <TransactionList transactions={transactions} loading={loading} />

      </main>
    </div>
  );
}

export default App;

