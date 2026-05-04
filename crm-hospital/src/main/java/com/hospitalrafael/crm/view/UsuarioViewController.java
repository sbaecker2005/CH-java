package com.hospitalrafael.crm.view;

import com.hospitalrafael.crm.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioViewController {

    private final UsuarioService usuarioService;

    @GetMapping
    public String listar(Model model) {
        try {
            model.addAttribute("usuarios", usuarioService.listarTodos());
        } catch (Exception e) {
            model.addAttribute("erro", e.getMessage());
        }
        model.addAttribute("paginaAtiva", "usuarios");
        return "usuarios/lista";
    }
}
