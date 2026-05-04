import React, { useState, useRef, useEffect } from 'react';
import { Bell, X, Sparkles } from 'lucide-react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { getNotificacoes, marcarNotificacaoLida } from '../api/endpoints';
import { formatDistanceToNow } from '../utils/dateUtils';
import type { NotificacaoResponse } from '../types';

interface Props {
  realtimeNotification?: NotificacaoResponse | null;
}

const NotificationBell: React.FC<Props> = ({ realtimeNotification }) => {
  const [open, setOpen] = useState(false);
  const ref = useRef<HTMLDivElement>(null);
  const queryClient = useQueryClient();

  const { data: notificacoes = [] } = useQuery({
    queryKey: ['notificacoes'],
    queryFn: getNotificacoes,
    refetchInterval: 30000,
  });

  const { mutate: markRead } = useMutation({
    mutationFn: marcarNotificacaoLida,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['notificacoes'] }),
  });

  // Add realtime notification to cache
  useEffect(() => {
    if (realtimeNotification) {
      queryClient.setQueryData<NotificacaoResponse[]>(['notificacoes'], (old = []) => {
        const exists = old.some((n) => n.id === realtimeNotification.id);
        if (exists) return old;
        return [realtimeNotification, ...old];
      });
    }
  }, [realtimeNotification, queryClient]);

  useEffect(() => {
    const handleClick = (e: MouseEvent) => {
      if (ref.current && !ref.current.contains(e.target as Node)) {
        setOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClick);
    return () => document.removeEventListener('mousedown', handleClick);
  }, []);

  const unread = notificacoes.filter((n) => !n.lida).length;

  return (
    <div ref={ref} className="relative">
      <button
        onClick={() => setOpen(!open)}
        className="relative p-2 rounded-xl hover:bg-gray-100 transition-colors duration-200"
      >
        <Bell className="w-5 h-5 text-gray-600" />
        {unread > 0 && (
          <span className="absolute top-1 right-1 w-4 h-4 bg-red-500 text-white text-xs rounded-full flex items-center justify-center font-bold leading-none">
            {unread > 9 ? '9+' : unread}
          </span>
        )}
      </button>

      {open && (
        <div className="absolute right-0 top-full mt-2 w-96 bg-white rounded-2xl shadow-2xl border border-gray-100 z-50 overflow-hidden animate-fade-in">
          <div className="flex items-center justify-between px-4 py-3 border-b border-gray-100">
            <div>
              <h3 className="font-semibold text-gray-900">Notificações</h3>
              {unread > 0 && (
                <p className="text-xs text-gray-500">{unread} não lida{unread !== 1 ? 's' : ''}</p>
              )}
            </div>
            <button onClick={() => setOpen(false)} className="p-1 hover:bg-gray-100 rounded-lg">
              <X className="w-4 h-4 text-gray-500" />
            </button>
          </div>

          <div className="max-h-96 overflow-y-auto">
            {notificacoes.length === 0 ? (
              <div className="py-10 text-center text-gray-400">
                <Bell className="w-8 h-8 mx-auto mb-2 opacity-40" />
                <p className="text-sm">Nenhuma notificação</p>
              </div>
            ) : (
              notificacoes.slice(0, 20).map((n) => (
                <button
                  key={n.id}
                  onClick={() => !n.lida && markRead(n.id)}
                  className={`w-full text-left px-4 py-3 border-b border-gray-50 hover:bg-gray-50 transition-colors duration-150 ${
                    !n.lida ? 'bg-blue-50/50' : ''
                  }`}
                >
                  <div className="flex items-start gap-3">
                    <div className={`mt-0.5 w-8 h-8 rounded-full flex items-center justify-center flex-shrink-0 ${
                      n.geradoPorIa ? 'bg-purple-100' : 'bg-blue-100'
                    }`}>
                      {n.geradoPorIa ? (
                        <Sparkles className="w-4 h-4 text-purple-600" />
                      ) : (
                        <Bell className="w-4 h-4 text-blue-600" />
                      )}
                    </div>
                    <div className="flex-1 min-w-0">
                      <p className={`text-sm ${!n.lida ? 'font-medium text-gray-900' : 'text-gray-600'}`}>
                        {n.mensagem}
                      </p>
                      {n.lead?.nome && (
                        <p className="text-xs text-blue-600 mt-0.5">{n.lead.nome}</p>
                      )}
                      <p className="text-xs text-gray-400 mt-1">{formatDistanceToNow(n.criadoEm)}</p>
                    </div>
                    {!n.lida && (
                      <span className="w-2 h-2 bg-blue-500 rounded-full flex-shrink-0 mt-2" />
                    )}
                  </div>
                </button>
              ))
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default NotificationBell;
