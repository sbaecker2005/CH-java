package com.hospitalrafael.crm.view;

import com.hospitalrafael.crm.service.NotificacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/notificacoes")
@RequiredArgsConstructor
public class NotificacaoViewController {

    private final NotificacaoService notificacaoService;

    @GetMapping
    public String listar(Model model) {
        try {
            model.addAttribute("notificacoes", notificacaoService.listarTodas());
            model.addAttribute("naoLidas", notificacaoService.contarNaoLidas());
        } catch (Exception e) {
            model.addAttribute("erro", e.getMessage());
        }
        model.addAttribute("paginaAtiva", "notificacoes");
        return "notificacoes/lista";
    }
}
