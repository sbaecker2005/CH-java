import apiClient from './client';
import type {
  LeadResponse,
  AgendamentoResponse,
  InteracaoResponse,
  NotificacaoResponse,
  DashboardData,
  LeadAnalysis,
  AuthResponse,
  CreateLeadRequest,
  CreateAgendamentoRequest,
  CreateInteracaoRequest,
  UsuarioResponse,
} from '../types';
import * as mock from '../mocks/api';

// Set to false to use the real Java backend
const USE_MOCK = true;

// Auth
export const login = (email: string, senha: string): Promise<AuthResponse> =>
  USE_MOCK
    ? mock.login(email, senha)
    : apiClient.post('/auth/login', { email, senha }).then((r) => r.data);

// Dashboard
export const getDashboard = (): Promise<DashboardData> =>
  USE_MOCK
    ? mock.getDashboard()
    : apiClient.get('/dashboard').then((r) => r.data);

// Leads
export const getLeads = (): Promise<LeadResponse[]> =>
  USE_MOCK
    ? mock.getLeads()
    : apiClient.get('/leads').then((r) => r.data);

export const getLead = (id: number): Promise<LeadResponse> =>
  USE_MOCK
    ? mock.getLead(id)
    : apiClient.get(`/leads/${id}`).then((r) => r.data);

export const createLead = (data: CreateLeadRequest): Promise<LeadResponse> =>
  USE_MOCK
    ? mock.createLead(data)
    : apiClient.post('/leads', data).then((r) => r.data);

export const updateLeadStatus = (id: number, status: string): Promise<LeadResponse> =>
  USE_MOCK
    ? mock.updateLeadStatus(id, status)
    : apiClient.patch(`/leads/${id}/status`, { status }).then((r) => r.data);

export const deleteLead = (id: number): Promise<void> =>
  USE_MOCK
    ? mock.deleteLead(id)
    : apiClient.delete(`/leads/${id}`).then(() => undefined);

export const getLeadsUrgentes = (): Promise<LeadResponse[]> =>
  USE_MOCK
    ? mock.getLeadsUrgentes()
    : apiClient.get('/leads/urgentes').then((r) => r.data);

export const getLeadsPrioridade = (): Promise<LeadResponse[]> =>
  USE_MOCK
    ? mock.getLeadsPrioridade()
    : apiClient.get('/leads/prioridade').then((r) => r.data);

// AI Analysis
export const analisarLead = (id: number): Promise<LeadAnalysis> =>
  USE_MOCK
    ? mock.analisarLead(id)
    : apiClient.post(`/ai/leads/${id}/analisar`).then((r) => r.data);

// Agendamentos
export const getAgendamentos = (): Promise<AgendamentoResponse[]> =>
  USE_MOCK
    ? mock.getAgendamentos()
    : apiClient.get('/agendamentos').then((r) => r.data);

export const createAgendamento = (data: CreateAgendamentoRequest): Promise<AgendamentoResponse> =>
  USE_MOCK
    ? mock.createAgendamento(data)
    : apiClient.post('/agendamentos', data).then((r) => r.data);

export const confirmarAgendamento = (id: number): Promise<AgendamentoResponse> =>
  USE_MOCK
    ? mock.confirmarAgendamento(id)
    : apiClient.patch(`/agendamentos/${id}/confirmar`).then((r) => r.data);

// Notificações
export const getNotificacoes = (): Promise<NotificacaoResponse[]> =>
  USE_MOCK
    ? mock.getNotificacoes()
    : apiClient.get('/notificacoes').then((r) => r.data);

export const marcarNotificacaoLida = (id: number): Promise<void> =>
  USE_MOCK
    ? mock.marcarNotificacaoLida(id)
    : apiClient.patch(`/notificacoes/${id}/lida`).then(() => undefined);

// Interações
export const getInteracoesByLead = (leadId: number): Promise<InteracaoResponse[]> =>
  USE_MOCK
    ? mock.getInteracoesByLead(leadId)
    : apiClient.get(`/interacoes/lead/${leadId}`).then((r) => r.data);

export const createInteracao = (data: CreateInteracaoRequest): Promise<InteracaoResponse> =>
  USE_MOCK
    ? mock.createInteracao(data)
    : apiClient.post('/interacoes', data).then((r) => r.data);

// Usuários
export const getUsuarios = (): Promise<UsuarioResponse[]> =>
  USE_MOCK
    ? mock.getUsuarios()
    : apiClient.get('/usuarios').then((r) => r.data);
