"""Gera o PDF de entrega Sprint 4 - CRM Hospital Sao Rafael."""
from pathlib import Path
from reportlab.lib.colors import HexColor
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import ParagraphStyle, getSampleStyleSheet
from reportlab.lib.units import cm
from reportlab.platypus import (Paragraph, SimpleDocTemplate, Spacer, Table,
                                TableStyle, PageBreak)

OUT = Path(__file__).parent / "Sprint4_Entrega_CRM_Hospital.pdf"

NAVY = HexColor("#0F172A")
BLUE = HexColor("#3B82F6")
GRAY = HexColor("#64748B")
LIGHT = HexColor("#F1F5F9")

styles = getSampleStyleSheet()
title = ParagraphStyle("title", parent=styles["Title"], textColor=NAVY,
                       fontSize=22, leading=26, spaceAfter=8)
sub = ParagraphStyle("sub", parent=styles["Normal"], textColor=BLUE,
                     fontSize=14, leading=18, spaceAfter=14)
h2 = ParagraphStyle("h2", parent=styles["Heading2"], textColor=NAVY,
                    fontSize=14, leading=18, spaceBefore=12, spaceAfter=6)
body = ParagraphStyle("body", parent=styles["Normal"], textColor=NAVY,
                      fontSize=11, leading=15, spaceAfter=6, alignment=4)
small = ParagraphStyle("small", parent=styles["Normal"], textColor=GRAY,
                       fontSize=9, leading=12)

doc = SimpleDocTemplate(str(OUT), pagesize=A4,
                        leftMargin=2*cm, rightMargin=2*cm,
                        topMargin=2*cm, bottomMargin=2*cm)
story = []

story.append(Paragraph("CRM Hospital S&atilde;o Rafael", title))
story.append(Paragraph("Sprint 4 — Exposi&ccedil;&atilde;o das Regras de Neg&oacute;cio em JSON e Views", sub))

# Equipe
data = [
    ["Integrante", "RM"],
    ["Samuel Baecker", "RM98765"],
]
t = Table(data, colWidths=[10*cm, 4*cm])
t.setStyle(TableStyle([
    ("BACKGROUND", (0, 0), (-1, 0), NAVY),
    ("TEXTCOLOR", (0, 0), (-1, 0), HexColor("#FFFFFF")),
    ("FONTNAME", (0, 0), (-1, 0), "Helvetica-Bold"),
    ("ALIGN", (0, 0), (-1, -1), "LEFT"),
    ("GRID", (0, 0), (-1, -1), 0.5, GRAY),
    ("BOTTOMPADDING", (0, 0), (-1, -1), 6),
    ("TOPPADDING", (0, 0), (-1, -1), 6),
    ("BACKGROUND", (0, 1), (-1, -1), LIGHT),
]))
story.append(t)
story.append(Spacer(1, 0.6*cm))

