import type { TransactionResponse } from '../types/transaction';

interface TransactionListProps {
  transactions: TransactionResponse[];
  loading: boolean;
}

export function TransactionList({ transactions, loading }: TransactionListProps) {
  const formatCurrency = (value: number) =>
    new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value);

  const formatDate = (dateStr: string) =>
    new Date(dateStr + 'T00:00:00').toLocaleDateString('pt-BR');

  if (loading) {
    return (
      <div className="rounded-2xl p-6 shadow-sm"
           style={{ backgroundColor: 'var(--color-surface)', border: '1px solid var(--color-border)' }}>
        <div className="space-y-3">
          {[1, 2, 3].map(i => (
            <div key={i} className="h-16 rounded-xl animate-pulse"
                 style={{ backgroundColor: 'var(--color-surface-secondary)' }} />
          ))}
        </div>
      </div>
    );
  }

  if (transactions.length === 0) {
    return (
      <div className="rounded-2xl p-12 text-center shadow-sm"
           style={{ backgroundColor: 'var(--color-surface)', border: '1px solid var(--color-border)' }}>
        <div className="w-16 h-16 rounded-2xl mx-auto mb-4 flex items-center justify-center"
             style={{ backgroundColor: 'var(--color-surface-secondary)' }}>
          <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="var(--color-text-tertiary)" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round">
            <rect x="2" y="5" width="20" height="14" rx="2"/><line x1="2" y1="10" x2="22" y2="10"/>
          </svg>
        </div>
        <p className="font-semibold text-sm" style={{ color: 'var(--color-text-secondary)' }}>
          Nenhuma transação ainda
        </p>
        <p className="text-xs mt-1" style={{ color: 'var(--color-text-tertiary)' }}>
          Clique em "Nova Transação" para começar
        </p>
      </div>
    );
  }

  const sorted = [...transactions].sort(
    (a, b) => new Date(b.date).getTime() - new Date(a.date).getTime()
  );

  return (
    <div className="rounded-2xl shadow-sm overflow-hidden"
         style={{ backgroundColor: 'var(--color-surface)', border: '1px solid var(--color-border)' }}>

      {/* Header */}
      <div className="px-6 py-4 flex items-center justify-between"
           style={{ borderBottom: '1px solid var(--color-border)' }}>
        <h2 className="text-sm font-semibold" style={{ color: 'var(--color-text-primary)' }}>
          Histórico
        </h2>
        <span className="text-xs font-medium px-2.5 py-1 rounded-full"
              style={{ backgroundColor: 'var(--color-surface-secondary)', color: 'var(--color-text-tertiary)' }}>
          {transactions.length} transaç{transactions.length === 1 ? 'ão' : 'ões'}
        </span>
      </div>

      {/* List */}
      <ul>
        {sorted.map((transaction, index) => {
          const isIncome = transaction.type === 'INCOME';
          const isLast = index === sorted.length - 1;

          return (
            <li key={transaction.id}
                className="flex items-center justify-between px-6 py-4 transition-colors"
                style={{
                  borderBottom: isLast ? 'none' : '1px solid var(--color-border-subtle)',
                }}
                onMouseEnter={e => (e.currentTarget.style.backgroundColor = 'var(--color-surface-hover)')}
                onMouseLeave={e => (e.currentTarget.style.backgroundColor = 'transparent')}
            >
              {/* Left: icon + info */}
              <div className="flex items-center gap-3.5">
                <div className="w-10 h-10 rounded-xl flex items-center justify-center shrink-0"
                     style={{ backgroundColor: isIncome ? 'var(--color-income-bg)' : 'var(--color-expense-bg)' }}>
                  {isIncome ? (
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round"
                         stroke="var(--color-income)">
                      <line x1="12" y1="19" x2="12" y2="5"/><polyline points="5 12 12 5 19 12"/>
                    </svg>
                  ) : (
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round"
                         stroke="var(--color-expense)">
                      <line x1="12" y1="5" x2="12" y2="19"/><polyline points="19 12 12 19 5 12"/>
                    </svg>
                  )}
                </div>
                <div>
                  <p className="text-sm font-medium" style={{ color: 'var(--color-text-primary)' }}>
                    {transaction.description}
                  </p>
                  <p className="text-xs mt-0.5" style={{ color: 'var(--color-text-tertiary)' }}>
                    {transaction.category} · {formatDate(transaction.date)}
                  </p>
                </div>
              </div>

              {/* Right: amount */}
              <p className="text-sm font-bold tabular-nums"
                 style={{ color: isIncome ? 'var(--color-income)' : 'var(--color-expense)' }}>
                {isIncome ? '+' : '−'} {formatCurrency(transaction.amount)}
              </p>
            </li>
          );
        })}
      </ul>
    </div>
  );
}
