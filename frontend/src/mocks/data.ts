import type {
  LeadResponse,
  AgendamentoResponse,
  InteracaoResponse,
  NotificacaoResponse,
  DashboardData,
  UsuarioResponse,
} from '../types';

const d = (daysAgo: number) =>
  new Date(Date.now() - daysAgo * 86_400_000).toISOString();

const future = (daysAhead: number, hour = 10) => {
  const dt = new Date();
  dt.setDate(dt.getDate() + daysAhead);
  dt.setHours(hour, 0, 0, 0);
  return dt.toISOString();
};

export const usuarios: UsuarioResponse[] = [
  { id: 1, nome: 'Administrador', email: 'admin@hospitalrafael.com', role: 'ADMIN' },
  { id: 2, nome: 'Carlos Silva', email: 'carlos.silva@hospitalrafael.com', role: 'OPERADOR' },
  { id: 3, nome: 'Mariana Costa', email: 'mariana.costa@hospitalrafael.com', role: 'OPERADOR' },
];

export const leads: LeadResponse[] = [
  // ── NOVO (15 leads) ──────────────────────────────────────────
  { id: 1,  nome: 'João Silva',          email: 'joao.silva@email.com',          telefone: 11977780001, canalOrigem: 'Instagram',  status: 'Novo',              leadScore: 'Muito Alto', prioridade: 1, fatorUrgencia: true,  procedimentoInteresse: 'Consulta Cardiologista', planoSaude: 'Unimed',      criadoEm: d(1),  operador: { id: 2, nome: 'Carlos Silva' } },
  { id: 2,  nome: 'Maria Santos',        email: 'maria.santos@email.com',        telefone: 11977780002, canalOrigem: 'WhatsApp',   status: 'Novo',              leadScore: 'Alto',       prioridade: 2, fatorUrgencia: false, procedimentoInteresse: 'Check-up Geral',         planoSaude: 'SulAmérica', criadoEm: d(2),  operador: { id: 3, nome: 'Mariana Costa' } },
  { id: 3,  nome: 'Pedro Oliveira',      email: 'pedro.oliveira@email.com',      telefone: 11977780003, canalOrigem: 'Google Ads', status: 'Novo',              leadScore: 'Médio',      prioridade: 3, fatorUrgencia: false, procedimentoInteresse: 'Ortopedia',              planoSaude: undefined,    criadoEm: d(3) },
  { id: 4,  nome: 'Ana Souza',           email: 'ana.souza@email.com',           telefone: 11977780004, canalOrigem: 'Site',       status: 'Novo',              leadScore: 'Alto',       prioridade: 2, fatorUrgencia: true,  procedimentoInteresse: 'Dermatologia',           planoSaude: 'Bradesco',   criadoEm: d(1) },
  { id: 5,  nome: 'Carlos Lima',         email: 'carlos.lima@email.com',         telefone: 11977780005, canalOrigem: 'Indicação',  status: 'Novo',              leadScore: 'Muito Alto', prioridade: 1, fatorUrgencia: true,  procedimentoInteresse: 'UTI Cardiológica',       planoSaude: 'Amil',       criadoEm: d(2) },
  { id: 6,  nome: 'Lucia Ferreira',      email: 'lucia.ferreira@email.com',      telefone: 11977780006, canalOrigem: 'Instagram',  status: 'Novo',              leadScore: 'Baixo',      prioridade: 4, fatorUrgencia: false, procedimentoInteresse: 'Clínica Geral',          planoSaude: undefined,    criadoEm: d(5) },
  { id: 7,  nome: 'Felipe Costa',        email: 'felipe.costa@email.com',        telefone: 11977780007, canalOrigem: 'WhatsApp',   status: 'Novo',              leadScore: 'Alto',       prioridade: 2, fatorUrgencia: false, procedimentoInteresse: 'Neurologia',             planoSaude: 'Unimed',     criadoEm: d(3) },
  { id: 8,  nome: 'Sandra Pereira',      email: 'sandra.pereira@email.com',      telefone: 11977780008, canalOrigem: 'Facebook',   status: 'Novo',              leadScore: 'Médio',      prioridade: 3, fatorUrgencia: false, procedimentoInteresse: 'Pediatria',              planoSaude: 'NotreDame',  criadoEm: d(4) },
  { id: 9,  nome: 'Ricardo Carvalho',    email: 'ricardo.carvalho@email.com',    telefone: 11977780009, canalOrigem: 'Google Ads', status: 'Novo',              leadScore: 'Muito Alto', prioridade: 1, fatorUrgencia: true,  procedimentoInteresse: 'Oncologia',              planoSaude: 'SulAmérica', criadoEm: d(1) },
  { id: 10, nome: 'Fernanda Alves',      email: 'fernanda.alves@email.com',      telefone: 11977780010, canalOrigem: 'Indicação',  status: 'Novo',              leadScore: 'Alto',       prioridade: 2, fatorUrgencia: false, procedimentoInteresse: 'Ginecologia',            planoSaude: 'Unimed',     criadoEm: d(6) },
  { id: 11, nome: 'Camila Melo',         email: 'camila.melo@email.com',         telefone: 11977780014, canalOrigem: 'Facebook',   status: 'Novo',              leadScore: 'Muito Alto', prioridade: 1, fatorUrgencia: true,  procedimentoInteresse: 'Cardiologia',            planoSaude: 'Amil',       criadoEm: d(3) },
  { id: 12, nome: 'Beatriz Dias',        email: 'beatriz.dias@email.com',        telefone: 11977780018, canalOrigem: 'WhatsApp',   status: 'Novo',              leadScore: 'Muito Alto', prioridade: 1, fatorUrgencia: true,  procedimentoInteresse: 'Cirurgia Geral',         planoSaude: 'NotreDame',  criadoEm: d(2) },
  { id: 13, nome: 'Paulo Cunha',         email: 'paulo.cunha@email.com',         telefone: 11977780023, canalOrigem: 'Facebook',   status: 'Novo',              leadScore: 'Muito Alto', prioridade: 1, fatorUrgencia: true,  procedimentoInteresse: 'Consulta Cardiologista', planoSaude: 'SulAmérica', criadoEm: d(1) },
  { id: 14, nome: 'Monica Teixeira',     email: 'monica.teixeira@email.com',     telefone: 11977780028, canalOrigem: 'WhatsApp',   status: 'Novo',              leadScore: 'Muito Alto', prioridade: 1, fatorUrgencia: true,  procedimentoInteresse: 'UTI Cardiológica',       planoSaude: 'Amil',       criadoEm: d(2) },
  { id: 15, nome: 'Gustavo Figueiredo',  email: 'gustavo.figueiredo@email.com',  telefone: 11977780033, canalOrigem: 'WhatsApp',   status: 'Novo',              leadScore: 'Muito Alto', prioridade: 1, fatorUrgencia: true,  procedimentoInteresse: 'Oncologia',              planoSaude: 'Bradesco',   criadoEm: d(1) },

  // ── EM ATENDIMENTO (12 leads) ─────────────────────────────────
  { id: 16, nome: 'Adriana Andrade',     email: 'adriana.andrade@email.com',     telefone: 11977780036, canalOrigem: 'Indicação',  status: 'Em Atendimento',    leadScore: 'Alto',       prioridade: 2, fatorUrgencia: true,  procedimentoInteresse: 'Dermatologia',           planoSaude: 'Amil',       criadoEm: d(5),  operador: { id: 2, nome: 'Carlos Silva' } },
  { id: 17, nome: 'Roberto Machado',     email: 'roberto.machado@email.com',     telefone: 11977780037, canalOrigem: 'Google Ads', status: 'Em Atendimento',    leadScore: 'Muito Alto', prioridade: 1, fatorUrgencia: true,  procedimentoInteresse: 'Consulta Cardiologista', planoSaude: 'Unimed',     criadoEm: d(7),  operador: { id: 3, nome: 'Mariana Costa' } },
  { id: 18, nome: 'Claudia Nunes',       email: 'claudia.nunes@email.com',       telefone: 11977780038, canalOrigem: 'Instagram',  status: 'Em Atendimento',    leadScore: 'Médio',      prioridade: 3, fatorUrgencia: false, procedimentoInteresse: 'Clínica Geral',          planoSaude: 'SulAmérica', criadoEm: d(10), operador: { id: 2, nome: 'Carlos Silva' } },
  { id: 19, nome: 'Alexandre Batista',   email: 'alexandre.batista@email.com',   telefone: 11977780039, canalOrigem: 'WhatsApp',   status: 'Em Atendimento',    leadScore: 'Alto',       prioridade: 2, fatorUrgencia: false, procedimentoInteresse: 'Neurologia',             planoSaude: 'Bradesco',   criadoEm: d(12) },
  { id: 20, nome: 'Fabio Mendes',        email: 'fabio.mendes@email.com',        telefone: 11977780041, canalOrigem: 'Facebook',   status: 'Em Atendimento',    leadScore: 'Muito Alto', prioridade: 1, fatorUrgencia: true,  procedimentoInteresse: 'UTI Cardiológica',       planoSaude: 'Amil',       criadoEm: d(6) },
  { id: 21, nome: 'Rosana Morais',       email: 'rosana.morais@email.com',       telefone: 11977780046, canalOrigem: 'Site',       status: 'Em Atendimento',    leadScore: 'Muito Alto', prioridade: 1, fatorUrgencia: true,  procedimentoInteresse: 'Oncologia',              planoSaude: 'Bradesco',   criadoEm: d(5) },
  { id: 22, nome: 'Sergio Fernandez',    email: 'sergio.fernandez@email.com',    telefone: 11977780049, canalOrigem: 'Google Ads', status: 'Em Atendimento',    leadScore: 'Alto',       prioridade: 2, fatorUrgencia: true,  procedimentoInteresse: 'Reumatologia',           planoSaude: 'SulAmérica', criadoEm: d(7) },
  { id: 23, nome: 'Marcus Azevedo',      email: 'marcus.azevedo@email.com',      telefone: 11977780051, canalOrigem: 'WhatsApp',   status: 'Em Atendimento',    leadScore: 'Muito Alto', prioridade: 1, fatorUrgencia: true,  procedimentoInteresse: 'Cirurgia Geral',         planoSaude: 'NotreDame',  criadoEm: d(8) },
  { id: 24, nome: 'Carla Magalhaes',     email: 'carla.magalhaes@email.com',     telefone: 11977780054, canalOrigem: 'Indicação',  status: 'Em Atendimento',    leadScore: 'Alto',       prioridade: 2, fatorUrgencia: true,  procedimentoInteresse: 'Check-up Geral',         planoSaude: 'Amil',       criadoEm: d(10) },
  { id: 25, nome: 'Larissa Vasconcelos', email: 'larissa.vasconcelos@email.com', telefone: 11977780056, canalOrigem: 'Instagram',  status: 'Em Atendimento',    leadScore: 'Muito Alto', prioridade: 1, fatorUrgencia: true,  procedimentoInteresse: 'Dermatologia',           planoSaude: 'SulAmérica', criadoEm: d(6) },
  { id: 26, nome: 'Leticia Pacheco',     email: 'leticia.pacheco@email.com',     telefone: 11977780064, canalOrigem: 'Site',       status: 'Em Atendimento',    leadScore: 'Alto',       prioridade: 2, fatorUrgencia: true,  procedimentoInteresse: 'Cirurgia Geral',         planoSaude: 'Bradesco',   criadoEm: d(8) },
  { id: 27, nome: 'Luiz Almeida',        email: 'luiz.almeida@email.com',        telefone: 11977780061, canalOrigem: 'Google Ads', status: 'Em Atendimento',    leadScore: 'Muito Alto', prioridade: 1, fatorUrgencia: true,  procedimentoInteresse: 'Oncologia',              planoSaude: 'Unimed',     criadoEm: d(7) },

  // ── AGUARDANDO RETORNO (8 leads) ─────────────────────────────
  { id: 28, nome: 'Simone Queiroz',      email: 'simone.queiroz@email.com',      telefone: 11977780066, canalOrigem: 'Indicação',  status: 'Aguardando Retorno', leadScore: 'Alto',       prioridade: 2, fatorUrgencia: true,  procedimentoInteresse: 'Reumatologia',           planoSaude: 'Amil',       criadoEm: d(12) },
  { id: 29, nome: 'Fernando Guimaraes', email: 'fernando.guimaraes@email.com',  telefone: 11977780067, canalOrigem: 'Google Ads', status: 'Aguardando Retorno', leadScore: 'Muito Alto', prioridade: 1, fatorUrgencia: true,  procedimentoInteresse: 'Cardiologia',            planoSaude: 'SulAmérica', criadoEm: d(15) },
  { id: 30, nome: 'Valeria Abreu',       email: 'valeria.abreu@email.com',       telefone: 11977780068, canalOrigem: 'Instagram',  status: 'Aguardando Retorno', leadScore: 'Médio',      prioridade: 3, fatorUrgencia: false, procedimentoInteresse: 'Nutrição',               planoSaude: undefined,    criadoEm: d(20) },
  { id: 31, nome: 'Rogerio Pimentel',   email: 'rogerio.pimentel@email.com',    telefone: 11977780069, canalOrigem: 'WhatsApp',   status: 'Aguardando Retorno', leadScore: 'Alto',       prioridade: 2, fatorUrgencia: false, procedimentoInteresse: 'Dermatologia',           planoSaude: 'Bradesco',   criadoEm: d(18) },
  { id: 32, nome: 'Livia Cordeiro',      email: 'livia.cordeiro@email.com',      telefone: 11977780070, canalOrigem: 'Site',       status: 'Aguardando Retorno', leadScore: 'Baixo',      prioridade: 4, fatorUrgencia: false, procedimentoInteresse: 'Check-up Geral',         planoSaude: 'Unimed',     criadoEm: d(25) },
  { id: 33, nome: 'Carla Santos',        email: 'carla.santos@email.com',        telefone: 11977770003, canalOrigem: 'Indicação',  status: 'Aguardando Retorno', leadScore: 'Médio',      prioridade: 3, fatorUrgencia: false, procedimentoInteresse: 'Ortopedia',              planoSaude: undefined,    criadoEm: d(3) },
  { id: 34, nome: 'Thiago Cardoso',      email: 'thiago.cardoso@email.com',      telefone: 11977780029, canalOrigem: 'Facebook',   status: 'Aguardando Retorno', leadScore: 'Alto',       prioridade: 2, fatorUrgencia: false, procedimentoInteresse: 'Endocrinologia',         planoSaude: 'Unimed',     criadoEm: d(7) },
  { id: 35, nome: 'Priscilla Xavier',    email: 'priscilla.xavier@email.com',    telefone: 11977780058, canalOrigem: 'Site',       status: 'Aguardando Retorno', leadScore: 'Médio',      prioridade: 3, fatorUrgencia: false, procedimentoInteresse: 'Clínica Geral',          planoSaude: 'Unimed',     criadoEm: d(18) },

  // ── CONVERTIDO (10 leads) ────────────────────────────────────
  { id: 36, nome: 'Diego Ferreira',      email: 'diego.ferreira@email.com',      telefone: 11977770004, canalOrigem: 'Google Ads', status: 'Convertido',         leadScore: 'Alto',       prioridade: 2, fatorUrgencia: false, procedimentoInteresse: 'Dermatologia',           planoSaude: 'Bradesco',   criadoEm: d(30), operador: { id: 2, nome: 'Carlos Silva' } },
  { id: 37, nome: 'Ana Pereira',         email: 'ana.pereira@email.com',         telefone: 11977770001, canalOrigem: 'Instagram',  status: 'Convertido',         leadScore: 'Muito Alto', prioridade: 1, fatorUrgencia: false, procedimentoInteresse: 'Consulta Cardiologista', planoSaude: 'Unimed',    criadoEm: d(25), operador: { id: 3, nome: 'Mariana Costa' } },
  { id: 38, nome: 'Bruno Martins',       email: 'bruno.martins@email.com',       telefone: 11977770002, canalOrigem: 'WhatsApp',   status: 'Convertido',         leadScore: 'Alto',       prioridade: 2, fatorUrgencia: false, procedimentoInteresse: 'Check-up Geral',         planoSaude: 'SulAmérica', criadoEm: d(22), operador: { id: 2, nome: 'Carlos Silva' } },
  { id: 39, nome: 'Helena Moreira',      email: 'helena.moreira@email.com',      telefone: 11977780034, canalOrigem: 'Site',       status: 'Convertido',         leadScore: 'Alto',       prioridade: 2, fatorUrgencia: false, procedimentoInteresse: 'Gastroenterologia',      planoSaude: 'Unimed',     criadoEm: d(35) },
  { id: 40, nome: 'Rafael Cavalcante',   email: 'rafael.cavalcante@email.com',   telefone: 11977780019, canalOrigem: 'Site',       status: 'Convertido',         leadScore: 'Alto',       prioridade: 2, fatorUrgencia: false, procedimentoInteresse: 'Fisioterapia',           planoSaude: 'Bradesco',   criadoEm: d(40) },
  { id: 41, nome: 'Natalia Braga',       email: 'natalia.braga@email.com',       telefone: 11977780052, canalOrigem: 'Site',       status: 'Convertido',         leadScore: 'Alto',       prioridade: 2, fatorUrgencia: false, procedimentoInteresse: 'Fisioterapia',           planoSaude: 'Bradesco',   criadoEm: d(28) },
  { id: 42, nome: 'Eliane Campos',       email: 'eliane.campos@email.com',       telefone: 11977780044, canalOrigem: 'Instagram',  status: 'Convertido',         leadScore: 'Alto',       prioridade: 2, fatorUrgencia: false, procedimentoInteresse: 'Endocrinologia',         planoSaude: 'SulAmérica', criadoEm: d(20) },
  { id: 43, nome: 'Marcia Vieira',       email: 'marcia.vieira@email.com',       telefone: 11977780042, canalOrigem: 'Indicação',  status: 'Convertido',         leadScore: 'Alto',       prioridade: 2, fatorUrgencia: false, procedimentoInteresse: 'Ginecologia',            planoSaude: 'Unimed',     criadoEm: d(32) },
  { id: 44, nome: 'Henrique Leite',      email: 'henrique.leite@email.com',      telefone: 11977780047, canalOrigem: 'Facebook',   status: 'Convertido',         leadScore: 'Alto',       prioridade: 2, fatorUrgencia: false, procedimentoInteresse: 'Cardiologia',            planoSaude: 'Unimed',     criadoEm: d(45) },
  { id: 45, nome: 'Isabela Goncalves',   email: 'isabela.goncalves@email.com',   telefone: 11977780062, canalOrigem: 'Instagram',  status: 'Convertido',         leadScore: 'Alto',       prioridade: 2, fatorUrgencia: false, procedimentoInteresse: 'Ginecologia',            planoSaude: 'SulAmérica', criadoEm: d(38) },

  // ── CANCELADO (5 leads) ──────────────────────────────────────
  { id: 46, nome: 'Tatiana Barbosa',     email: 'tatiana.barbosa@email.com',     telefone: 11977780040, canalOrigem: 'Site',       status: 'Cancelado',          leadScore: 'Baixo',      prioridade: 4, fatorUrgencia: false, procedimentoInteresse: 'Psiquiatria',            planoSaude: undefined,    criadoEm: d(30) },
  { id: 47, nome: 'Mario Nogueira',      email: 'mario.nogueira@email.com',      telefone: 11977780055, canalOrigem: 'Google Ads', status: 'Cancelado',          leadScore: 'Baixo',      prioridade: 4, fatorUrgencia: false, procedimentoInteresse: 'Nutrição',               planoSaude: undefined,    criadoEm: d(35) },
  { id: 48, nome: 'Vera Rodrigues',      email: 'vera.rodrigues@email.com',      telefone: 11977780050, canalOrigem: 'Instagram',  status: 'Cancelado',          leadScore: 'Baixo',      prioridade: 4, fatorUrgencia: false, procedimentoInteresse: 'Pediatria',              planoSaude: undefined,    criadoEm: d(40) },
  { id: 49, nome: 'Nelson Tavares',      email: 'nelson.tavares@email.com',      telefone: 11977780045, canalOrigem: 'WhatsApp',   status: 'Cancelado',          leadScore: 'Baixo',      prioridade: 4, fatorUrgencia: false, procedimentoInteresse: 'Ortopedia',              planoSaude: undefined,    criadoEm: d(50) },
  { id: 50, nome: 'Antonio Rezende',     email: 'antonio.rezende@email.com',     telefone: 11977780065, canalOrigem: 'Facebook',   status: 'Cancelado',          leadScore: 'Baixo',      prioridade: 4, fatorUrgencia: false, procedimentoInteresse: 'Oftalmologia',           planoSaude: 'Unimed',     criadoEm: d(30) },
];