story.append(Paragraph("Descri&ccedil;&atilde;o da Solu&ccedil;&atilde;o", h2))
descr = (
    "O <b>CRM Hospital S&atilde;o Rafael</b> &eacute; uma aplica&ccedil;&atilde;o "
    "back-end Java/Spring Boot 3 que digitaliza todo o ciclo de aquisi&ccedil;&atilde;o "
    "e relacionamento com pacientes do hospital. A solu&ccedil;&atilde;o foi modelada "
    "seguindo <b>Domain-Driven Design</b>, dividindo o c&oacute;digo em pacotes por "
    "dom&iacute;nio (lead, agendamento, intera&ccedil;&atilde;o, usu&aacute;rio, "
    "notifica&ccedil;&atilde;o e intelig&ecirc;ncia artificial). Cada dom&iacute;nio "
    "exp&otilde;e dois conjuntos de endpoints: um <b>JSONController</b> "
    "(<i>@RestController</i>) que serve a API REST consumida pelo front-end React/Vite "
    "e por integra&ccedil;&otilde;es externas, e um <b>ViewController</b> "
    "(<i>@Controller</i>) que renderiza p&aacute;ginas HTML server-side com "
    "<b>Thymeleaf</b>. As p&aacute;ginas Thymeleaf funcionam como "
    "<b>documenta&ccedil;&atilde;o viva</b> da API: cada uma cont&eacute;m um bloco "
    "listando os endpoints relacionados, verbos HTTP e c&oacute;digos de retorno "
    "esperados.<br/><br/>"
    "As principais funcionalidades incluem: cadastro de leads com "
    "c&aacute;lculo autom&aacute;tico de <i>lead score</i> e prioridade; gest&atilde;o "
    "de agendamentos com valida&ccedil;&atilde;o de conflito de hor&aacute;rio e data "
    "passada; registro de intera&ccedil;&otilde;es (liga&ccedil;&otilde;es, e-mail, "
    "WhatsApp) com detec&ccedil;&atilde;o sem&acirc;ntica de urg&ecirc;ncia via IA "
    "(Spring AI + Anthropic Claude); notifica&ccedil;&otilde;es em tempo real via "
    "WebSocket; relat&oacute;rios di&aacute;rios gerados automaticamente por IA; "
    "autentica&ccedil;&atilde;o JWT com Spring Security; e tratamento centralizado de "
    "exce&ccedil;&otilde;es por meio de <i>GlobalExceptionHandler</i> com "
    "<i>@RestControllerAdvice</i>. A arquitetura segue rigorosamente o padr&atilde;o "
    "em camadas (Controller → Service → Repository), utiliza DTOs com Bean Validation, "
    "Mappers dedicados, conversores JPA para enums e documenta&ccedil;&atilde;o "
    "OpenAPI/Swagger acess&iacute;vel em <i>/swagger-ui.html</i>."
)
story.append(Paragraph(descr, body))

story.append(Paragraph("Atendimento aos crit&eacute;rios da Sprint 4", h2))
crit = [
    ["Crit&eacute;rio", "Como foi atendido"],
    ["Endpoints separados por dom&iacute;nio (JSON + Views Thymeleaf) com links nas p&aacute;ginas",
     "6 dom&iacute;nios, cada um com @RestController em <i>controller/</i>, @Controller em <i>view/</i> "
     "e template em <i>templates/&lt;dominio&gt;/</i> contendo bloco de documenta&ccedil;&atilde;o de endpoints."],
    ["Nomenclatura de endpoints, c&oacute;digos de retorno e verbos HTTP corretos",
     "POST 201 Created, GET 200 OK, PATCH 200, DELETE 204 No Content, 404 Not Found, 409 Conflict, 422 Unprocessable. "
     "Recursos no plural (<i>/api/leads</i>, <i>/api/agendamentos</i>) conforme REST."],
    ["Funcionalidades propostas, entregues e operacionais",
     "Lead scoring, gest&atilde;o de agendamentos, intera&ccedil;&otilde;es com IA, notifica&ccedil;&otilde;es WebSocket, "
     "relat&oacute;rios IA agendados (job 7h), JWT, dashboard executivo."],
    ["Boas pr&aacute;ticas (nomenclatura, exce&ccedil;&otilde;es, design patterns)",
     "Padr&atilde;o em camadas, DI por construtor (@RequiredArgsConstructor), DTO + Mapper, "
     "GlobalExceptionHandler com 9 exce&ccedil;&otilde;es customizadas, Bean Validation, "
     "OpenAPI/Swagger, testes JUnit 5 + Mockito."],
]
t = Table(crit, colWidths=[5.5*cm, 11*cm])
t.setStyle(TableStyle([
    ("BACKGROUND", (0, 0), (-1, 0), NAVY),
    ("TEXTCOLOR", (0, 0), (-1, 0), HexColor("#FFFFFF")),
    ("FONTNAME", (0, 0), (-1, 0), "Helvetica-Bold"),
    ("VALIGN", (0, 0), (-1, -1), "TOP"),
    ("GRID", (0, 0), (-1, -1), 0.5, GRAY),
    ("BOTTOMPADDING", (0, 0), (-1, -1), 6),
    ("TOPPADDING", (0, 0), (-1, -1), 6),
    ("FONTSIZE", (0, 0), (-1, -1), 10),
    ("BACKGROUND", (0, 1), (-1, -1), LIGHT),
]))
story.append(t)

