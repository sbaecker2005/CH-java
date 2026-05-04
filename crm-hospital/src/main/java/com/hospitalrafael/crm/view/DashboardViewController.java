package com.hospitalrafael.crm.view;

import com.hospitalrafael.crm.service.LeadService;
import com.hospitalrafael.crm.service.NotificacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardViewController {

    private final LeadService leadService;
    private final NotificacaoService notificacaoService;

    @GetMapping("/")
    public String home() {
        return "redirect:/painel";
    }

    @GetMapping("/painel")
    public String dashboard(Model model) {
        try {
            model.addAttribute("dashboard", leadService.getDashboard());
            model.addAttribute("leadsUrgentes", leadService.listarUrgentes());
            model.addAttribute("notificacoesNaoLidas", notificacaoService.contarNaoLidas());
        } catch (Exception e) {
            model.addAttribute("erro", "Não foi possível carregar os dados: " + e.getMessage());
        }
        model.addAttribute("paginaAtiva", "painel");
        return "painel";
    }
}
