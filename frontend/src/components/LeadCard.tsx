import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Zap, Phone, Mail, Clock } from 'lucide-react';
import type { LeadResponse } from '../types';
import ScoreBadge from './ScoreBadge';
import { formatDistanceToNow } from '../utils/dateUtils';

interface LeadCardProps {
  lead: LeadResponse;
  draggable?: boolean;
  onDragStart?: (e: React.DragEvent, lead: LeadResponse) => void;
  compact?: boolean;
}

const LeadCard: React.FC<LeadCardProps> = ({ lead, draggable, onDragStart, compact }) => {
  const navigate = useNavigate();

  return (
    <div
      draggable={draggable}
      onDragStart={onDragStart ? (e) => onDragStart(e, lead) : undefined}
      onClick={() => navigate(`/leads/${lead.id}`)}
      className={`bg-white rounded-xl border border-gray-100 shadow-sm hover:shadow-md cursor-pointer transition-all duration-200 hover:-translate-y-0.5 ${
        lead.fatorUrgencia ? 'border-l-4 border-l-red-500' : ''
      } ${compact ? 'p-3' : 'p-4'}`}
    >
      <div className="flex items-start justify-between gap-2 mb-2">
        <div className="flex items-center gap-1.5 min-w-0">
          {lead.fatorUrgencia && (
            <Zap className="w-3.5 h-3.5 text-red-500 flex-shrink-0 fill-current" />
          )}
          <p className="font-semibold text-gray-900 text-sm truncate">{lead.nome}</p>
        </div>
        <ScoreBadge score={lead.leadScore} />
      </div>

      {!compact && (
        <>
          <div className="space-y-1 mb-2">
            <div className="flex items-center gap-1.5 text-xs text-gray-500">
              <Mail className="w-3 h-3 flex-shrink-0" />
              <span className="truncate">{lead.email}</span>
            </div>
            {lead.procedimentoInteresse && (
              <div className="flex items-center gap-1.5 text-xs text-gray-500">
                <Phone className="w-3 h-3 flex-shrink-0" />
                <span className="truncate">{lead.procedimentoInteresse}</span>
              </div>
            )}
          </div>

          <div className="flex items-center justify-between mt-2 pt-2 border-t border-gray-50">
            <span className="text-xs text-gray-400 flex items-center gap-1">
              <Clock className="w-3 h-3" />
              {formatDistanceToNow(lead.criadoEm)}
            </span>
            {lead.canalOrigem && (
              <span className="text-xs bg-gray-100 text-gray-600 px-2 py-0.5 rounded-full">
                {lead.canalOrigem}
              </span>
            )}
          </div>
        </>
      )}

      {compact && lead.fatorUrgencia && (
        <p className="text-xs text-red-600 font-medium mt-1">Urgente</p>
      )}
    </div>
  );
};

export default LeadCard;
