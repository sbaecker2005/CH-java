import React from 'react';
import type { LucideIcon } from 'lucide-react';

interface KpiCardProps {
  title: string;
  value: string | number;
  icon: LucideIcon;
  color: 'blue' | 'red' | 'green' | 'purple';
  subtitle?: string;
  trend?: number;
}

const colorConfig = {
  blue: {
    bg: 'bg-blue-50',
    iconBg: 'bg-blue-600',
    text: 'text-blue-700',
    border: 'border-blue-100',
  },
  red: {
    bg: 'bg-red-50',
    iconBg: 'bg-red-600',
    text: 'text-red-700',
    border: 'border-red-100',
  },
  green: {
    bg: 'bg-green-50',
    iconBg: 'bg-green-600',
    text: 'text-green-700',
    border: 'border-green-100',
  },
  purple: {
    bg: 'bg-purple-50',
    iconBg: 'bg-purple-600',
    text: 'text-purple-700',
    border: 'border-purple-100',
  },
};

const KpiCard: React.FC<KpiCardProps> = ({ title, value, icon: Icon, color, subtitle, trend }) => {
  const cfg = colorConfig[color];

  return (
    <div className={`bg-white rounded-2xl p-6 border ${cfg.border} shadow-sm hover:shadow-md transition-shadow duration-200`}>
      <div className="flex items-start justify-between">
        <div>
          <p className="text-sm font-medium text-gray-500 mb-1">{title}</p>
          <p className={`text-3xl font-bold ${cfg.text}`}>{value}</p>
          {subtitle && <p className="text-xs text-gray-400 mt-1">{subtitle}</p>}
          {trend !== undefined && (
            <div className={`flex items-center gap-1 mt-2 text-xs font-medium ${trend >= 0 ? 'text-green-600' : 'text-red-600'}`}>
              <span>{trend >= 0 ? '↑' : '↓'} {Math.abs(trend)}%</span>
              <span className="text-gray-400 font-normal">vs mês anterior</span>
            </div>
          )}
        </div>
        <div className={`${cfg.iconBg} rounded-xl p-3`}>
          <Icon className="w-6 h-6 text-white" />
        </div>
      </div>
    </div>
  );
};

export default KpiCard;
