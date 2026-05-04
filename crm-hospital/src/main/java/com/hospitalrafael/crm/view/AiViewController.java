package com.hospitalrafael.crm.view;

import com.hospitalrafael.crm.repository.RelatorioIaRepository;
import com.hospitalrafael.crm.service.LeadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ia")
@RequiredArgsConstructor
public class AiViewController {

    private final LeadService leadService;
    private final RelatorioIaRepository relatorioIaRepository;

    @GetMapping
    public String painel(Model model) {
        try {
            model.addAttribute("leadsUrgentes", leadService.listarUrgentes());
            model.addAttribute("ultimoRelatorio",
                    relatorioIaRepository.findTopByOrderByGeradoEmDesc().orElse(null));
        } catch (Exception e) {
            model.addAttribute("erro", e.getMessage());
        }
        model.addAttribute("paginaAtiva", "ia");
        return "ia/painel";
    }
}