export const interacoes: InteracaoResponse[] = [
  { id: 1,  tipo: 'Ligação',   conteudo: 'Paciente relatou dor no peito há 3 dias. Informado sobre consulta de urgência disponível amanhã.', urgenciaDetectada: true,  urgenciaNivel: 'ALTA',  realizadoEm: d(0),  lead: { id: 1,  nome: 'João Silva' },   operador: { id: 2, nome: 'Carlos Silva' } },
  { id: 2,  tipo: 'WhatsApp',  conteudo: 'Enviado link de agendamento online. Paciente confirmou interesse no check-up executivo.',           urgenciaDetectada: false, urgenciaNivel: 'BAIXA', realizadoEm: d(1),  lead: { id: 2,  nome: 'Maria Santos' }, operador: { id: 3, nome: 'Mariana Costa' } },
  { id: 3,  tipo: 'E-mail',    conteudo: 'Encaminhado material informativo sobre o procedimento de check-up. Aguardando retorno.',             urgenciaDetectada: false, urgenciaNivel: 'BAIXA', realizadoEm: d(2),  lead: { id: 2,  nome: 'Maria Santos' }, operador: { id: 3, nome: 'Mariana Costa' } },
  { id: 4,  tipo: 'Ligação',   conteudo: 'Confirmado agendamento para semana que vem. Plano de saúde Unimed aceito.',                         urgenciaDetectada: false, urgenciaNivel: 'BAIXA', realizadoEm: d(1),  lead: { id: 36, nome: 'Diego Ferreira' }, operador: { id: 2, nome: 'Carlos Silva' } },
  { id: 5,  tipo: 'Presencial',conteudo: 'Primeira consulta realizada. Médico solicitou exames complementares. Retorno em 15 dias.',          urgenciaDetectada: false, urgenciaNivel: 'BAIXA', realizadoEm: d(7),  lead: { id: 36, nome: 'Diego Ferreira' }, operador: { id: 2, nome: 'Carlos Silva' } },
  { id: 6,  tipo: 'WhatsApp',  conteudo: 'Lembrete de consulta enviado. Paciente confirmou presença.',                                        urgenciaDetectada: false, urgenciaNivel: 'BAIXA', realizadoEm: d(14), lead: { id: 36, nome: 'Diego Ferreira' }, operador: { id: 2, nome: 'Carlos Silva' } },
  { id: 7,  tipo: 'Ligação',   conteudo: 'Urgência identificada: sintomas de infarto. Encaminhado ao pronto-socorro imediatamente.',          urgenciaDetectada: true,  urgenciaNivel: 'ALTA',  realizadoEm: d(0),  lead: { id: 5,  nome: 'Carlos Lima' },   operador: { id: 3, nome: 'Mariana Costa' } },
  { id: 8,  tipo: 'Chat',      conteudo: 'Paciente iniciou contato pelo site. Interesse em cirurgia oncológica. Enviado para triagem.',       urgenciaDetectada: false, urgenciaNivel: 'MEDIA', realizadoEm: d(3),  lead: { id: 9,  nome: 'Ricardo Carvalho' }, operador: { id: 2, nome: 'Carlos Silva' } },
  { id: 9,  tipo: 'E-mail',    conteudo: 'Proposta de valor enviada. Plano SulAmérica cobre 80% do procedimento.',                           urgenciaDetectada: false, urgenciaNivel: 'BAIXA', realizadoEm: d(5),  lead: { id: 37, nome: 'Ana Pereira' },  operador: { id: 3, nome: 'Mariana Costa' } },
  { id: 10, tipo: 'Ligação',   conteudo: 'Conversado sobre opções de cirurgia. Paciente muito satisfeito com atendimento.',                   urgenciaDetectada: false, urgenciaNivel: 'BAIXA', realizadoEm: d(10), lead: { id: 37, nome: 'Ana Pereira' },  operador: { id: 3, nome: 'Mariana Costa' } },
];

