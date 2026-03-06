import type { TransactionResponse } from '../types/transaction';

interface BalanceCardProps {
  balance: number;
  transactions: TransactionResponse[];
}

/**
 * Componente que exibe o saldo atual e um resumo financeiro.
 *
 * POR QUE EXISTE: Separa a responsabilidade de exibir o saldo
 *   da lógica de buscar dados (que fica no App).
 *
 * Recebe: balance (número) e transactions (para calcular totais)
 * Exibe: saldo total, total de receitas e total de despesas
 */
export function BalanceCard({ balance, transactions }: BalanceCardProps) {
  // Calcula totais localmente para exibir no resumo
  const totalIncome = transactions
    .filter(t => t.type === 'INCOME')
    .reduce((sum, t) => sum + t.amount, 0);

  const totalExpense = transactions
    .filter(t => t.type === 'EXPENSE')
    .reduce((sum, t) => sum + t.amount, 0);

  // Formata número como moeda brasileira
  const formatCurrency = (value: number) =>
    new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value);

  const isPositive = balance >= 0;

  return (
    <div className="bg-white rounded-2xl shadow-md p-6 mb-6">
      <h2 className="text-sm font-medium text-gray-500 uppercase tracking-wide mb-1">
        Saldo Atual
      </h2>
      <p className={`text-4xl font-bold mb-6 ${isPositive ? 'text-emerald-600' : 'text-red-600'}`}>
        {formatCurrency(balance)}
      </p>

      <div className="grid grid-cols-2 gap-4">
        {/* Receitas */}
        <div className="bg-emerald-50 rounded-xl p-4">
          <div className="flex items-center gap-2 mb-1">
            <span className="text-emerald-500 text-lg">↑</span>
            <span className="text-sm font-medium text-gray-600">Receitas</span>
          </div>
          <p className="text-xl font-bold text-emerald-600">{formatCurrency(totalIncome)}</p>
        </div>

        {/* Despesas */}
        <div className="bg-red-50 rounded-xl p-4">
          <div className="flex items-center gap-2 mb-1">
            <span className="text-red-500 text-lg">↓</span>
            <span className="text-sm font-medium text-gray-600">Despesas</span>
          </div>
          <p className="text-xl font-bold text-red-600">{formatCurrency(totalExpense)}</p>
        </div>
      </div>
    </div>
  );
}