story.append(PageBreak())
story.append(Paragraph("P&aacute;ginas que comp&otilde;em a solu&ccedil;&atilde;o", h2))
paginas = [
    ["URL", "Template Thymeleaf", "Descri&ccedil;&atilde;o"],
    ["/painel", "templates/painel.html", "Dashboard executivo com KPIs e leads urgentes"],
    ["/leads", "templates/leads/lista.html", "Lista paginada de leads + cadastro + documenta&ccedil;&atilde;o REST"],
    ["/leads/{id}", "templates/leads/detalhe.html", "Detalhe do lead com hist&oacute;rico e a&ccedil;&otilde;es"],
    ["/agendamentos", "templates/agendamentos/lista.html", "Gest&atilde;o de agendamentos por per&iacute;odo"],
    ["/interacoes", "templates/interacoes/lista.html", "Hist&oacute;rico de intera&ccedil;&otilde;es e an&aacute;lise IA"],
    ["/notificacoes", "templates/notificacoes/lista.html", "Caixa de notifica&ccedil;&otilde;es do operador"],
    ["/usuarios", "templates/usuarios/lista.html", "Gest&atilde;o de usu&aacute;rios e perfis"],
    ["/ia", "templates/ia/painel.html", "Painel de IA: an&aacute;lise de leads e relat&oacute;rio di&aacute;rio"],
    ["/swagger-ui.html", "(gerado por springdoc)", "Documenta&ccedil;&atilde;o OpenAPI completa"],
]
t = Table(paginas, colWidths=[4*cm, 5.5*cm, 7*cm])
t.setStyle(TableStyle([
    ("BACKGROUND", (0, 0), (-1, 0), NAVY),
    ("TEXTCOLOR", (0, 0), (-1, 0), HexColor("#FFFFFF")),
    ("FONTNAME", (0, 0), (-1, 0), "Helvetica-Bold"),
    ("VALIGN", (0, 0), (-1, -1), "MIDDLE"),
    ("GRID", (0, 0), (-1, -1), 0.5, GRAY),
    ("FONTSIZE", (0, 0), (-1, -1), 9),
    ("ROWBACKGROUNDS", (0, 1), (-1, -1), [LIGHT, HexColor("#FFFFFF")]),
]))
story.append(t)

story.append(Spacer(1, 0.6*cm))
story.append(Paragraph("Estrutura t&eacute;cnica do projeto", h2))
tech = (
    "<b>Stack:</b> Java 17, Spring Boot 3, Spring Data JPA (Oracle), Spring Security + JWT, "
    "Spring AI (Anthropic Claude), Thymeleaf, Bootstrap 5, WebSocket (STOMP), Lombok, "
    "MapStruct (mappers manuais), JUnit 5 + Mockito, springdoc-openapi.<br/><br/>"
    "<b>Pacotes principais:</b> "
    "<i>controller/</i> (REST JSON), <i>view/</i> (Thymeleaf), <i>service/</i>, "
    "<i>repository/</i>, <i>model/</i>, <i>dto/</i>, <i>mapper/</i>, <i>exception/</i>, "
    "<i>config/</i>, <i>security/</i>, <i>ai/</i>.<br/><br/>"
    "<b>Reposit&oacute;rio:</b> https://github.com/sbaecker2005/CH-java"
)
story.append(Paragraph(tech, body))

story.append(Spacer(1, 0.4*cm))
story.append(Paragraph("Observa&ccedil;&atilde;o sobre prints", h2))
story.append(Paragraph(
    "Os prints das p&aacute;ginas que comp&otilde;em a solu&ccedil;&atilde;o devem ser "
    "anexados ao final deste documento ou capturados ap&oacute;s subir a aplica&ccedil;&atilde;o "
    "(<i>./mvnw spring-boot:run</i> dentro de <i>crm-hospital/</i>) e acessar as URLs listadas "
    "na tabela acima.", body))

doc.build(story)
print("OK ->", OUT)
