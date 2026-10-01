package com.portal.solicitacoes.internas.service;

import com.portal.solicitacoes.internas.dto.UserCreateDTO;
import com.portal.solicitacoes.internas.dto.UserLoginDTO;
import com.portal.solicitacoes.internas.entity.User;
import com.portal.solicitacoes.internas.exception.EmailAlreadyExistsException;
import com.portal.solicitacoes.internas.repositories.UserRepository;
import com.portal.solicitacoes.internas.security.TokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private UserService userService;

    @Test
    void deveCriarUsuarioComSenhaCriptografada() {

        UserCreateDTO dto = new UserCreateDTO(
                "Marcos",
                "marcos@email.com",
                "12345678"
        );

        when(userRepository.existsByEmail(dto.email()))
                .thenReturn(false);

        when(passwordEncoder.encode(dto.password()))
                .thenReturn("senha-criptografada");

        User savedUser = new User();
        savedUser.setId(UUID.randomUUID());
        savedUser.setEmail(dto.email());
        savedUser.setUsername(dto.username());
        savedUser.setPassword("senha-criptografada");

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        User result = userService.createUser(dto);

        assertNotNull(result);
        assertEquals(dto.email(), result.getEmail());
        assertEquals("senha-criptografada", result.getPassword());

        verify(passwordEncoder).encode(dto.password());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void naoDeveCadastrarUsuarioComEmailDuplicado() {

        UserCreateDTO dto = new UserCreateDTO(
                "Marcos",
                "marcos@email.com",
                "12345678"
        );

        when(userRepository.existsByEmail(dto.email()))
                .thenReturn(true);

        EmailAlreadyExistsException exception = assertThrows(
                EmailAlreadyExistsException.class,
                () -> userService.createUser(dto)
        );

        assertEquals(
                "Email já cadastrado!",
                exception.getMessage()
        );

        verify(userRepository, never())
                .save(any(User.class));

        verify(passwordEncoder, never())
                .encode(anyString());
    }

    @Test
    void naoDeveFazerLoginComSenhaIncorreta() {

        User user = new User();

        user.setId(UUID.randomUUID());
        user.setEmail("marcos@email.com");
        user.setUsername("Marcos");
        user.setPassword("senha-criptografada");

        UserLoginDTO dto = new UserLoginDTO(
                "marcos@email.com",
                "senha-errada"
        );

        when(userRepository.findByEmail(dto.email()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                dto.password(),
                user.getPassword()
        )).thenReturn(false);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.login(dto)
        );

        assertEquals(
                "email ou senha nao correspondem",
                exception.getMessage()
        );

        verify(tokenService, never())
                .createToken(any(), any());
    }

    @Test
    void deveFazerLoginComCredenciaisCorretas() {

        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        user.setEmail("marcos@email.com");
        user.setUsername("Marcos");
        user.setPassword("senha-criptografada");

        UserLoginDTO dto = new UserLoginDTO(
                "marcos@email.com",
                "12345678"
        );

        when(userRepository.findByEmail(dto.email()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                dto.password(),
                user.getPassword()
        )).thenReturn(true);

        when(tokenService.createToken(
                user.getId(),
                user.getEmail()
        )).thenReturn("jwt-token-teste");

        String token = userService.login(dto);

        assertNotNull(token);
        assertEquals("jwt-token-teste", token);

        verify(passwordEncoder)
                .matches(dto.password(), user.getPassword());

        verify(tokenService)
                .createToken(user.getId(), user.getEmail());
    }

    @Test
    void naoDeveFazerLoginComUsuarioInexistente() {

        UserLoginDTO dto = new UserLoginDTO(
                "naoexiste@email.com",
                "12345678"
        );

        when(userRepository.findByEmail(dto.email()))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.login(dto)
        );

        assertEquals(
                "email ou senha nao correspondem",
                exception.getMessage()
        );

        verify(tokenService, never())
                .createToken(any(), any());

        verify(passwordEncoder, never())
                .matches(anyString(), anyString());
    }
}