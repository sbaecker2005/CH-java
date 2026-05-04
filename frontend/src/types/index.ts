export interface LeadResponse {
  id: number;
  nome: string;
  email: string;
  telefone: number;
  canalOrigem?: string;
  status: string;
  leadScore?: string;
  prioridade?: number;
  fatorUrgencia: boolean;
  procedimentoInteresse?: string;
  planoSaude?: string;
  ultimoContato?: string;
  criadoEm: string;
  operador?: { id: number; nome: string };
}

export interface UsuarioResponse {
  id: number;
  nome: string;
  email: string;
  role: string;
}

export interface AgendamentoResponse {
  id: number;
  procedimento?: string;
  dataHora: string;
  status: string;
  lembreteEnviado: boolean;
  lead: { id: number; nome: string };
  operador: { id: number; nome: string };
}

export interface InteracaoResponse {
  id: number;
  tipo: string;
  conteudo?: string;
  urgenciaDetectada: boolean;
  urgenciaNivel: string;
  realizadoEm: string;
  lead: { id: number; nome: string };
  operador: { id: number; nome: string };
}

export interface NotificacaoResponse {
  id: number;
  mensagem: string;
  leadNome?: string;
  lida: boolean;
  geradoPorIa: boolean;
  criadoEm: string;
  lead: { id: number; nome: string };
}

export interface DashboardData {
  totalLeads: number;
  leadsNovos: number;
  leadsUrgentes: number;
  taxaConversao: number;
  leadsPorStatus: Record<string, number>;
}

export interface LeadAnalysis {
  recomendacao: string;
  prioridade: string;
  mensagemSugerida: string;
  justificativa: string;
  prazo: string;
}

export interface AuthUser {
  id: number;
  nome: string;
  email: string;
  role: string;
}

export interface AuthResponse {
  token: string;
  tipo: string;
  usuario: AuthUser;
}

export interface CreateLeadRequest {
  nome: string;
  email: string;
  telefone: string;
  canalOrigem?: string;
  procedimentoInteresse?: string;
  planoSaude?: string;
}

export interface CreateAgendamentoRequest {
  leadId: number;
  operadorId: number;
  procedimento?: string;
  dataHora: string;
}

export interface CreateInteracaoRequest {
  leadId: number;
  operadorId: number;
  tipo: string;
  conteudo?: string;
}
