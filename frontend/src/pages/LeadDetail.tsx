import React, { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  ArrowLeft, Zap, Mail, Phone, Calendar, Tag, Shield, Clock,
  Sparkles, Brain, MessageSquare, Target, AlertTriangle,
  CheckCircle, Send, RefreshCw, User, ChevronDown,
} from 'lucide-react';
import toast from 'react-hot-toast';
import Layout from '../components/Layout';
import StatusBadge from '../components/StatusBadge';
import ScoreBadge from '../components/ScoreBadge';
import {
  getLead, analisarLead, getInteracoesByLead, createInteracao, updateLeadStatus,
} from '../api/endpoints';
import { useAuth } from '../contexts/AuthContext';
import { formatDateTime, formatDistanceToNow } from '../utils/dateUtils';
import type { LeadAnalysis } from '../types';

const STATUS_OPTIONS = ['Novo', 'Em Atendimento', 'Aguardando Retorno', 'Convertido', 'Cancelado'];
const INTERACAO_TIPOS = ['Ligação', 'E-mail', 'WhatsApp', 'Presencial', 'Chat', 'Outro'];

const PRIORIDADE_CONFIG: Record<string, { color: string; label: string }> = {
  ALTA: { color: 'text-red-600 bg-red-50 border-red-200', label: 'Alta' },
  MEDIA: { color: 'text-orange-600 bg-orange-50 border-orange-200', label: 'Média' },
  BAIXA: { color: 'text-green-600 bg-green-50 border-green-200', label: 'Baixa' },
  URGENTE: { color: 'text-red-800 bg-red-100 border-red-300', label: 'Urgente' },
};