export const agendamentos: AgendamentoResponse[] = [
  { id: 1, procedimento: 'Consulta Cardiologista',    dataHora: future(1, 9),  status: 'Confirmado',  lembreteEnviado: true,  lead: { id: 1,  nome: 'João Silva' },        operador: { id: 2, nome: 'Carlos Silva' } },
  { id: 2, procedimento: 'Check-up Geral',            dataHora: future(1, 14), status: 'Pendente',    lembreteEnviado: false, lead: { id: 2,  nome: 'Maria Santos' },      operador: { id: 3, nome: 'Mariana Costa' } },
  { id: 3, procedimento: 'Consulta Oncologia',        dataHora: future(2, 10), status: 'Confirmado',  lembreteEnviado: true,  lead: { id: 9,  nome: 'Ricardo Carvalho' },  operador: { id: 2, nome: 'Carlos Silva' } },
  { id: 4, procedimento: 'Cirurgia Cardíaca',         dataHora: future(3, 8),  status: 'Pendente',    lembreteEnviado: false, lead: { id: 5,  nome: 'Carlos Lima' },        operador: { id: 3, nome: 'Mariana Costa' } },
  { id: 5, procedimento: 'Retorno Dermatologia',      dataHora: future(4, 11), status: 'Confirmado',  lembreteEnviado: true,  lead: { id: 36, nome: 'Diego Ferreira' },    operador: { id: 2, nome: 'Carlos Silva' } },
  { id: 6, procedimento: 'Consulta Ginecologia',      dataHora: future(5, 15), status: 'Pendente',    lembreteEnviado: false, lead: { id: 10, nome: 'Fernanda Alves' },    operador: { id: 3, nome: 'Mariana Costa' } },
  { id: 7, procedimento: 'Avaliação Ortopédica',      dataHora: future(7, 9),  status: 'Confirmado',  lembreteEnviado: false, lead: { id: 3,  nome: 'Pedro Oliveira' },    operador: { id: 2, nome: 'Carlos Silva' } },
];

