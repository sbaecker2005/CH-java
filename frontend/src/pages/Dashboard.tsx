import React, { useState, useCallback } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  Users, Zap, UserPlus, TrendingUp, Sparkles, ArrowRight,
} from 'lucide-react';
import { PieChart, Pie, Cell, Tooltip, ResponsiveContainer, Legend } from 'recharts';
import { useNavigate } from 'react-router-dom';
import Layout from '../components/Layout';
import KpiCard from '../components/KpiCard';
import LeadCard from '../components/LeadCard';
import StatusBadge from '../components/StatusBadge';
import { getDashboard, getLeads, updateLeadStatus } from '../api/endpoints';
import type { LeadResponse } from '../types';
import { formatDistanceToNow } from '../utils/dateUtils';

const STATUS_COLUMNS = [
  'Novo',
  'Em Atendimento',
  'Aguardando Retorno',
  'Convertido',
  'Cancelado',
] as const;

const COLUMN_COLORS: Record<string, string> = {
  Novo: 'border-t-blue-500 bg-blue-50/30',
  'Em Atendimento': 'border-t-yellow-500 bg-yellow-50/30',
  'Aguardando Retorno': 'border-t-orange-500 bg-orange-50/30',
  Convertido: 'border-t-green-500 bg-green-50/30',
  Cancelado: 'border-t-red-500 bg-red-50/30',
};

const COLUMN_HEADER_COLORS: Record<string, string> = {
  Novo: 'text-blue-700',
  'Em Atendimento': 'text-yellow-700',
  'Aguardando Retorno': 'text-orange-700',
  Convertido: 'text-green-700',
  Cancelado: 'text-red-700',
};

const PIE_COLORS = ['#3B82F6', '#F59E0B', '#F97316', '#22C55E', '#EF4444'];

