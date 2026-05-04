package com.hospitalrafael.crm.service;

import com.hospitalrafael.crm.dto.usuario.UsuarioRequest;
import com.hospitalrafael.crm.dto.usuario.UsuarioResponse;
import com.hospitalrafael.crm.exception.CpfInvalidoException;
import com.hospitalrafael.crm.exception.EmailDuplicadoException;
import com.hospitalrafael.crm.exception.RecursoNaoEncontradoException;
import com.hospitalrafael.crm.mapper.UsuarioMapper;
import com.hospitalrafael.crm.model.Usuario;
import com.hospitalrafael.crm.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioService - Testes Unitários")
class UsuarioServiceTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private UsuarioMapper usuarioMapper;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private UsuarioService usuarioService;

    private UsuarioRequest requestBase;
    private Usuario usuarioBase;
    private UsuarioResponse responseBase;

    @BeforeEach
    void setUp() {
        requestBase = new UsuarioRequest();
        requestBase.setNome("Carlos Silva");
        requestBase.setEmail("carlos@hospital.com");
        requestBase.setSenha("Senha@123");
        requestBase.setCpf("12345678901");
        requestBase.setTelefone("11999990001");
        requestBase.setDataNasc(LocalDate.of(1990, 5, 15));

        usuarioBase = new Usuario();
        usuarioBase.setId(1L);
        usuarioBase.setNome("Carlos Silva");
        usuarioBase.setEmail("carlos@hospital.com");
        usuarioBase.setSenha("hash");
        usuarioBase.setCpf("12345678901");
        usuarioBase.setRole("OPERADOR");

        responseBase = new UsuarioResponse();
        responseBase.setId(1L);
        responseBase.setNome("Carlos Silva");
        responseBase.setEmail("carlos@hospital.com");
    }

    @Test
    @DisplayName("Deve cadastrar usuário com CPF e email válidos")
    void deveCadastrarComSucesso() {
        when(usuarioRepository.existsByEmail(any())).thenReturn(false);
        when(usuarioRepository.existsByCpf(any())).thenReturn(false);
        when(usuarioMapper.toEntity(any())).thenReturn(usuarioBase);
        when(passwordEncoder.encode(any())).thenReturn("hash-bcrypt");
        when(usuarioRepository.save(any())).thenReturn(usuarioBase);
        when(usuarioMapper.toResponse(any())).thenReturn(responseBase);

        UsuarioResponse resultado = usuarioService.cadastrar(requestBase);

        assertNotNull(resultado);
        assertEquals("Carlos Silva", resultado.getNome());
        verify(usuarioRepository).save(any());
        verify(passwordEncoder).encode("Senha@123");
    }

    @Test
    @DisplayName("Deve lançar CpfInvalidoException para CPF com menos de 11 dígitos")
    void deveLancarExceptionParaCpfCurto() {
        requestBase.setCpf("123");
        assertThrows(CpfInvalidoException.class, () -> usuarioService.cadastrar(requestBase));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar CpfInvalidoException para CPF com letras")
    void deveLancarExceptionParaCpfComLetras() {
        requestBase.setCpf("1234567890A");
        assertThrows(CpfInvalidoException.class, () -> usuarioService.cadastrar(requestBase));
    }

    @Test
    @DisplayName("Deve lançar EmailDuplicadoException para email já existente")
    void deveLancarExceptionParaEmailDuplicado() {
        when(usuarioRepository.existsByEmail(any())).thenReturn(true);
        assertThrows(EmailDuplicadoException.class, () -> usuarioService.cadastrar(requestBase));
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException para ID inexistente")
    void deveLancarExceptionParaIdInexistente() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RecursoNaoEncontradoException.class, () -> usuarioService.buscarPorId(99L));
    }

    @Test
    @DisplayName("Deve listar todos os usuários")
    void deveListarTodos() {
        when(usuarioRepository.findAll()).thenReturn(List.of(usuarioBase));
        when(usuarioMapper.toResponse(any())).thenReturn(responseBase);

        var resultado = usuarioService.listarTodos();
        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("Deve remover usuário existente")
    void deveRemoverComSucesso() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioBase));
        doNothing().when(usuarioRepository).delete(usuarioBase);

        assertDoesNotThrow(() -> usuarioService.remover(1L));
        verify(usuarioRepository).delete(usuarioBase);
    }
}
