package com.hospitalrafael.crm.service;

import com.hospitalrafael.crm.dto.usuario.UsuarioRequest;
import com.hospitalrafael.crm.dto.usuario.UsuarioResponse;
import com.hospitalrafael.crm.exception.CpfInvalidoException;
import com.hospitalrafael.crm.exception.DadosInvalidosException;
import com.hospitalrafael.crm.exception.EmailDuplicadoException;
import com.hospitalrafael.crm.exception.RecursoNaoEncontradoException;
import com.hospitalrafael.crm.mapper.UsuarioMapper;
import com.hospitalrafael.crm.model.Usuario;
import com.hospitalrafael.crm.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Serviço responsável pelas regras de negócio relacionadas a Usuários/Operadores.
 *
 * Responsabilidades:
 *  - Cadastro com validação de CPF e email únicos
 *  - Hash de senha com BCrypt
 *  - Atualização de dados
 *  - Remoção segura
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    // ─── REGRA DE NEGÓCIO 1: Cadastro com validação de CPF e email único ──────

    /**
     * Cadastra um novo usuário/operador.
     *
     * Regras aplicadas:
     *  - CPF deve ter exatamente 11 dígitos numéricos
     *  - Email não pode ser duplicado no sistema
     *  - CPF não pode ser duplicado no sistema
     *  - Senha é armazenada como hash BCrypt
     */
    public UsuarioResponse cadastrar(UsuarioRequest request) {
        validarCpf(request.getCpf());
        validarEmailUnico(request.getEmail());
        validarCpfUnico(request.getCpf());

        Usuario usuario = usuarioMapper.toEntity(request);
        usuario.setSenha(passwordEncoder.encode(request.getSenha()));
        if (usuario.getRole() == null) usuario.setRole("OPERADOR");

        log.info("Cadastrando usuário: {}", request.getEmail());
        return usuarioMapper.toResponse(usuarioRepository.save(usuario));
    }

    // ─── REGRA DE NEGÓCIO 2: Atualização de dados do usuário ─────────────────

    /**
     * Atualiza os dados de um usuário existente.
     * Garante que o novo email não conflite com outro usuário.
     */
    public UsuarioResponse atualizar(Long id, UsuarioRequest dados) {
        Usuario existente = buscarEntidadePorId(id);

        if (!existente.getEmail().equalsIgnoreCase(dados.getEmail())) {
            validarEmailUnico(dados.getEmail());
        }

        existente.setNome(dados.getNome());
        existente.setEmail(dados.getEmail());
        existente.setTelefone(dados.getTelefone());
        if (dados.getSenha() != null && !dados.getSenha().isBlank()) {
            existente.setSenha(passwordEncoder.encode(dados.getSenha()));
        }

        return usuarioMapper.toResponse(usuarioRepository.save(existente));
    }

    // ─── REGRA DE NEGÓCIO 3: Remoção segura ──────────────────────────────────

    public void remover(Long id) {
        log.info("Removendo usuário id={}", id);
        usuarioRepository.delete(buscarEntidadePorId(id));
    }

    // ─── Consultas ────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Long id) {
        return usuarioMapper.toResponse(buscarEntidadePorId(id));
    }

    @Transactional(readOnly = true)
    public Page<UsuarioResponse> listar(Pageable pageable) {
        return usuarioRepository.findAll(pageable).map(usuarioMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(usuarioMapper::toResponse)
                .toList();
    }

    // ─── Validações privadas ──────────────────────────────────────────────────

    private void validarCpf(String cpf) {
        if (cpf == null || !cpf.matches("\\d{11}")) {
            throw new CpfInvalidoException(cpf);
        }
    }

    private void validarEmailUnico(String email) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new EmailDuplicadoException(email);
        }
    }

    private void validarCpfUnico(String cpf) {
        if (usuarioRepository.existsByCpf(cpf)) {
            throw new DadosInvalidosException("CPF " + cpf + " já está cadastrado no sistema.");
        }
    }

    private Usuario buscarEntidadePorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário", id));
    }
}
