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
import {
  leads,
  interacoes,
  agendamentos,
  notificacoes,
  usuarios,
  computeDashboard,
  nextId,
} from './data';

const delay = (ms = 300) => new Promise<void>((r) => setTimeout(r, ms));

const err = (msg: string) => Promise.reject(new Error(msg));

// ── Auth ─────────────────────────────────────────────────────────────────────

const MOCK_CREDENTIALS: Record<string, { password: string; userId: number }> = {
  'admin@hospitalrafael.com':         { password: 'Admin@123', userId: 1 },
  'carlos.silva@hospitalrafael.com':  { password: 'Admin@123', userId: 2 },
  'mariana.costa@hospitalrafael.com': { password: 'Admin@123', userId: 3 },
};

export const login = async (email: string, senha: string): Promise<AuthResponse> => {
  await delay(600);
  const cred = MOCK_CREDENTIALS[email];
  if (!cred || cred.password !== senha) {
    return err('Credenciais inválidas');
  }
  const usuario = usuarios.find((u) => u.id === cred.userId)!;
  return {
    token: `mock-jwt-token-${usuario.id}-${Date.now()}`,
    tipo: 'Bearer',
    usuario,
  };
};

// ── Dashboard ─────────────────────────────────────────────────────────────────

export const getDashboard = async (): Promise<DashboardData> => {
  await delay(400);
  return computeDashboard();
};

// ── Leads ─────────────────────────────────────────────────────────────────────

export const getLeads = async (): Promise<LeadResponse[]> => {
  await delay(350);
  return [...leads];
};

export const getLead = async (id: number): Promise<LeadResponse> => {
  await delay(250);
  const lead = leads.find((l) => l.id === id);
  if (!lead) return err(`Lead ${id} não encontrado`);
  return { ...lead };
};

export const createLead = async (data: CreateLeadRequest): Promise<LeadResponse> => {
  await delay(500);
  const newLead: LeadResponse = {
    id: nextId(),
    nome: data.nome,
    email: data.email,
    telefone: Number(data.telefone.replace(/\D/g, '')),
    canalOrigem: data.canalOrigem,
    status: 'Novo',
    leadScore: 'Médio',
    prioridade: 3,
    fatorUrgencia: false,
    procedimentoInteresse: data.procedimentoInteresse,
    planoSaude: data.planoSaude,
    criadoEm: new Date().toISOString(),
  };
  leads.push(newLead);
  return { ...newLead };
};

export const updateLeadStatus = async (id: number, status: string): Promise<LeadResponse> => {
  await delay(300);
  const lead = leads.find((l) => l.id === id);
  if (!lead) return err(`Lead ${id} não encontrado`);
  lead.status = status;
  return { ...lead };
};

export const deleteLead = async (id: number): Promise<void> => {
  await delay(400);
  const idx = leads.findIndex((l) => l.id === id);
  if (idx === -1) return err(`Lead ${id} não encontrado`);
  leads.splice(idx, 1);
};

export const getLeadsUrgentes = async (): Promise<LeadResponse[]> => {
  await delay(300);
  return leads.filter((l) => l.fatorUrgencia).map((l) => ({ ...l }));
};

export const getLeadsPrioridade = async (): Promise<LeadResponse[]> => {
  await delay(300);
  return [...leads].sort((a, b) => (a.prioridade ?? 99) - (b.prioridade ?? 99));
};

// ── AI Analysis ──────────────────────────────────────────────────────────────

const AI_TEMPLATES: Array<{
  recomendacao: string;
  mensagemSugerida: string;
  justificativa: string;
  prazo: string;
}> = [
  {
    recomendacao: 'Prioridade máxima de contato. O perfil indica alto risco cardiovascular e histórico de sintomas agudos. Recomenda-se ligação imediata e encaminhamento para triagem hospitalar.',
    mensagemSugerida: 'Olá! Sou do Hospital São Rafael. Notei seu interesse em cardiologia e gostaríamos de agendar uma avaliação com urgência. Posso confirmar o horário agora?',
    justificativa: 'Score "Muito Alto" combinado com fator de urgência ativo e procedimento de alto risco clínico indica probabilidade elevada de conversão e necessidade de atendimento imediato.',
    prazo: 'Imediato — nas próximas 2 horas',
  },
  {
    recomendacao: 'Lead com alto potencial de conversão. Demonstrou interesse genuíno e possui plano de saúde compatível. Agendar consulta nos próximos 2 dias aumenta significativamente a taxa de conversão.',
    mensagemSugerida: 'Olá! Temos uma disponibilidade especial para sua consulta ainda esta semana. Seu plano de saúde cobre o procedimento. Gostaria de confirmar o agendamento?',
    justificativa: 'Combinação de score alto, canal de origem qualificado e presença de plano de saúde são indicadores fortes de conversão segundo nosso modelo preditivo.',
    prazo: '2 dias úteis',
  },
  {
    recomendacao: 'Lead em fase de consideração. Recomenda-se envio de material educativo sobre o procedimento de interesse seguido de contato personalizado para esclarecer dúvidas.',
    mensagemSugerida: 'Olá! Preparamos um guia completo sobre o procedimento que você tem interesse. Posso enviar por WhatsApp? Também estou disponível para esclarecer qualquer dúvida.',
    justificativa: 'Score médio indica lead ainda em fase de pesquisa. Abordagem educativa aumenta confiança e prepara para conversão nas próximas interações.',
    prazo: '3–5 dias úteis',
  },
];

