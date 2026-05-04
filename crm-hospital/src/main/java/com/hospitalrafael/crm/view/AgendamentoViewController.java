package com.hospitalrafael.crm.view;

import com.hospitalrafael.crm.service.AgendamentoService;
import com.hospitalrafael.crm.service.LeadService;
import com.hospitalrafael.crm.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/agendamentos")
@RequiredArgsConstructor
public class AgendamentoViewController {

    private final AgendamentoService agendamentoService;
    private final LeadService leadService;
    private final UsuarioService usuarioService;

    @GetMapping
    public String listar(Model model) {
        try {
            model.addAttribute("agendamentos", agendamentoService.listarTodos());
            model.addAttribute("leads", leadService.listarTodos());
            model.addAttribute("usuarios", usuarioService.listarTodos());
        } catch (Exception e) {
            model.addAttribute("erro", e.getMessage());
        }
        model.addAttribute("paginaAtiva", "agendamentos");
        return "agendamentos/lista";
    }
}
