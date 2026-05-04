import React from 'react';
import { TrendingUp } from 'lucide-react';

interface ScoreBadgeProps {
  score?: string;
}

const scoreConfig: Record<string, { className: string; dot: string }> = {
  'Muito Alto': { className: 'bg-red-100 text-red-700 border border-red-200', dot: 'bg-red-500' },
  Alto: { className: 'bg-orange-100 text-orange-700 border border-orange-200', dot: 'bg-orange-500' },
  Médio: { className: 'bg-yellow-100 text-yellow-700 border border-yellow-200', dot: 'bg-yellow-500' },
  Baixo: { className: 'bg-gray-100 text-gray-600 border border-gray-200', dot: 'bg-gray-400' },
};

const ScoreBadge: React.FC<ScoreBadgeProps> = ({ score }) => {
  if (!score) return <span className="text-gray-400 text-xs">—</span>;

  const config = scoreConfig[score] ?? {
    className: 'bg-gray-100 text-gray-600 border border-gray-200',
    dot: 'bg-gray-400',
  };

  return (
    <span className={`inline-flex items-center gap-1.5 rounded-full text-xs px-2.5 py-1 font-medium ${config.className}`}>
      <TrendingUp className="w-3 h-3" />
      {score}
    </span>
  );
};

export default ScoreBadge;
