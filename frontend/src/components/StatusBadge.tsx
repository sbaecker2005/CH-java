import React from 'react';

interface StatusBadgeProps {
  status: string;
  size?: 'sm' | 'md';
}

const statusConfig: Record<string, { label: string; className: string }> = {
  Novo: { label: 'Novo', className: 'bg-blue-100 text-blue-800 border border-blue-200' },
  'Em Atendimento': { label: 'Em Atendimento', className: 'bg-yellow-100 text-yellow-800 border border-yellow-200' },
  'Aguardando Retorno': { label: 'Aguardando Retorno', className: 'bg-orange-100 text-orange-800 border border-orange-200' },
  Convertido: { label: 'Convertido', className: 'bg-green-100 text-green-800 border border-green-200' },
  Cancelado: { label: 'Cancelado', className: 'bg-red-100 text-red-800 border border-red-200' },
  // Agendamento statuses
  Pendente: { label: 'Pendente', className: 'bg-yellow-100 text-yellow-800 border border-yellow-200' },
  Confirmado: { label: 'Confirmado', className: 'bg-green-100 text-green-800 border border-green-200' },
  Reagendado: { label: 'Reagendado', className: 'bg-blue-100 text-blue-800 border border-blue-200' },
};

const StatusBadge: React.FC<StatusBadgeProps> = ({ status, size = 'md' }) => {
  const config = statusConfig[status] ?? {
    label: status,
    className: 'bg-gray-100 text-gray-800 border border-gray-200',
  };

  const sizeClass = size === 'sm' ? 'text-xs px-2 py-0.5' : 'text-xs px-2.5 py-1';

  return (
    <span className={`inline-flex items-center rounded-full font-medium ${sizeClass} ${config.className}`}>
      {config.label}
    </span>
  );
};

export default StatusBadge;
