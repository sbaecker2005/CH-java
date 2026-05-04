package com.hospitalrafael.crm.controller;

import com.hospitalrafael.crm.dto.auth.LoginRequest;
import com.hospitalrafael.crm.dto.auth.LoginResponse;
import com.hospitalrafael.crm.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Login e geração de token JWT")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(
        summary = "Realizar login",
        description = "Autentica o usuário com e-mail e senha, retornando um token JWT Bearer válido por 24 horas"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login realizado — token JWT retornado"),
        @ApiResponse(responseCode = "401", description = "Credenciais inválidas"),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    })
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            return ResponseEntity.ok(authService.autenticar(request));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).build();
        }
    }
}