const LeadDetail: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const leadId = Number(id);
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const { user } = useAuth();

  const [analysis, setAnalysis] = useState<LeadAnalysis | null>(null);
  const [showAnalysis, setShowAnalysis] = useState(false);
  const [interacaoForm, setInteracaoForm] = useState({ tipo: 'Ligação', conteudo: '' });
  const [showStatusMenu, setShowStatusMenu] = useState(false);

  const { data: lead, isLoading } = useQuery({
    queryKey: ['lead', leadId],
    queryFn: () => getLead(leadId),
    enabled: !!leadId,
  });

  const { data: interacoes = [], isLoading: interacoesLoading } = useQuery({
    queryKey: ['interacoes', leadId],
    queryFn: () => getInteracoesByLead(leadId),
    enabled: !!leadId,
  });

  const { mutate: analyze, isPending: analyzing } = useMutation({
    mutationFn: () => analisarLead(leadId),
    onSuccess: (data) => {
      setAnalysis(data);
      setShowAnalysis(true);
      toast.success('Análise de IA concluída!');
    },
    onError: () => toast.error('Erro ao analisar lead com IA'),
  });

  const { mutate: addInteracao, isPending: sendingInteracao } = useMutation({
    mutationFn: () =>
      createInteracao({
        leadId,
        operadorId: user?.id ?? 1,
        tipo: interacaoForm.tipo,
        conteudo: interacaoForm.conteudo,
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['interacoes', leadId] });
      setInteracaoForm({ tipo: 'Ligação', conteudo: '' });
      toast.success('Interação registrada!');
    },
    onError: () => toast.error('Erro ao registrar interação'),
  });

  const { mutate: changeStatus } = useMutation({
    mutationFn: (status: string) => updateLeadStatus(leadId, status),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['lead', leadId] });
      queryClient.invalidateQueries({ queryKey: ['leads'] });
      setShowStatusMenu(false);
      toast.success('Status atualizado!');
    },
    onError: () => toast.error('Erro ao atualizar status'),
  });

  if (isLoading) {
    return (
      <Layout>
        <div className="flex items-center justify-center h-64">
          <div className="w-8 h-8 border-4 border-gray-200 border-t-primary-600 rounded-full animate-spin" />
        </div>
      </Layout>
    );
  }

  if (!lead) {
    return (
      <Layout>
        <div className="text-center py-16 text-gray-400">
          <p>Lead não encontrado</p>
          <button onClick={() => navigate('/leads')} className="mt-4 text-primary-600 text-sm">
            Voltar para Leads
          </button>
        </div>
      </Layout>
    );
  }

  const prioridadeCfg = analysis
    ? (PRIORIDADE_CONFIG[analysis.prioridade?.toUpperCase()] ?? PRIORIDADE_CONFIG.MEDIA)
    : null;

  return (
    <Layout>
      {/* Back + header */}
      <div className="flex items-center gap-3 mb-6">
        <button
          onClick={() => navigate('/leads')}
          className="p-2 hover:bg-gray-100 rounded-xl transition-colors"
        >
          <ArrowLeft className="w-5 h-5 text-gray-600" />
        </button>
        <div className="flex-1">
          <div className="flex items-center gap-2">
            <h1 className="text-2xl font-bold text-gray-900">{lead.nome}</h1>
            {lead.fatorUrgencia && (
              <Zap className="w-6 h-6 text-red-500 fill-current" />
            )}
          </div>
          <p className="text-sm text-gray-500">Detalhes completos do lead · ID #{lead.id}</p>
        </div>
        <div className="flex items-center gap-3">
          <StatusBadge status={lead.status} />
          {/* Status changer */}
          <div className="relative">
            <button
              onClick={() => setShowStatusMenu(!showStatusMenu)}
              className="flex items-center gap-1.5 px-4 py-2 border border-gray-200 rounded-xl text-sm font-medium text-gray-700 hover:bg-gray-50 transition-colors"
            >
              Alterar Status
              <ChevronDown className="w-4 h-4" />
            </button>
            {showStatusMenu && (
              <div className="absolute right-0 top-full mt-1 w-52 bg-white rounded-xl shadow-xl border border-gray-100 z-20 overflow-hidden animate-fade-in">
                {STATUS_OPTIONS.map((s) => (
                  <button
                    key={s}
                    onClick={() => changeStatus(s)}
                    className={`w-full text-left px-4 py-2.5 text-sm hover:bg-gray-50 transition-colors ${
                      lead.status === s ? 'bg-primary-50 text-primary-700 font-medium' : 'text-gray-700'
                    }`}
                  >
                    {s}
                  </button>
                ))}
              </div>
            )}
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-5 gap-6">
        {/* Left column: profile + interactions */}
        <div className="lg:col-span-2 space-y-6">
          {/* Profile card */}
          <div className="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden">
            <div className="bg-gradient-to-br from-primary-700 to-primary-900 px-6 py-6 text-white">
              <div className="flex items-center gap-4">
                <div className="w-16 h-16 bg-white/20 rounded-2xl flex items-center justify-center text-2xl font-bold">
                  {lead.nome.charAt(0).toUpperCase()}
                </div>
                <div>
                  <h2 className="text-xl font-bold">{lead.nome}</h2>
                  <ScoreBadge score={lead.leadScore} />
                </div>
              </div>
            </div>

            <div className="px-6 py-4 space-y-3">
              <InfoRow icon={Mail} label="E-mail" value={lead.email} />
              <InfoRow icon={Phone} label="Telefone" value={String(lead.telefone)} />
              <InfoRow icon={Tag} label="Canal" value={lead.canalOrigem} />
              <InfoRow icon={Target} label="Procedimento" value={lead.procedimentoInteresse} />
              <InfoRow icon={Shield} label="Plano de Saúde" value={lead.planoSaude} />
              <InfoRow icon={Calendar} label="Cadastrado em" value={formatDateTime(lead.criadoEm)} />
              {lead.ultimoContato && (
                <InfoRow icon={Clock} label="Último contato" value={formatDateTime(lead.ultimoContato)} />
              )}
              {lead.operador && (
                <InfoRow icon={User} label="Operador" value={lead.operador.nome} />
              )}
            </div>

            {lead.fatorUrgencia && (
              <div className="mx-6 mb-4 p-3 bg-red-50 border border-red-200 rounded-xl flex items-center gap-2">
                <Zap className="w-4 h-4 text-red-600 fill-current flex-shrink-0" />
                <p className="text-sm text-red-700 font-medium">Lead marcado como urgente</p>
              </div>
            )}
          </div>

          {/* Interaction form */}
          <div className="bg-white rounded-2xl border border-gray-100 shadow-sm p-6">
            <h3 className="font-bold text-gray-900 mb-4 flex items-center gap-2">
              <MessageSquare className="w-4 h-4 text-primary-600" />
              Registrar Interação
            </h3>
            <div className="space-y-3">
              <div>
                <label className="text-xs font-medium text-gray-600 mb-1 block">Tipo</label>
                <select
                  value={interacaoForm.tipo}
                  onChange={(e) => setInteracaoForm({ ...interacaoForm, tipo: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-primary-500 bg-white"
                >
                  {INTERACAO_TIPOS.map((t) => <option key={t} value={t}>{t}</option>)}
                </select>
              </div>
              <div>
                <label className="text-xs font-medium text-gray-600 mb-1 block">Observações</label>
                <textarea
                  value={interacaoForm.conteudo}
                  onChange={(e) => setInteracaoForm({ ...interacaoForm, conteudo: e.target.value })}
                  placeholder="Descreva o que foi discutido..."
                  rows={3}
                  className="w-full px-3 py-2 border border-gray-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-primary-500 resize-none"
                />
              </div>
              <button
                onClick={() => addInteracao()}
                disabled={sendingInteracao}
                className="w-full flex items-center justify-center gap-2 bg-primary-700 hover:bg-primary-800 text-white py-2.5 rounded-xl text-sm font-semibold transition-colors disabled:opacity-70"
              >
                {sendingInteracao ? (
                  <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                ) : (
                  <Send className="w-4 h-4" />
                )}
                Registrar
              </button>
            </div>
          </div>
        </div>

        {/* Right column: AI + interactions */}
        <div className="lg:col-span-3 space-y-6">
          {/* AI Analysis Panel */}
          <div className="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden">
            <div className="px-6 py-4 border-b border-gray-100">
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-2.5">
                  <div className="w-9 h-9 bg-gradient-to-br from-purple-500 to-primary-600 rounded-xl flex items-center justify-center">
                    <Brain className="w-5 h-5 text-white" />
                  </div>
                  <div>
                    <h3 className="font-bold text-gray-900">Análise com IA</h3>
                    <p className="text-xs text-gray-500">Powered by IA</p>
                  </div>
                </div>
                <button
                  onClick={() => analyze()}
                  disabled={analyzing}
                  className="flex items-center gap-2 bg-gradient-to-r from-purple-600 to-primary-700 hover:from-purple-700 hover:to-primary-800 text-white px-4 py-2 rounded-xl text-sm font-semibold transition-all duration-200 shadow-sm disabled:opacity-70"
                >
                  {analyzing ? (
                    <>
                      <RefreshCw className="w-4 h-4 animate-spin" />
                      Analisando...
                    </>
                  ) : (
                    <>
                      <Sparkles className="w-4 h-4" />
                      {analysis ? 'Reanalisar' : 'Analisar com IA'}
                    </>
                  )}
                </button>
              </div>
            </div>

            {/* AI Loading */}
            {analyzing && (
              <div className="px-6 py-10 text-center">
                <div className="relative w-16 h-16 mx-auto mb-4">
                  <div className="w-16 h-16 border-4 border-purple-100 border-t-purple-600 rounded-full animate-spin" />
                  <Brain className="w-7 h-7 text-purple-600 absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2" />
                </div>
                <p className="text-sm font-medium text-gray-700">Analisando dados do lead...</p>
                <p className="text-xs text-gray-400 mt-1">A IA está processando as informações</p>
              </div>
            )}

            {/* AI Result */}
            {!analyzing && showAnalysis && analysis && (
              <div className="p-6 space-y-4 animate-slide-up">
                {/* Priority badge */}
                {prioridadeCfg && (
                  <div className={`inline-flex items-center gap-2 px-4 py-2 rounded-full border text-sm font-bold ${prioridadeCfg.color}`}>
                    <AlertTriangle className="w-4 h-4" />
                    Prioridade: {prioridadeCfg.label}
                  </div>
                )}

                {/* Main recommendation */}
                <div className="bg-gradient-to-br from-purple-600 via-primary-700 to-teal-600 rounded-2xl p-5 text-white">
                  <div className="flex items-center gap-2 mb-3">
                    <Sparkles className="w-5 h-5 text-yellow-300" />
                    <span className="font-bold text-sm uppercase tracking-wide opacity-90">Recomendação da IA</span>
                  </div>
                  <p className="text-white/95 leading-relaxed">{analysis.recomendacao}</p>
                </div>

                {/* Details grid */}
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                  <div className="bg-blue-50 border border-blue-100 rounded-xl p-4">
                    <div className="flex items-center gap-2 mb-2">
                      <MessageSquare className="w-4 h-4 text-blue-600" />
                      <span className="text-xs font-semibold text-blue-700 uppercase tracking-wide">Mensagem Sugerida</span>
                    </div>
                    <p className="text-sm text-blue-900 leading-relaxed">{analysis.mensagemSugerida}</p>
                  </div>

                  <div className="bg-orange-50 border border-orange-100 rounded-xl p-4">
                    <div className="flex items-center gap-2 mb-2">
                      <Clock className="w-4 h-4 text-orange-600" />
                      <span className="text-xs font-semibold text-orange-700 uppercase tracking-wide">Prazo Recomendado</span>
                    </div>
                    <p className="text-sm text-orange-900 leading-relaxed">{analysis.prazo}</p>
                  </div>
                </div>

                <div className="bg-gray-50 border border-gray-100 rounded-xl p-4">
                  <div className="flex items-center gap-2 mb-2">
                    <CheckCircle className="w-4 h-4 text-green-600" />
                    <span className="text-xs font-semibold text-gray-600 uppercase tracking-wide">Justificativa</span>
                  </div>
                  <p className="text-sm text-gray-700 leading-relaxed">{analysis.justificativa}</p>
                </div>
              </div>
            )}

            {/* Empty state */}
            {!analyzing && !showAnalysis && (
              <div className="px-6 py-10 text-center">
                <div className="w-16 h-16 bg-purple-50 rounded-2xl flex items-center justify-center mx-auto mb-4">
                  <Brain className="w-8 h-8 text-purple-400" />
                </div>
                <p className="text-sm font-medium text-gray-600">Análise de IA disponível</p>
                <p className="text-xs text-gray-400 mt-1">
                  Clique em "Analisar com IA" para obter recomendações personalizadas
                </p>
              </div>
            )}
          </div>

          {/* Interaction Timeline */}
          <div className="bg-white rounded-2xl border border-gray-100 shadow-sm p-6">
            <h3 className="font-bold text-gray-900 mb-4 flex items-center gap-2">
              <Clock className="w-4 h-4 text-primary-600" />
              Histórico de Interações
              {interacoes.length > 0 && (
                <span className="ml-1 w-5 h-5 bg-primary-100 text-primary-700 text-xs font-bold rounded-full flex items-center justify-center">
                  {interacoes.length}
                </span>
              )}
            </h3>

            {interacoesLoading ? (
              <div className="space-y-3">
                {[1, 2, 3].map((i) => (
                  <div key={i} className="h-16 bg-gray-100 rounded-xl animate-pulse" />
                ))}
              </div>
            ) : interacoes.length === 0 ? (
              <div className="text-center py-10 text-gray-300">
                <MessageSquare className="w-10 h-10 mx-auto mb-2 opacity-40" />
                <p className="text-sm">Nenhuma interação registrada</p>
              </div>
            ) : (
              <div className="relative">
                <div className="absolute left-4 top-0 bottom-0 w-px bg-gray-100" />
                <div className="space-y-4">
                  {interacoes.map((item) => (
                    <div key={item.id} className="relative pl-10">
                      <div className={`absolute left-2.5 top-3 w-3 h-3 rounded-full border-2 border-white ${
                        item.urgenciaDetectada ? 'bg-red-500' : 'bg-primary-500'
                      }`} />
                      <div className={`p-4 rounded-xl border ${
                        item.urgenciaDetectada
                          ? 'bg-red-50 border-red-100'
                          : 'bg-gray-50 border-gray-100'
                      }`}>
                        <div className="flex items-center justify-between mb-1.5">
                          <div className="flex items-center gap-2">
                            <span className={`text-xs font-semibold px-2 py-0.5 rounded-full ${
                              item.urgenciaDetectada
                                ? 'bg-red-100 text-red-700'
                                : 'bg-primary-100 text-primary-700'
                            }`}>
                              {item.tipo}
                            </span>
                            {item.urgenciaDetectada && (
                              <Zap className="w-3.5 h-3.5 text-red-500 fill-current" />
                            )}
                          </div>
                          <span className="text-xs text-gray-400">{formatDistanceToNow(item.realizadoEm)}</span>
                        </div>
                        {item.conteudo && (
                          <p className="text-sm text-gray-700 leading-relaxed">{item.conteudo}</p>
                        )}
                        <p className="text-xs text-gray-400 mt-2">por {item.operador.nome}</p>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </Layout>
  );
};

const InfoRow: React.FC<{ icon: React.ElementType; label: string; value?: string | null }> = ({
  icon: Icon,
  label,
  value,
}) => {
  if (!value) return null;
  return (
    <div className="flex items-start gap-3">
      <div className="w-7 h-7 bg-gray-50 rounded-lg flex items-center justify-center flex-shrink-0 mt-0.5">
        <Icon className="w-3.5 h-3.5 text-gray-500" />
      </div>
      <div className="min-w-0">
        <p className="text-xs text-gray-400 leading-none mb-0.5">{label}</p>
        <p className="text-sm text-gray-800 font-medium break-all">{value}</p>
      </div>
    </div>
  );
};

export default LeadDetail;
