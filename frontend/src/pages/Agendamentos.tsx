import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  Calendar, Plus, CheckCircle, Clock, X, ChevronDown, User,
  Stethoscope,
} from 'lucide-react';
import toast from 'react-hot-toast';
import Layout from '../components/Layout';
import StatusBadge from '../components/StatusBadge';
import { getAgendamentos, createAgendamento, confirmarAgendamento, getLeads, getUsuarios } from '../api/endpoints';
import type { CreateAgendamentoRequest } from '../types';
import { formatDateGroup, formatDateTime } from '../utils/dateUtils';
import { useAuth } from '../contexts/AuthContext';

const Agendamentos: React.FC = () => {
  const queryClient = useQueryClient();
  const { user } = useAuth();
  const [showModal, setShowModal] = useState(false);
  const [form, setForm] = useState<CreateAgendamentoRequest>({
    leadId: 0,
    operadorId: user?.id ?? 1,
    procedimento: '',
    dataHora: '',
  });

  const { data: agendamentos = [], isLoading } = useQuery({
    queryKey: ['agendamentos'],
    queryFn: getAgendamentos,
    refetchInterval: 60000,
  });

  const { data: leads = [] } = useQuery({
    queryKey: ['leads'],
    queryFn: getLeads,
  });

  const { data: usuarios = [] } = useQuery({
    queryKey: ['usuarios'],
    queryFn: getUsuarios,
  });

  const { mutate: createAg, isPending: creating } = useMutation({
    mutationFn: createAgendamento,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['agendamentos'] });
      setShowModal(false);
      setForm({ leadId: 0, operadorId: user?.id ?? 1, procedimento: '', dataHora: '' });
      toast.success('Agendamento criado com sucesso!');
    },
    onError: () => toast.error('Erro ao criar agendamento'),
  });

  const { mutate: confirmar } = useMutation({
    mutationFn: confirmarAgendamento,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['agendamentos'] });
      toast.success('Agendamento confirmado!');
    },
    onError: () => toast.error('Erro ao confirmar agendamento'),
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!form.leadId || !form.dataHora) {
      toast.error('Preencha todos os campos obrigatórios');
      return;
    }
    createAg(form);
  };

  // Group by date
  const grouped = agendamentos.reduce<Record<string, typeof agendamentos>>((acc, ag) => {
    const dateKey = new Date(ag.dataHora).toDateString();
    if (!acc[dateKey]) acc[dateKey] = [];
    acc[dateKey].push(ag);
    return acc;
  }, {});

  const sortedDates = Object.keys(grouped).sort(
    (a, b) => new Date(a).getTime() - new Date(b).getTime()
  );

  const totalPendente = agendamentos.filter((a) => a.status === 'Pendente').length;
  const totalConfirmado = agendamentos.filter((a) => a.status === 'Confirmado').length;

  return (
    <Layout title="Agendamentos">
      {/* Top stats */}
      <div className="grid grid-cols-3 gap-4 mb-6">
        <div className="bg-white rounded-2xl border border-gray-100 shadow-sm p-4 flex items-center gap-3">
          <div className="w-10 h-10 bg-blue-100 rounded-xl flex items-center justify-center">
            <Calendar className="w-5 h-5 text-blue-600" />
          </div>
          <div>
            <p className="text-2xl font-bold text-gray-900">{agendamentos.length}</p>
            <p className="text-xs text-gray-500">Total</p>
          </div>
        </div>
        <div className="bg-white rounded-2xl border border-gray-100 shadow-sm p-4 flex items-center gap-3">
          <div className="w-10 h-10 bg-yellow-100 rounded-xl flex items-center justify-center">
            <Clock className="w-5 h-5 text-yellow-600" />
          </div>
          <div>
            <p className="text-2xl font-bold text-gray-900">{totalPendente}</p>
            <p className="text-xs text-gray-500">Pendentes</p>
          </div>
        </div>
        <div className="bg-white rounded-2xl border border-gray-100 shadow-sm p-4 flex items-center gap-3">
          <div className="w-10 h-10 bg-green-100 rounded-xl flex items-center justify-center">
            <CheckCircle className="w-5 h-5 text-green-600" />
          </div>
          <div>
            <p className="text-2xl font-bold text-gray-900">{totalConfirmado}</p>
            <p className="text-xs text-gray-500">Confirmados</p>
          </div>
        </div>
      </div>

      {/* Header row */}
      <div className="flex items-center justify-between mb-6">
        <div>
          <h2 className="text-lg font-bold text-gray-900">Agenda</h2>
          <p className="text-sm text-gray-500">Agendamentos organizados por data</p>
        </div>
        <button
          onClick={() => setShowModal(true)}
          className="flex items-center gap-2 bg-primary-700 hover:bg-primary-800 text-white px-5 py-2.5 rounded-xl text-sm font-semibold transition-all duration-200 shadow-sm"
        >
          <Plus className="w-4 h-4" />
          Novo Agendamento
        </button>
      </div>

      {/* Grouped list */}
      {isLoading ? (
        <div className="space-y-4">
          {[1, 2, 3].map((i) => (
            <div key={i} className="h-32 bg-white rounded-2xl animate-pulse border border-gray-100" />
          ))}
        </div>
      ) : agendamentos.length === 0 ? (
        <div className="bg-white rounded-2xl border border-gray-100 shadow-sm py-20 text-center">
          <Calendar className="w-12 h-12 text-gray-200 mx-auto mb-3" />
          <p className="text-gray-500 font-medium">Nenhum agendamento</p>
          <p className="text-sm text-gray-400 mt-1">Crie seu primeiro agendamento</p>
        </div>
      ) : (
        <div className="space-y-6">
          {sortedDates.map((dateKey) => (
            <div key={dateKey}>
              {/* Date header */}
              <div className="flex items-center gap-3 mb-3">
                <div className="w-8 h-8 bg-primary-700 rounded-xl flex items-center justify-center">
                  <Calendar className="w-4 h-4 text-white" />
                </div>
                <h3 className="font-bold text-gray-900 capitalize">
                  {formatDateGroup(grouped[dateKey][0].dataHora)}
                </h3>
                <div className="flex-1 h-px bg-gray-100" />
                <span className="text-xs text-gray-400 font-medium">
                  {grouped[dateKey].length} agendamento{grouped[dateKey].length !== 1 ? 's' : ''}
                </span>
              </div>

              {/* Cards */}
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                {grouped[dateKey].map((ag) => (
                  <div
                    key={ag.id}
                    className={`bg-white rounded-2xl border shadow-sm p-5 transition-all duration-200 hover:shadow-md ${
                      ag.status === 'Confirmado'
                        ? 'border-green-200 bg-green-50/20'
                        : ag.status === 'Cancelado'
                        ? 'border-red-200 bg-red-50/20 opacity-75'
                        : 'border-gray-100'
                    }`}
                  >
                    <div className="flex items-start justify-between mb-3">
                      <div className="flex items-center gap-2">
                        <div className="w-9 h-9 bg-primary-100 rounded-xl flex items-center justify-center">
                          <User className="w-4 h-4 text-primary-700" />
                        </div>
                        <div>
                          <p className="font-semibold text-sm text-gray-900">{ag.lead.nome}</p>
                          <p className="text-xs text-gray-400">{ag.operador.nome}</p>
                        </div>
                      </div>
                      <StatusBadge status={ag.status} size="sm" />
                    </div>

                    {ag.procedimento && (
                      <div className="flex items-center gap-2 mb-3 text-sm text-gray-600">
                        <Stethoscope className="w-3.5 h-3.5 text-teal-600 flex-shrink-0" />
                        <span className="truncate">{ag.procedimento}</span>
                      </div>
                    )}

                    <div className="flex items-center gap-2 text-xs text-gray-500 mb-3">
                      <Clock className="w-3.5 h-3.5" />
                      <span>{formatDateTime(ag.dataHora)}</span>
                    </div>

                    {ag.lembreteEnviado && (
                      <div className="text-xs text-green-600 flex items-center gap-1 mb-3">
                        <CheckCircle className="w-3 h-3" />
                        Lembrete enviado
                      </div>
                    )}

                    {ag.status === 'Pendente' && (
                      <button
                        onClick={() => confirmar(ag.id)}
                        className="w-full flex items-center justify-center gap-1.5 bg-green-600 hover:bg-green-700 text-white py-2 rounded-xl text-xs font-semibold transition-colors"
                      >
                        <CheckCircle className="w-3.5 h-3.5" />
                        Confirmar
                      </button>
                    )}
                  </div>
                ))}
              </div>
            </div>
          ))}
        </div>
      )}

      {/* New Agendamento Modal */}
      {showModal && (
        <div className="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center z-50 p-4 animate-fade-in">
          <div className="bg-white rounded-2xl shadow-2xl w-full max-w-md animate-slide-up">
            <div className="flex items-center justify-between px-6 py-4 border-b border-gray-100">
              <div>
                <h2 className="text-lg font-bold text-gray-900">Novo Agendamento</h2>
                <p className="text-sm text-gray-500">Agendar consulta ou procedimento</p>
              </div>
              <button onClick={() => setShowModal(false)} className="p-2 hover:bg-gray-100 rounded-xl">
                <X className="w-5 h-5 text-gray-500" />
              </button>
            </div>

            <form onSubmit={handleSubmit} className="px-6 py-5 space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1.5">
                  Paciente (Lead) *
                </label>
                <div className="relative">
                  <select
                    value={form.leadId || ''}
                    onChange={(e) => setForm({ ...form, leadId: Number(e.target.value) })}
                    required
                    className="w-full px-3.5 py-2.5 border border-gray-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-primary-500 bg-white appearance-none"
                  >
                    <option value="">Selecione o lead...</option>
                    {leads.map((l) => (
                      <option key={l.id} value={l.id}>{l.nome}</option>
                    ))}
                  </select>
                  <ChevronDown className="absolute right-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400 pointer-events-none" />
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1.5">
                  Operador Responsável *
                </label>
                <div className="relative">
                  <select
                    value={form.operadorId}
                    onChange={(e) => setForm({ ...form, operadorId: Number(e.target.value) })}
                    required
                    className="w-full px-3.5 py-2.5 border border-gray-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-primary-500 bg-white appearance-none"
                  >
                    {usuarios.map((u) => (
                      <option key={u.id} value={u.id}>{u.nome}</option>
                    ))}
                  </select>
                  <ChevronDown className="absolute right-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400 pointer-events-none" />
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1.5">
                  Procedimento
                </label>
                <input
                  value={form.procedimento}
                  onChange={(e) => setForm({ ...form, procedimento: e.target.value })}
                  placeholder="Ex: Consulta Cardiologia, Exame de Sangue..."
                  className="w-full px-3.5 py-2.5 border border-gray-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1.5">
                  Data e Hora *
                </label>
                <input
                  type="datetime-local"
                  value={form.dataHora}
                  onChange={(e) => setForm({ ...form, dataHora: e.target.value })}
                  required
                  className="w-full px-3.5 py-2.5 border border-gray-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
                />
              </div>

              <div className="flex gap-3 pt-2">
                <button
                  type="button"
                  onClick={() => setShowModal(false)}
                  className="flex-1 py-2.5 border border-gray-200 text-gray-700 rounded-xl text-sm font-medium hover:bg-gray-50 transition-colors"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  disabled={creating}
                  className="flex-1 py-2.5 bg-primary-700 hover:bg-primary-800 text-white rounded-xl text-sm font-semibold transition-colors disabled:opacity-70 flex items-center justify-center gap-2"
                >
                  {creating ? (
                    <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                  ) : (
                    <Plus className="w-4 h-4" />
                  )}
                  {creating ? 'Criando...' : 'Criar Agendamento'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </Layout>
  );
};

export default Agendamentos;
