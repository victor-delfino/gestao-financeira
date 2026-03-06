import { useState } from 'react';
import type { CreateTransactionRequest, TransactionType } from '../types/transaction';

interface TransactionFormProps {
  onSubmit: (data: CreateTransactionRequest) => Promise<void>;
}

export function TransactionForm({ onSubmit }: TransactionFormProps) {
  const [description, setDescription] = useState('');
  const [amount, setAmount] = useState('');
  const [type, setType] = useState<TransactionType>('EXPENSE');
  const [category, setCategory] = useState('');
  const [date, setDate] = useState(new Date().toISOString().split('T')[0]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [open, setOpen] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      await onSubmit({ description, amount: parseFloat(amount), type, category, date });
      setDescription('');
      setAmount('');
      setCategory('');
      setDate(new Date().toISOString().split('T')[0]);
      setType('EXPENSE');
      setOpen(false);
    } catch (err: unknown) {
      if (err && typeof err === 'object' && 'response' in err) {
        const axiosErr = err as { response?: { data?: { message?: string } } };
        setError(axiosErr.response?.data?.message ?? 'Erro ao salvar transação.');
      } else {
        setError('Erro ao conectar com o servidor.');
      }
    } finally {
      setLoading(false);
    }
  };

  const inputStyle = {
    backgroundColor: 'var(--color-surface-secondary)',
    border: '1px solid var(--color-border)',
    color: 'var(--color-text-primary)',
    '--tw-ring-color': 'var(--color-accent)',
  } as React.CSSProperties;

  return (
    <div className="rounded-2xl shadow-sm overflow-hidden"
         style={{ backgroundColor: 'var(--color-surface)', border: '1px solid var(--color-border)' }}>

      {/* Toggle header */}
      <button
        onClick={() => setOpen(!open)}
        className="w-full px-6 py-4 flex items-center justify-between cursor-pointer transition-colors"
        style={{ color: 'var(--color-text-primary)' }}
      >
        <div className="flex items-center gap-3">
          <div className="w-8 h-8 rounded-lg flex items-center justify-center"
               style={{ backgroundColor: 'var(--color-accent-subtle)' }}>
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="var(--color-accent)" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
              <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
            </svg>
          </div>
          <span className="text-sm font-semibold">Nova Transação</span>
        </div>
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"
             className={`transition-transform duration-200 ${open ? 'rotate-180' : ''}`}
             style={{ color: 'var(--color-text-tertiary)' }}>
          <polyline points="6 9 12 15 18 9"/>
        </svg>
      </button>

      {/* Collapsible form */}
      {open && (
        <div className="px-6 pb-6" style={{ borderTop: '1px solid var(--color-border)' }}>
          <div className="pt-5">
            {error && (
              <div className="rounded-xl px-4 py-3 mb-5 text-sm font-medium"
                   style={{ backgroundColor: 'var(--color-danger-bg)', color: 'var(--color-danger)', border: '1px solid var(--color-danger)' }}>
                {error}
              </div>
            )}

            <form onSubmit={handleSubmit} className="grid grid-cols-1 gap-4 sm:grid-cols-2">
              {/* Descrição */}
              <div className="sm:col-span-2">
                <label className="block text-xs font-semibold uppercase tracking-wider mb-2"
                       style={{ color: 'var(--color-text-secondary)' }}>Descrição</label>
                <input type="text" value={description} onChange={e => setDescription(e.target.value)} required
                  placeholder="Ex: Salário, Aluguel, Supermercado..."
                  className="w-full px-4 py-2.5 rounded-xl text-sm focus:outline-none focus:ring-2"
                  style={inputStyle} />
              </div>

              {/* Valor */}
              <div>
                <label className="block text-xs font-semibold uppercase tracking-wider mb-2"
                       style={{ color: 'var(--color-text-secondary)' }}>Valor (R$)</label>
                <input type="number" value={amount} onChange={e => setAmount(e.target.value)} required
                  placeholder="0,00" min="0.01" step="0.01"
                  className="w-full px-4 py-2.5 rounded-xl text-sm focus:outline-none focus:ring-2"
                  style={inputStyle} />
              </div>

              {/* Tipo */}
              <div>
                <label className="block text-xs font-semibold uppercase tracking-wider mb-2"
                       style={{ color: 'var(--color-text-secondary)' }}>Tipo</label>
                <div className="grid grid-cols-2 gap-2">
                  <button type="button" onClick={() => setType('INCOME')}
                    className={`py-2.5 rounded-xl text-sm font-semibold transition-all cursor-pointer ${type === 'INCOME' ? 'ring-2' : ''}`}
                    style={{
                      backgroundColor: type === 'INCOME' ? 'var(--color-income-bg)' : 'var(--color-surface-secondary)',
                      color: type === 'INCOME' ? 'var(--color-income)' : 'var(--color-text-secondary)',
                      border: `1px solid ${type === 'INCOME' ? 'var(--color-income)' : 'var(--color-border)'}`,
                      '--tw-ring-color': 'var(--color-income)',
                    } as React.CSSProperties}>
                    ↑ Receita
                  </button>
                  <button type="button" onClick={() => setType('EXPENSE')}
                    className={`py-2.5 rounded-xl text-sm font-semibold transition-all cursor-pointer ${type === 'EXPENSE' ? 'ring-2' : ''}`}
                    style={{
                      backgroundColor: type === 'EXPENSE' ? 'var(--color-expense-bg)' : 'var(--color-surface-secondary)',
                      color: type === 'EXPENSE' ? 'var(--color-expense)' : 'var(--color-text-secondary)',
                      border: `1px solid ${type === 'EXPENSE' ? 'var(--color-expense)' : 'var(--color-border)'}`,
                      '--tw-ring-color': 'var(--color-expense)',
                    } as React.CSSProperties}>
                    ↓ Despesa
                  </button>
                </div>
              </div>

              {/* Categoria */}
              <div>
                <label className="block text-xs font-semibold uppercase tracking-wider mb-2"
                       style={{ color: 'var(--color-text-secondary)' }}>Categoria</label>
                <input type="text" value={category} onChange={e => setCategory(e.target.value)} required
                  placeholder="Ex: Moradia, Alimentação..."
                  className="w-full px-4 py-2.5 rounded-xl text-sm focus:outline-none focus:ring-2"
                  style={inputStyle} />
              </div>

              {/* Data */}
              <div>
                <label className="block text-xs font-semibold uppercase tracking-wider mb-2"
                       style={{ color: 'var(--color-text-secondary)' }}>Data</label>
                <input type="date" value={date} onChange={e => setDate(e.target.value)} required
                  className="w-full px-4 py-2.5 rounded-xl text-sm focus:outline-none focus:ring-2"
                  style={inputStyle} />
              </div>

              {/* Submit */}
              <div className="sm:col-span-2 pt-1">
                <button type="submit" disabled={loading}
                  className="w-full py-2.5 rounded-xl text-sm font-semibold text-white transition-all
                             hover:opacity-90 active:scale-[0.98] disabled:opacity-50 disabled:cursor-not-allowed cursor-pointer"
                  style={{ backgroundColor: 'var(--color-accent)' }}>
                  {loading ? (
                    <span className="inline-flex items-center gap-2">
                      <svg className="animate-spin h-4 w-4" viewBox="0 0 24 24"><circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" fill="none"/><path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"/></svg>
                      Salvando...
                    </span>
                  ) : 'Salvar Transação'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
