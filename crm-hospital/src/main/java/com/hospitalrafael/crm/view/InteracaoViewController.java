package com.hospitalrafael.crm.view;

import com.hospitalrafael.crm.service.InteracaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/interacoes")
@RequiredArgsConstructor
public class InteracaoViewController {

    private final InteracaoService interacaoService;

    @GetMapping
    public String listar(Model model) {
        try {
            model.addAttribute("urgentes", interacaoService.listarUrgentes());
        } catch (Exception e) {
            model.addAttribute("erro", e.getMessage());
        }
        model.addAttribute("paginaAtiva", "interacoes");
        return "interacoes/lista";
    }
}
