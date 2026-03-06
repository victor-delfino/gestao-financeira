import { useState } from 'react';
import type { CreateTransactionRequest, TransactionType } from '../types/transaction';

interface TransactionFormProps {
  /** Chamado ao submeter o formulário com sucesso */
  onSubmit: (data: CreateTransactionRequest) => Promise<void>;
}

/**
 * Formulário para criar uma nova transação financeira.
 *
 * POR QUE EXISTE: Isola toda a lógica de formulário (estado local,
 *   validação básica, submit) em um componente dedicado.
 *   O componente pai (App) só recebe o callback onSubmit pronto.
 *
 * ESTADO LOCAL: cada campo do formulário é controlado pelo React
 *   (controlled component) — o estado é a fonte da verdade.
 */
export function TransactionForm({ onSubmit }: TransactionFormProps) {
  // Estado do formulário — espelha CreateTransactionRequest
  const [description, setDescription] = useState('');
  const [amount, setAmount] = useState('');
  const [type, setType] = useState<TransactionType>('EXPENSE');
  const [category, setCategory] = useState('');
  const [date, setDate] = useState(new Date().toISOString().split('T')[0]); // hoje
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault(); // evita reload da página
    setError(null);
    setLoading(true);

    try {
      await onSubmit({
        description,
        amount: parseFloat(amount),
        type,
        category,
        date,
      });

      // Limpa o formulário após sucesso
      setDescription('');
      setAmount('');
      setCategory('');
      setDate(new Date().toISOString().split('T')[0]);
      setType('EXPENSE');
    } catch (err: unknown) {
      // Trata erro da API (DomainException ou erro de rede)
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

  // Classes reutilizáveis para os inputs
  const inputClass =
    'w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-400';
  const labelClass = 'block text-sm font-medium text-gray-700 mb-1';

  return (
    <div className="bg-white rounded-2xl shadow-md p-6 mb-6">
      <h2 className="text-lg font-semibold text-gray-800 mb-4">Nova Transação</h2>

      {/* Exibe erro da API se houver */}
      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 rounded-lg px-4 py-3 mb-4 text-sm">
          {error}
        </div>
      )}

      <form onSubmit={handleSubmit} className="grid grid-cols-1 gap-4 sm:grid-cols-2">

        {/* Descrição */}
        <div className="sm:col-span-2">
          <label className={labelClass}>Descrição</label>
          <input
            type="text"
            className={inputClass}
            placeholder="Ex: Aluguel, Salário, Supermercado..."
            value={description}
            onChange={e => setDescription(e.target.value)}
            required
          />
        </div>

        {/* Valor */}
        <div>
          <label className={labelClass}>Valor (R$)</label>
          <input
            type="number"
            className={inputClass}
            placeholder="0,00"
            min="0.01"
            step="0.01"
            value={amount}
            onChange={e => setAmount(e.target.value)}
            required
          />
        </div>

        {/* Tipo */}
        <div>
          <label className={labelClass}>Tipo</label>
          <select
            className={inputClass}
            value={type}
            onChange={e => setType(e.target.value as TransactionType)}
          >
            <option value="INCOME">↑ Receita</option>
            <option value="EXPENSE">↓ Despesa</option>
          </select>
        </div>

        {/* Categoria */}
        <div>
          <label className={labelClass}>Categoria</label>
          <input
            type="text"
            className={inputClass}
            placeholder="Ex: Moradia, Alimentação, Trabalho..."
            value={category}
            onChange={e => setCategory(e.target.value)}
            required
          />
        </div>

        {/* Data */}
        <div>
          <label className={labelClass}>Data</label>
          <input
            type="date"
            className={inputClass}
            value={date}
            onChange={e => setDate(e.target.value)}
            required
          />
        </div>

        {/* Botão submit */}
        <div className="sm:col-span-2">
          <button
            type="submit"
            disabled={loading}
            className="w-full bg-indigo-600 text-white font-medium py-2 px-4 rounded-lg
                       hover:bg-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed
                       transition-colors duration-150"
          >
            {loading ? 'Salvando...' : 'Salvar Transação'}
          </button>
        </div>

      </form>
    </div>
  );
}
