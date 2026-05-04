-- ============================================================
-- V3: Dados iniciais para demonstração (FIAP Next)
-- Senha: Admin@123 (BCrypt hash)
-- ============================================================

-- Admin do sistema
INSERT INTO USUARIO (ID, NOME, EMAIL, SENHA, DOC, TELEFONE, DATANASC, ROLE)
VALUES (
    SQ_USUARIO.NEXTVAL,
    'Administrador',
    'admin@hospitalrafael.com',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.',
    '00000000000',
    11999990000,
    DATE '1985-01-15',
    'ADMIN'
);

-- Operadores de CRM
INSERT INTO USUARIO (ID, NOME, EMAIL, SENHA, DOC, TELEFONE, DATANASC, ROLE)
VALUES (
    SQ_USUARIO.NEXTVAL,
    'Carlos Silva',
    'carlos.silva@hospitalrafael.com',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.',
    '12345678901',
    11999990001,
    DATE '1990-05-15',
    'OPERADOR'
);

INSERT INTO USUARIO (ID, NOME, EMAIL, SENHA, DOC, TELEFONE, DATANASC, ROLE)
VALUES (
    SQ_USUARIO.NEXTVAL,
    'Mariana Costa',
    'mariana.costa@hospitalrafael.com',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.',
    '98765432100',
    11999990002,
    DATE '1993-08-22',
    'OPERADOR'
);

-- Leads de demonstração
INSERT INTO LEAD (ID, NOME, TELEFONE, EMAIL, CANAL_ORIGEM, STATUS, LEAD_SCORE, PRIORIDADE, FATOR_URGENCIA, PROCEDIMENTO_INTERESSE, PLANO_SAUDE, CRIADO_EM)
VALUES (SQ_LEAD.NEXTVAL, 'Ana Pereira',    11977770001, 'ana.pereira@email.com',    'Instagram',  'Novo',              'Muito Alto', 1, 1, 'Consulta Cardiologista', 'Unimed',    SYSDATE - 5);

INSERT INTO LEAD (ID, NOME, TELEFONE, EMAIL, CANAL_ORIGEM, STATUS, LEAD_SCORE, PRIORIDADE, FATOR_URGENCIA, PROCEDIMENTO_INTERESSE, PLANO_SAUDE, CRIADO_EM)
VALUES (SQ_LEAD.NEXTVAL, 'Bruno Martins',  11977770002, 'bruno.martins@email.com',  'WhatsApp',   'Em Atendimento',    'Alto',       2, 0, 'Check-up Geral',         'SulAmérica', SYSDATE - 4);

INSERT INTO LEAD (ID, NOME, TELEFONE, EMAIL, CANAL_ORIGEM, STATUS, LEAD_SCORE, PRIORIDADE, FATOR_URGENCIA, PROCEDIMENTO_INTERESSE, PLANO_SAUDE, CRIADO_EM)
VALUES (SQ_LEAD.NEXTVAL, 'Carla Santos',   11977770003, 'carla.santos@email.com',   'Indicação',  'Aguardando Retorno','Médio',      3, 0, 'Ortopedia',              NULL,        SYSDATE - 3);

INSERT INTO LEAD (ID, NOME, TELEFONE, EMAIL, CANAL_ORIGEM, STATUS, LEAD_SCORE, PRIORIDADE, FATOR_URGENCIA, PROCEDIMENTO_INTERESSE, PLANO_SAUDE, CRIADO_EM)
VALUES (SQ_LEAD.NEXTVAL, 'Diego Ferreira', 11977770004, 'diego.ferreira@email.com', 'Google Ads', 'Convertido',        'Alto',       2, 0, 'Dermatologia',           'Bradesco',  SYSDATE - 7);

INSERT INTO LEAD (ID, NOME, TELEFONE, EMAIL, CANAL_ORIGEM, STATUS, LEAD_SCORE, PRIORIDADE, FATOR_URGENCIA, PROCEDIMENTO_INTERESSE, PLANO_SAUDE, CRIADO_EM)
VALUES (SQ_LEAD.NEXTVAL, 'Elena Rocha',    11977770005, 'elena.rocha@email.com',    'Site',       'Novo',              'Baixo',      4, 0, 'Clínica Geral',          NULL,        SYSDATE - 1);

INSERT INTO LEAD (ID, NOME, TELEFONE, EMAIL, CANAL_ORIGEM, STATUS, LEAD_SCORE, PRIORIDADE, FATOR_URGENCIA, PROCEDIMENTO_INTERESSE, PLANO_SAUDE, CRIADO_EM)
VALUES (SQ_LEAD.NEXTVAL, 'Fábio Lima',     11977770006, 'fabio.lima@email.com',     'WhatsApp',   'Novo',              'Muito Alto', 1, 1, 'UTI Cardiológica',       'Amil',      SYSDATE);

COMMIT;
