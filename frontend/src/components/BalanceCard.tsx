import type { TransactionResponse } from '../types/transaction';

interface BalanceCardProps {
  balance: number;
  transactions: TransactionResponse[];
}

export function BalanceCard({ balance, transactions }: BalanceCardProps) {
  const totalIncome = transactions
    .filter(t => t.type === 'INCOME')
    .reduce((sum, t) => sum + t.amount, 0);

  const totalExpense = transactions
    .filter(t => t.type === 'EXPENSE')
    .reduce((sum, t) => sum + t.amount, 0);

  const formatCurrency = (value: number) =>
    new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value);

  const isPositive = balance >= 0;

  return (
    <div className="rounded-2xl p-6 shadow-sm"
         style={{ backgroundColor: 'var(--color-surface)', border: '1px solid var(--color-border)' }}>

      {/* Saldo */}
      <div className="mb-6">
        <p className="text-xs font-semibold uppercase tracking-wider mb-2"
           style={{ color: 'var(--color-text-tertiary)' }}>
          Saldo Atual
        </p>
        <p className="text-4xl font-extrabold tracking-tight"
           style={{ color: isPositive ? 'var(--color-income)' : 'var(--color-expense)' }}>
          {formatCurrency(balance)}
        </p>
      </div>

      {/* Receitas / Despesas grid */}
      <div className="grid grid-cols-2 gap-4">
        {/* Receitas */}
        <div className="rounded-xl p-4"
             style={{ backgroundColor: 'var(--color-income-bg)' }}>
          <div className="flex items-center gap-2 mb-2">
            <div className="w-7 h-7 rounded-lg flex items-center justify-center"
                 style={{ backgroundColor: 'var(--color-income)', opacity: 0.15 }}>
            </div>
            <svg className="w-4 h-4 absolute ml-1.5"
                 style={{ color: 'var(--color-income)' }}
                 viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
              <polyline points="23 6 13.5 15.5 8.5 10.5 1 18"/><polyline points="17 6 23 6 23 12"/>
            </svg>
            <span className="text-xs font-semibold uppercase tracking-wider ml-2"
                  style={{ color: 'var(--color-income)' }}>
              Receitas
            </span>
          </div>
          <p className="text-xl font-bold" style={{ color: 'var(--color-income)' }}>
            {formatCurrency(totalIncome)}
          </p>
        </div>

        {/* Despesas */}
        <div className="rounded-xl p-4"
             style={{ backgroundColor: 'var(--color-expense-bg)' }}>
          <div className="flex items-center gap-2 mb-2">
            <div className="w-7 h-7 rounded-lg flex items-center justify-center"
                 style={{ backgroundColor: 'var(--color-expense)', opacity: 0.15 }}>
            </div>
            <svg className="w-4 h-4 absolute ml-1.5"
                 style={{ color: 'var(--color-expense)' }}
                 viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
              <polyline points="23 18 13.5 8.5 8.5 13.5 1 6"/><polyline points="17 18 23 18 23 12"/>
            </svg>
            <span className="text-xs font-semibold uppercase tracking-wider ml-2"
                  style={{ color: 'var(--color-expense)' }}>
              Despesas
            </span>
          </div>
          <p className="text-xl font-bold" style={{ color: 'var(--color-expense)' }}>
            {formatCurrency(totalExpense)}
          </p>
        </div>
      </div>
    </div>
  );
}
