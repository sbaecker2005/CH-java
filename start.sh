#!/bin/bash
# ============================================================
# CRM Hospital São Rafael — Script de inicialização completo
# Roda backend (Spring Boot) e frontend (React) juntos
# ============================================================

set -e

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_DIR="$ROOT_DIR/crm-hospital"
FRONTEND_DIR="$ROOT_DIR/frontend"

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo ""
echo -e "${BLUE}╔══════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║    CRM Hospital São Rafael — Startup         ║${NC}"
echo -e "${BLUE}╚══════════════════════════════════════════════╝${NC}"
echo ""

# ─── Verificar variáveis de ambiente ────────────────────────
if [ -z "$ANTHROPIC_API_KEY" ]; then
    echo -e "${YELLOW}⚠  ANTHROPIC_API_KEY não definida — IA usará fallback inteligente${NC}"
fi

if [ -z "$ORACLE_USER" ]; then
    echo -e "${YELLOW}⚠  ORACLE_USER não definida — usando valor padrão do application.properties${NC}"
fi

# ─── Instalar dependências do frontend ──────────────────────
echo -e "${BLUE}► Instalando dependências do frontend...${NC}"
cd "$FRONTEND_DIR"
if [ ! -d "node_modules" ]; then
    npm install --silent
    echo -e "${GREEN}✓ Dependências instaladas${NC}"
else
    echo -e "${GREEN}✓ node_modules já existe${NC}"
fi

# ─── Iniciar Backend ─────────────────────────────────────────
echo ""
echo -e "${BLUE}► Iniciando Backend (Spring Boot :8080)...${NC}"
cd "$BACKEND_DIR"
mvn spring-boot:run -q \
    -Dspring-boot.run.jvmArguments="-Xmx512m" \
    ${ORACLE_USER:+-Dspring.datasource.username=$ORACLE_USER} \
    ${ORACLE_PASSWORD:+-Dspring.datasource.password=$ORACLE_PASSWORD} \
    ${ANTHROPIC_API_KEY:+-Dspring.ai.anthropic.api-key=$ANTHROPIC_API_KEY} \
    &
BACKEND_PID=$!
echo -e "${GREEN}✓ Backend iniciando (PID: $BACKEND_PID)${NC}"

# ─── Aguardar backend estar pronto ──────────────────────────
echo -e "${BLUE}► Aguardando backend iniciar...${NC}"
for i in {1..30}; do
    if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
        echo -e "${GREEN}✓ Backend pronto!${NC}"
        break
    fi
    sleep 2
    echo -n "."
done

# ─── Iniciar Frontend ─────────────────────────────────────────
echo ""
echo -e "${BLUE}► Iniciando Frontend (Vite :5173)...${NC}"
cd "$FRONTEND_DIR"
npm run dev &
FRONTEND_PID=$!
echo -e "${GREEN}✓ Frontend iniciando (PID: $FRONTEND_PID)${NC}"

echo ""
echo -e "${GREEN}╔══════════════════════════════════════════════╗${NC}"
echo -e "${GREEN}║  Sistema iniciado com sucesso!               ║${NC}"
echo -e "${GREEN}║                                              ║${NC}"
echo -e "${GREEN}║  Frontend:  http://localhost:5173            ║${NC}"
echo -e "${GREEN}║  Backend:   http://localhost:8080            ║${NC}"
echo -e "${GREEN}║  Swagger:   http://localhost:8080/swagger-ui.html ║${NC}"
echo -e "${GREEN}║                                              ║${NC}"
echo -e "${GREEN}║  Login padrão:                               ║${NC}"
echo -e "${GREEN}║  Email: admin@hospitalrafael.com             ║${NC}"
echo -e "${GREEN}║  Senha: password                             ║${NC}"
echo -e "${GREEN}║                                              ║${NC}"
echo -e "${GREEN}║  Pressione Ctrl+C para encerrar tudo         ║${NC}"
echo -e "${GREEN}╚══════════════════════════════════════════════╝${NC}"

# ─── Cleanup ao pressionar Ctrl+C ───────────────────────────
cleanup() {
    echo ""
    echo -e "${RED}► Encerrando serviços...${NC}"
    kill $BACKEND_PID 2>/dev/null || true
    kill $FRONTEND_PID 2>/dev/null || true
    echo -e "${GREEN}✓ Serviços encerrados${NC}"
    exit 0
}
trap cleanup SIGINT SIGTERM

# Mantém o script rodando
wait
