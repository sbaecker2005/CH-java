package com.hospitalrafael.crm.view;

import com.hospitalrafael.crm.service.LeadService;
import com.hospitalrafael.crm.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/leads")
@RequiredArgsConstructor
public class LeadViewController {

    private final LeadService leadService;
    private final UsuarioService usuarioService;

    @GetMapping
    public String listar(Model model) {
        try {
            model.addAttribute("leads", leadService.listarPorPrioridade());
            model.addAttribute("usuarios", usuarioService.listarTodos());
        } catch (Exception e) {
            model.addAttribute("erro", e.getMessage());
        }
        model.addAttribute("paginaAtiva", "leads");
        return "leads/lista";
    }

    @GetMapping("/{id}")
    public String detalhe(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("lead", leadService.buscarPorId(id));
        } catch (Exception e) {
            model.addAttribute("erro", e.getMessage());
        }
        model.addAttribute("paginaAtiva", "leads");
        return "leads/detalhe";
    }
}