export const notificacoes: NotificacaoResponse[] = [
  { id: 1,  mensagem: 'Lead João Silva requer atenção urgente: possível infarto.',                   leadNome: 'João Silva',        lida: false, geradoPorIa: true,  criadoEm: d(0),  lead: { id: 1,  nome: 'João Silva' } },
  { id: 2,  mensagem: 'Novo lead cadastrado: Maria Santos (Check-up Geral).',                        leadNome: 'Maria Santos',      lida: false, geradoPorIa: false, criadoEm: d(0),  lead: { id: 2,  nome: 'Maria Santos' } },
  { id: 3,  mensagem: 'IA identificou urgência no lead Carlos Lima: UTI Cardiológica.',              leadNome: 'Carlos Lima',       lida: false, geradoPorIa: true,  criadoEm: d(0),  lead: { id: 5,  nome: 'Carlos Lima' } },
  { id: 4,  mensagem: 'Lead Ricardo Carvalho aguarda triagem oncológica urgente.',                   leadNome: 'Ricardo Carvalho',  lida: false, geradoPorIa: true,  criadoEm: d(1),  lead: { id: 9,  nome: 'Ricardo Carvalho' } },
  { id: 5,  mensagem: 'Agendamento confirmado: João Silva - Consulta Cardiologista amanhã às 9h.',   leadNome: 'João Silva',        lida: true,  geradoPorIa: false, criadoEm: d(1),  lead: { id: 1,  nome: 'João Silva' } },
  { id: 6,  mensagem: 'IA recomenda contato imediato com Beatriz Dias (Cirurgia Geral).',            leadNome: 'Beatriz Dias',      lida: false, geradoPorIa: true,  criadoEm: d(1),  lead: { id: 12, nome: 'Beatriz Dias' } },
  { id: 7,  mensagem: 'Lead convertido: Diego Ferreira agendou procedimento de Dermatologia.',       leadNome: 'Diego Ferreira',    lida: true,  geradoPorIa: false, criadoEm: d(2),  lead: { id: 36, nome: 'Diego Ferreira' } },
  { id: 8,  mensagem: 'Lead convertido: Ana Pereira realizou consulta de Cardiologia.',              leadNome: 'Ana Pereira',       lida: true,  geradoPorIa: false, criadoEm: d(3),  lead: { id: 37, nome: 'Ana Pereira' } },
  { id: 9,  mensagem: 'IA detectou padrão de urgência em 3 novos leads este mês.',                  leadNome: undefined,           lida: true,  geradoPorIa: true,  criadoEm: d(3),  lead: { id: 1,  nome: 'João Silva' } },
  { id: 10, mensagem: 'Lembrete: Fernando Guimarães aguarda retorno há 15 dias (Cardiologia).',     leadNome: 'Fernando Guimaraes',lida: false, geradoPorIa: true,  criadoEm: d(4),  lead: { id: 29, nome: 'Fernando Guimaraes' } },
  { id: 11, nome: 'Monica Teixeira',  mensagem: 'Lead Monica Teixeira necessita atenção: UTI Cardiológica urgente.', leadNome: 'Monica Teixeira', lida: false, geradoPorIa: true, criadoEm: d(2), lead: { id: 14, nome: 'Monica Teixeira' } } as NotificacaoResponse,
  { id: 12, mensagem: 'Taxa de conversão do mês: 20%. Meta atingida!',                              leadNome: undefined,           lida: true,  geradoPorIa: true,  criadoEm: d(7),  lead: { id: 37, nome: 'Ana Pereira' } },
];

export const computeDashboard = (): DashboardData => {
  const total = leads.length;
  const urgentes = leads.filter((l) => l.fatorUrgencia).length;
  const novos = leads.filter((l) => l.status === 'Novo').length;
  const convertidos = leads.filter((l) => l.status === 'Convertido').length;
  const taxaConversao = total > 0 ? (convertidos / total) * 100 : 0;

  const leadsPorStatus: Record<string, number> = {};
  for (const l of leads) {
    leadsPorStatus[l.status] = (leadsPorStatus[l.status] ?? 0) + 1;
  }

  return { totalLeads: total, leadsNovos: novos, leadsUrgentes: urgentes, taxaConversao, leadsPorStatus };
};

let _nextId = 51;
export const nextId = () => _nextId++;
