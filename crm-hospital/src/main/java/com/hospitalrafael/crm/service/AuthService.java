package com.hospitalrafael.crm.service;

import com.hospitalrafael.crm.dto.auth.LoginRequest;
import com.hospitalrafael.crm.dto.auth.LoginResponse;
import com.hospitalrafael.crm.exception.RecursoNaoEncontradoException;
import com.hospitalrafael.crm.model.Usuario;
import com.hospitalrafael.crm.repository.UsuarioRepository;
import com.hospitalrafael.crm.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    /**
     * Autentica o usuário e retorna um JWT.
     *
     * Usa o principal retornado pelo AuthenticationManager para evitar
     * uma segunda chamada ao banco de dados desnecessária.
     */
    @Transactional(readOnly = true)
    public LoginResponse autenticar(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha())
        );

        // UserDetails já está disponível no principal — sem segunda query ao banco
        UserDetails userDetails = (UserDetails) auth.getPrincipal();

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário", 0L));

        String token = jwtService.generateToken(userDetails, usuario.getRole());

        log.info("Login realizado: {}", request.getEmail());
        return new LoginResponse(
            token,
            "Bearer",
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail(),
            usuario.getRole()
        );
    }
}
