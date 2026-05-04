import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import api from '../api/client'
import { NotificacaoResponse } from '../types'
import { Bell, BellOff, Sparkles, CheckCheck } from 'lucide-react'
import { formatDate } from '../utils/dateUtils'
import toast from 'react-hot-toast'
import { useWebSocket } from '../hooks/useWebSocket'

export default function Notificacoes() {
  const queryClient = useQueryClient()

  const { data: notificacoes = [], isLoading } = useQuery<NotificacaoResponse[]>({
    queryKey: ['notificacoes'],
    queryFn: () => api.get('/notificacoes').then(r => r.data),
  })

  const marcarLida = useMutation({
    mutationFn: (id: number) => api.patch(`/notificacoes/${id}/lida`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['notificacoes'] })
    },
  })

  const marcarTodasLidas = async () => {
    const naoLidas = notificacoes.filter(n => !n.lida)
    await Promise.all(naoLidas.map(n => marcarLida.mutateAsync(n.id)))
    toast.success(`${naoLidas.length} notificações marcadas como lidas`)
  }

  // WebSocket: adiciona novas notificações em tempo real
  useWebSocket((nova: NotificacaoResponse) => {
    queryClient.setQueryData<NotificacaoResponse[]>(['notificacoes'], old =>
      [nova, ...(old ?? [])]
    )
  })

  const naoLidas = notificacoes.filter(n => !n.lida).length

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-blue-600" />
      </div>
    )
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-slate-800">Notificações</h1>
          <p className="text-slate-500 text-sm mt-1">
            {naoLidas > 0 ? `${naoLidas} não lida${naoLidas > 1 ? 's' : ''}` : 'Todas lidas'}
          </p>
        </div>
        {naoLidas > 0 && (
          <button onClick={marcarTodasLidas} className="btn-secondary flex items-center gap-2 text-sm">
            <CheckCheck className="w-4 h-4" />
            Marcar todas como lidas
          </button>
        )}
      </div>

      {notificacoes.length === 0 ? (
        <div className="card p-12 text-center">
          <BellOff className="w-12 h-12 text-slate-300 mx-auto mb-3" />
          <p className="text-slate-500">Nenhuma notificação</p>
        </div>
      ) : (
        <div className="space-y-3">
          {notificacoes.map(n => (
            <div
              key={n.id}
              className={`card p-4 flex items-start gap-4 cursor-pointer transition-all hover:shadow-md
                ${!n.lida ? 'border-l-4 border-l-blue-500 bg-blue-50/30' : ''}`}
              onClick={() => !n.lida && marcarLida.mutate(n.id)}
            >
              <div className={`mt-0.5 p-2 rounded-full flex-shrink-0
                ${n.geradoPorIa
                  ? 'bg-purple-100 text-purple-600'
                  : 'bg-blue-100 text-blue-600'}`}>
                {n.geradoPorIa ? <Sparkles className="w-4 h-4" /> : <Bell className="w-4 h-4" />}
              </div>

              <div className="flex-1 min-w-0">
                <div className="flex items-center gap-2 flex-wrap">
                  <span className="font-medium text-slate-800 text-sm">
                    {n.leadNome ?? n.lead?.nome ?? 'Paciente'}
                  </span>
                  {n.geradoPorIa && (
                    <span className="text-xs bg-purple-100 text-purple-700 px-2 py-0.5 rounded-full">
                      ✨ IA
                    </span>
                  )}
                  {!n.lida && (
                    <span className="text-xs bg-blue-100 text-blue-700 px-2 py-0.5 rounded-full">
                      Nova
                    </span>
                  )}
                </div>
                <p className="text-slate-600 text-sm mt-1">{n.mensagem}</p>
                <p className="text-slate-400 text-xs mt-1">{formatDate(n.criadoEm)}</p>
              </div>

              {!n.lida && (
                <div className="w-2 h-2 bg-blue-500 rounded-full mt-2 flex-shrink-0" />
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
