package com.hospitalrafael.crm;

import com.hospitalrafael.crm.dto.lead.LeadRequest;
import com.hospitalrafael.crm.dto.usuario.UsuarioRequest;
import com.hospitalrafael.crm.service.LeadService;
import com.hospitalrafael.crm.service.UsuarioService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDate;

/**
 * Aplicação principal do CRM - Hospital São Rafael
 *
 * Challenge Sprint 4 (2ESPR)
 * Integrantes:
 *   - Alexandre Delfino  RM560059
 *   - Enzo Luciano       RM559557
 *   - Luigi Thiengo      RM560755
 *   - Pedro Claudino     RM561023
 *   - Samuel Backer      RM559269
 *
 * Tecnologias:
 *   - Spring Boot 3.2 + JPA + Oracle/H2
 *   - Spring AI — análise semântica de leads
 *   - Spring Security + JWT
 *   - WebSocket STOMP — notificações em tempo real
 *   - Flyway — migrações de banco de dados
 *   - MapStruct + Lombok
 *   - SpringDoc OpenAPI (Swagger UI: /swagger-ui.html)
 *   - React + TypeScript (frontend em /frontend, build integrado ao Maven)
 */
@SpringBootApplication
@EnableScheduling
public class CrmHospitalApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrmHospitalApplication.class, args);
    }

    /**
     * ============================================================
     * ZONA DE TESTES — Executa no startup quando app.demo-mode=true
     * Demonstra as principais regras de negócio da aplicação
     * ============================================================
     */
    @Bean
    CommandLineRunner zonaDeTestes(UsuarioService usuarioService, LeadService leadService) {
        return args -> {
            // Só executa se app.demo-mode=true
            String demoMode = System.getProperty("app.demo-mode",
                    System.getenv().getOrDefault("APP_DEMO_MODE", "false"));
            if (!"true".equalsIgnoreCase(demoMode)) return;

            System.out.println("\n╔══════════════════════════════════════════════╗");
            System.out.println("║   ZONA DE TESTES — CRM Hospital São Rafael   ║");
            System.out.println("╚══════════════════════════════════════════════╝\n");

            // ── TESTE 1: Criação de Usuário/Operador ─────────────────────────
            System.out.println("── TESTE 1: Criação de Usuário ──");
            try {
                UsuarioRequest req = new UsuarioRequest();
                req.setNome("Operador Teste");
                req.setEmail("teste.operador@hospital.com");
                req.setSenha("Senha@123");
                req.setCpf("11122233344");
                req.setTelefone("11988880001");
                req.setDataNasc(LocalDate.of(1990, 1, 15));
                var salvo = usuarioService.cadastrar(req);
                System.out.println("[OK] Usuário criado: " + salvo.getNome() + " | ID: " + salvo.getId());
            } catch (Exception e) {
                System.out.println("[ERRO] " + e.getMessage());
            }

            // ── TESTE 2: Cadastro de Lead com cálculo de Score ───────────────
            System.out.println("\n── TESTE 2: Cadastro de Lead com Lead Score ──");
            try {
                LeadRequest req = new LeadRequest();
                req.setNome("Paciente Teste");
                req.setEmail("paciente.teste@email.com");
                req.setTelefone("11977771111");
                req.setCanalOrigem("Indicação");
                req.setPlanoSaude("Unimed");
                req.setProcedimentoInteresse("Cardiologia");
                req.setFatorUrgencia(true);
                var lead = leadService.cadastrar(req);
                System.out.println("[OK] Lead criado: " + lead.getNome()
                        + " | Score: " + lead.getLeadScore()
                        + " | Prioridade: " + lead.getPrioridade());
            } catch (Exception e) {
                System.out.println("[ERRO] " + e.getMessage());
            }

            // ── TESTE 3: Validação de Lead duplicado ─────────────────────────
            System.out.println("\n── TESTE 3: Lead duplicado (deve lançar exception) ──");
            try {
                LeadRequest dup = new LeadRequest();
                dup.setNome("Paciente Teste");
                dup.setEmail("paciente.teste@email.com");
                dup.setTelefone("11977771111");
                leadService.cadastrar(dup);
                System.out.println("[FALHOU] Deveria ter lançado LeadDuplicadoException");
            } catch (Exception e) {
                System.out.println("[OK] Exception capturada: " + e.getMessage());
            }

            // ── TESTE 4: Listagem de Leads por Prioridade ────────────────────
            System.out.println("\n── TESTE 4: Listagem por prioridade ──");
            try {
                var leads = leadService.listarPorPrioridade();
                System.out.println("[OK] Leads ordenados: " + leads.size() + " encontrado(s)");
                leads.stream().limit(3).forEach(l ->
                        System.out.println("   > " + l.getNome() + " | Score: " + l.getLeadScore()));
            } catch (Exception e) {
                System.out.println("[ERRO] " + e.getMessage());
            }

            // ── TESTE 5: Leads urgentes ──────────────────────────────────────
            System.out.println("\n── TESTE 5: Busca de leads urgentes ──");
            try {
                var urgentes = leadService.listarUrgentes();
                System.out.println("[OK] Leads urgentes: " + urgentes.size());
            } catch (Exception e) {
                System.out.println("[ERRO] " + e.getMessage());
            }

            // ── TESTE 6: Validação de CPF inválido ──────────────────────────
            System.out.println("\n── TESTE 6: Validação de CPF inválido ──");
            try {
                UsuarioRequest inv = new UsuarioRequest();
                inv.setNome("Teste");
                inv.setEmail("cpf.invalido@email.com");
                inv.setSenha("Senha@123");
                inv.setCpf("123"); // CPF inválido — deve lançar CpfInvalidoException
                inv.setTelefone("11999999999");
                inv.setDataNasc(LocalDate.of(1990, 1, 1));
                usuarioService.cadastrar(inv);
                System.out.println("[FALHOU] Deveria ter lançado CpfInvalidoException");
            } catch (Exception e) {
                System.out.println("[OK] Exception capturada: " + e.getMessage());
            }

            // ── TESTE 7: Dashboard ───────────────────────────────────────────
            System.out.println("\n── TESTE 7: Dashboard com métricas ──");
            try {
                var dashboard = leadService.getDashboard();
                System.out.println("[OK] Total leads: " + dashboard.getTotalLeads()
                        + " | Urgentes: " + dashboard.getLeadsUrgentes()
                        + " | Taxa de conversão: " + dashboard.getTaxaConversao() + "%");
            } catch (Exception e) {
                System.out.println("[ERRO] " + e.getMessage());
            }

            System.out.println("\n╔══════════════════════════════════════════╗");
            System.out.println("║          FIM DA ZONA DE TESTES           ║");
            System.out.println("╚══════════════════════════════════════════╝\n");
        };
    }
}
