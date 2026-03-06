import type { TransactionResponse } from '../types/transaction';

interface TransactionListProps {
  transactions: TransactionResponse[];
  loading: boolean;
}

/**
 * Lista de transações financeiras.
 *
 * POR QUE EXISTE: Responsabilidade única de exibir a lista.
 *   Recebe dados prontos via props — não busca nada, não tem lógica de negócio.
 *   É um componente "burro" (presentational): recebe dados e renderiza.
 */
export function TransactionList({ transactions, loading }: TransactionListProps) {
  const formatCurrency = (value: number) =>
    new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value);

  const formatDate = (dateStr: string) =>
    new Date(dateStr + 'T00:00:00').toLocaleDateString('pt-BR');

  if (loading) {
    return (
      <div className="bg-white rounded-2xl shadow-md p-6">
        <div className="animate-pulse space-y-3">
          {[1, 2, 3].map(i => (
            <div key={i} className="h-16 bg-gray-100 rounded-lg" />
          ))}
        </div>
      </div>
    );
  }

  if (transactions.length === 0) {
    return (
      <div className="bg-white rounded-2xl shadow-md p-10 text-center text-gray-400">
        <p className="text-4xl mb-3">💸</p>
        <p className="font-medium">Nenhuma transação registrada ainda.</p>
        <p className="text-sm mt-1">Use o formulário acima para adicionar a primeira.</p>
      </div>
    );
  }

  // Ordena por data mais recente primeiro
  const sorted = [...transactions].sort(
    (a, b) => new Date(b.date).getTime() - new Date(a.date).getTime()
  );

  return (
    <div className="bg-white rounded-2xl shadow-md overflow-hidden">
      <div className="px-6 py-4 border-b border-gray-100">
        <h2 className="text-lg font-semibold text-gray-800">
          Histórico
          <span className="ml-2 text-sm font-normal text-gray-400">
            ({transactions.length} transaç{transactions.length === 1 ? 'ão' : 'ões'})
          </span>
        </h2>
      </div>

      <ul className="divide-y divide-gray-50">
        {sorted.map(transaction => {
          const isIncome = transaction.type === 'INCOME';

          return (
            <li
              key={transaction.id}
              className="flex items-center justify-between px-6 py-4 hover:bg-gray-50 transition-colors"
            >
              {/* Ícone + Informações */}
              <div className="flex items-center gap-4">
                {/* Ícone colorido por tipo */}
                <div className={`w-10 h-10 rounded-full flex items-center justify-center text-lg
                  ${isIncome ? 'bg-emerald-100 text-emerald-600' : 'bg-red-100 text-red-600'}`}>
                  {isIncome ? '↑' : '↓'}
                </div>

                <div>
                  <p className="font-medium text-gray-800">{transaction.description}</p>
                  <p className="text-xs text-gray-400">
                    {transaction.category} · {formatDate(transaction.date)}
                  </p>
                </div>
              </div>

              {/* Valor */}
              <p className={`font-bold text-base ${isIncome ? 'text-emerald-600' : 'text-red-600'}`}>
                {isIncome ? '+' : '-'} {formatCurrency(transaction.amount)}
              </p>
            </li>
          );
        })}
      </ul>
    </div>
  );
}