const Dashboard: React.FC = () => {
  const queryClient = useQueryClient();
  const navigate = useNavigate();
  const [dragOver, setDragOver] = useState<string | null>(null);
  const [draggingLead, setDraggingLead] = useState<LeadResponse | null>(null);

  const { data: dashboard, isLoading: dashLoading } = useQuery({
    queryKey: ['dashboard'],
    queryFn: getDashboard,
    refetchInterval: 60000,
  });

  const { data: leads = [], isLoading: leadsLoading } = useQuery({
    queryKey: ['leads'],
    queryFn: getLeads,
    refetchInterval: 30000,
  });

  const { mutate: changeStatus } = useMutation({
    mutationFn: ({ id, status }: { id: number; status: string }) => updateLeadStatus(id, status),
    onMutate: async ({ id, status }) => {
      await queryClient.cancelQueries({ queryKey: ['leads'] });
      const prev = queryClient.getQueryData<LeadResponse[]>(['leads']);
      queryClient.setQueryData<LeadResponse[]>(['leads'], (old = []) =>
        old.map((l) => (l.id === id ? { ...l, status } : l))
      );
      return { prev };
    },
    onError: (_err, _vars, ctx) => {
      if (ctx?.prev) queryClient.setQueryData(['leads'], ctx.prev);
    },
    onSettled: () => {
      queryClient.invalidateQueries({ queryKey: ['leads'] });
      queryClient.invalidateQueries({ queryKey: ['dashboard'] });
    },
  });

  const getLeadsByStatus = useCallback(
    (status: string) => leads.filter((l) => l.status === status),
    [leads]
  );

  const handleDragStart = useCallback((e: React.DragEvent, lead: LeadResponse) => {
    setDraggingLead(lead);
    e.dataTransfer.effectAllowed = 'move';
    e.dataTransfer.setData('leadId', String(lead.id));
  }, []);

  const handleDragOver = useCallback((e: React.DragEvent, status: string) => {
    e.preventDefault();
    e.dataTransfer.dropEffect = 'move';
    setDragOver(status);
  }, []);

  const handleDrop = useCallback(
    (e: React.DragEvent, status: string) => {
      e.preventDefault();
      const leadId = Number(e.dataTransfer.getData('leadId'));
      if (leadId && draggingLead?.status !== status) {
        changeStatus({ id: leadId, status });
      }
      setDragOver(null);
      setDraggingLead(null);
    },
    [changeStatus, draggingLead]
  );

  const handleDragLeave = useCallback(() => {
    setDragOver(null);
  }, []);

  const pieData = dashboard
    ? Object.entries(dashboard.leadsPorStatus).map(([name, value]) => ({ name, value }))
    : [];

  const urgentLeads = leads.filter((l) => l.fatorUrgencia).slice(0, 5);

  return (
    <Layout title="Dashboard">
      {/* KPI Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
        {dashLoading ? (
          Array.from({ length: 4 }).map((_, i) => (
            <div key={i} className="bg-white rounded-2xl p-6 border border-gray-100 animate-pulse h-32" />
          ))
        ) : (
          <>
            <KpiCard
              title="Total de Leads"
              value={dashboard?.totalLeads ?? 0}
              icon={Users}
              color="blue"
              subtitle="todos os leads cadastrados"
            />
            <KpiCard
              title="Leads Urgentes"
              value={dashboard?.leadsUrgentes ?? 0}
              icon={Zap}
              color="red"
              subtitle="requerem atenção imediata"
            />
            <KpiCard
              title="Leads Novos"
              value={dashboard?.leadsNovos ?? 0}
              icon={UserPlus}
              color="green"
              subtitle="aguardando primeiro contato"
            />
            <KpiCard
              title="Taxa de Conversão"
              value={`${(dashboard?.taxaConversao ?? 0).toFixed(1)}%`}
              icon={TrendingUp}
              color="purple"
              subtitle="leads convertidos em pacientes"
            />
          </>
        )}
      </div>

      {/* Kanban + Chart Row */}
      <div className="mb-8">
        <div className="flex items-center justify-between mb-4">
          <div>
            <h2 className="text-lg font-bold text-gray-900">Kanban de Leads</h2>
            <p className="text-sm text-gray-500">Arraste os cards para atualizar o status</p>
          </div>
          <button
            onClick={() => navigate('/leads')}
            className="flex items-center gap-1.5 text-sm text-primary-700 font-medium hover:text-primary-800 transition-colors"
          >
            Ver todos
            <ArrowRight className="w-4 h-4" />
          </button>
        </div>

        {/* Kanban board */}
        <div className="flex gap-4 overflow-x-auto pb-4">
          {STATUS_COLUMNS.map((status) => {
            const colLeads = getLeadsByStatus(status);
            const isOver = dragOver === status;

            return (
              <div
                key={status}
                className={`flex-shrink-0 w-64 bg-white rounded-2xl border-t-4 shadow-sm transition-all duration-200 ${
                  COLUMN_COLORS[status]
                } ${isOver ? 'ring-2 ring-primary-400 scale-[1.01]' : ''}`}
                onDragOver={(e) => handleDragOver(e, status)}
                onDrop={(e) => handleDrop(e, status)}
                onDragLeave={handleDragLeave}
              >
                <div className="px-4 py-3 border-b border-gray-100">
                  <div className="flex items-center justify-between">
                    <span className={`text-sm font-semibold ${COLUMN_HEADER_COLORS[status]}`}>
                      {status}
                    </span>
                    <span className="w-6 h-6 bg-gray-100 text-gray-600 text-xs font-bold rounded-full flex items-center justify-center">
                      {colLeads.length}
                    </span>
                  </div>
                </div>

                <div
                  className={`p-3 space-y-2.5 min-h-[200px] transition-colors duration-200 ${
                    isOver ? 'bg-primary-50/50' : ''
                  }`}
                >
                  {leadsLoading ? (
                    <div className="space-y-2">
                      {[1, 2].map((i) => (
                        <div key={i} className="h-20 bg-gray-100 rounded-xl animate-pulse" />
                      ))}
                    </div>
                  ) : colLeads.length === 0 ? (
                    <div className="flex items-center justify-center h-24 text-gray-300 text-xs">
                      Nenhum lead
                    </div>
                  ) : (
                    colLeads.slice(0, 8).map((lead) => (
                      <LeadCard
                        key={lead.id}
                        lead={lead}
                        draggable
                        onDragStart={handleDragStart}
                      />
                    ))
                  )}
                  {colLeads.length > 8 && (
                    <button
                      onClick={() => navigate('/leads')}
                      className="w-full text-center text-xs text-gray-400 hover:text-primary-600 py-2 transition-colors"
                    >
                      +{colLeads.length - 8} mais
                    </button>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      </div>

      {/* Bottom Row: Chart + Urgent Leads */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Pie Chart */}
        <div className="bg-white rounded-2xl border border-gray-100 shadow-sm p-6">
          <h3 className="text-base font-bold text-gray-900 mb-1">Distribuição por Status</h3>
          <p className="text-sm text-gray-500 mb-4">Visão geral do pipeline</p>
          {pieData.length > 0 ? (
            <ResponsiveContainer width="100%" height={260}>
              <PieChart>
                <Pie
                  data={pieData}
                  cx="50%"
                  cy="50%"
                  innerRadius={60}
                  outerRadius={100}
                  paddingAngle={3}
                  dataKey="value"
                >
                  {pieData.map((_, index) => (
                    <Cell key={`cell-${index}`} fill={PIE_COLORS[index % PIE_COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip
                  formatter={(value: number) => [value, 'Leads']}
                  contentStyle={{ borderRadius: '12px', border: '1px solid #e5e7eb', fontSize: '13px' }}
                />
                <Legend
                  formatter={(value) => <span className="text-xs text-gray-600">{value}</span>}
                />
              </PieChart>
            </ResponsiveContainer>
          ) : (
            <div className="h-64 flex items-center justify-center text-gray-300">
              {dashLoading ? (
                <div className="w-8 h-8 border-4 border-gray-200 border-t-primary-600 rounded-full animate-spin" />
              ) : (
                <p className="text-sm">Nenhum dado disponível</p>
              )}
            </div>
          )}
        </div>

        {/* Urgent Leads */}
        <div className="bg-white rounded-2xl border border-gray-100 shadow-sm p-6">
          <div className="flex items-center justify-between mb-4">
            <div>
              <h3 className="text-base font-bold text-gray-900 mb-1">Leads Urgentes</h3>
              <p className="text-sm text-gray-500">Requerem atenção imediata</p>
            </div>
            <div className="w-9 h-9 bg-red-100 rounded-xl flex items-center justify-center">
              <Zap className="w-5 h-5 text-red-600 fill-current" />
            </div>
          </div>

          {urgentLeads.length === 0 ? (
            <div className="flex flex-col items-center justify-center h-48 text-gray-300">
              <Zap className="w-10 h-10 mb-2 opacity-30" />
              <p className="text-sm">Nenhum lead urgente no momento</p>
            </div>
          ) : (
            <div className="space-y-3">
              {urgentLeads.map((lead) => (
                <button
                  key={lead.id}
                  onClick={() => navigate(`/leads/${lead.id}`)}
                  className="w-full flex items-center gap-3 p-3 rounded-xl hover:bg-red-50 border border-red-100 transition-all duration-200 group text-left"
                >
                  <div className="w-9 h-9 bg-red-500 rounded-full flex items-center justify-center text-white text-sm font-bold flex-shrink-0">
                    {lead.nome.charAt(0).toUpperCase()}
                  </div>
                  <div className="flex-1 min-w-0">
                    <p className="font-semibold text-gray-900 text-sm truncate">{lead.nome}</p>
                    <p className="text-xs text-gray-500 truncate">{lead.procedimentoInteresse ?? lead.email}</p>
                  </div>
                  <div className="text-right flex-shrink-0">
                    <StatusBadge status={lead.status} size="sm" />
                    <p className="text-xs text-gray-400 mt-1">{formatDistanceToNow(lead.criadoEm)}</p>
                  </div>
                </button>
              ))}

              {leads.filter((l) => l.fatorUrgencia).length > 5 && (
                <button
                  onClick={() => navigate('/leads')}
                  className="w-full text-center text-sm text-primary-600 font-medium hover:text-primary-700 py-2 flex items-center justify-center gap-1"
                >
                  Ver todos os urgentes
                  <ArrowRight className="w-4 h-4" />
                </button>
              )}
            </div>
          )}
        </div>
      </div>

      {/* AI Notification Banner */}
      <div className="mt-6 bg-gradient-to-r from-purple-600 via-primary-700 to-teal-600 rounded-2xl p-6 text-white shadow-lg">
        <div className="flex items-center gap-4">
          <div className="w-12 h-12 bg-white/20 rounded-xl flex items-center justify-center flex-shrink-0">
            <Sparkles className="w-6 h-6 text-white" />
          </div>
          <div className="flex-1">
            <h3 className="font-bold text-lg">Análise com Inteligência Artificial</h3>
            <p className="text-white/80 text-sm mt-1">
              Selecione um lead para obter recomendações de IA, pontuação de prioridade, mensagens sugeridas e análise de urgência em tempo real.
            </p>
          </div>
          <button
            onClick={() => navigate('/leads')}
            className="bg-white/20 hover:bg-white/30 text-white font-semibold px-5 py-2.5 rounded-xl transition-all duration-200 flex items-center gap-2 flex-shrink-0"
          >
            Analisar Leads
            <ArrowRight className="w-4 h-4" />
          </button>
        </div>
      </div>
    </Layout>
  );
};

export default Dashboard;