export const analisarLead = async (id: number): Promise<LeadAnalysis> => {
  await delay(1800);
  const lead = leads.find((l) => l.id === id);
  if (!lead) return err(`Lead ${id} não encontrado`);

  const isUrgent = lead.fatorUrgencia;
  const score = lead.leadScore ?? 'Médio';

  let templateIdx = 2;
  if (isUrgent || score === 'Muito Alto') templateIdx = 0;
  else if (score === 'Alto') templateIdx = 1;

  const tpl = AI_TEMPLATES[templateIdx];
  const proc = lead.procedimentoInteresse ?? 'procedimento de interesse';
  const prioridade = isUrgent ? 'URGENTE' : score === 'Muito Alto' ? 'ALTA' : score === 'Alto' ? 'ALTA' : 'MEDIA';

  return {
    prioridade,
    recomendacao: tpl.recomendacao,
    mensagemSugerida: tpl.mensagemSugerida.replace('cardiologia', proc.toLowerCase()),
    justificativa: tpl.justificativa,
    prazo: tpl.prazo,
  };
};

// ── Agendamentos ──────────────────────────────────────────────────────────────

export const getAgendamentos = async (): Promise<AgendamentoResponse[]> => {
  await delay(350);
  return [...agendamentos];
};

export const createAgendamento = async (data: CreateAgendamentoRequest): Promise<AgendamentoResponse> => {
  await delay(500);
  const lead = leads.find((l) => l.id === data.leadId);
  const operador = usuarios.find((u) => u.id === data.operadorId);
  if (!lead || !operador) return err('Lead ou operador inválido');

  const newAg: AgendamentoResponse = {
    id: nextId(),
    procedimento: data.procedimento,
    dataHora: data.dataHora,
    status: 'Pendente',
    lembreteEnviado: false,
    lead: { id: lead.id, nome: lead.nome },
    operador: { id: operador.id, nome: operador.nome },
  };
  agendamentos.push(newAg);
  return { ...newAg };
};

export const confirmarAgendamento = async (id: number): Promise<AgendamentoResponse> => {
  await delay(300);
  const ag = agendamentos.find((a) => a.id === id);
  if (!ag) return err(`Agendamento ${id} não encontrado`);
  ag.status = 'Confirmado';
  ag.lembreteEnviado = true;
  return { ...ag };
};

// ── Notificações ──────────────────────────────────────────────────────────────

export const getNotificacoes = async (): Promise<NotificacaoResponse[]> => {
  await delay(300);
  return [...notificacoes].sort(
    (a, b) => new Date(b.criadoEm).getTime() - new Date(a.criadoEm).getTime()
  );
};

export const marcarNotificacaoLida = async (id: number): Promise<void> => {
  await delay(200);
  const notif = notificacoes.find((n) => n.id === id);
  if (notif) notif.lida = true;
};

// ── Interações ────────────────────────────────────────────────────────────────

export const getInteracoesByLead = async (leadId: number): Promise<InteracaoResponse[]> => {
  await delay(300);
  return interacoes
    .filter((i) => i.lead.id === leadId)
    .sort((a, b) => new Date(b.realizadoEm).getTime() - new Date(a.realizadoEm).getTime());
};

export const createInteracao = async (data: CreateInteracaoRequest): Promise<InteracaoResponse> => {
  await delay(400);
  const lead = leads.find((l) => l.id === data.leadId);
  const operador = usuarios.find((u) => u.id === data.operadorId);
  if (!lead || !operador) return err('Lead ou operador inválido');

  const urgenciaDetectada =
    !!data.conteudo &&
    /urgent|urgente|dor|infarto|emergência|emergencia|grave/i.test(data.conteudo);

  const newInt: InteracaoResponse = {
    id: nextId(),
    tipo: data.tipo,
    conteudo: data.conteudo,
    urgenciaDetectada,
    urgenciaNivel: urgenciaDetectada ? 'ALTA' : 'BAIXA',
    realizadoEm: new Date().toISOString(),
    lead: { id: lead.id, nome: lead.nome },
    operador: { id: operador.id, nome: operador.nome },
  };
  interacoes.push(newInt);

  lead.ultimoContato = newInt.realizadoEm;
  if (urgenciaDetectada) lead.fatorUrgencia = true;

  return { ...newInt };
};

// ── Usuários ──────────────────────────────────────────────────────────────────

export const getUsuarios = async (): Promise<UsuarioResponse[]> => {
  await delay(200);
  return [...usuarios];
};
