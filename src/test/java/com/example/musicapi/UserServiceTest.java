package com.example.musicapi.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.musicapi.dto.UserResponse;
import com.example.musicapi.dto.UserUpdateRequest;
import com.example.musicapi.model.User;
import com.example.musicapi.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    /**
     * Testa a recuperação de todos os usuários, validando o mapeamento para
     * DTOs.
     */
    @Test
    void getAllUsers_ShouldReturnList() {
        User user = new User();
        user.setId(1L);
        user.setUsername("user1");
        user.setRole(User.Role.USER);

        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserResponse> result = userService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals("user1", result.get(0).getUsername());
    }

    /**
     * Testa a atualização de um usuário, verificando especificamente se a senha
     * é codificada antes de salvar.
     */
    @Test
    void updateUser_ShouldUpdatePassword() {
        User user = new User();
        user.setId(1L);
        user.setUsername("oldUser");
        user.setPassword("oldPass");

        UserUpdateRequest request = new UserUpdateRequest();
        request.setPassword("newPass");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNewPass");
        when(userRepository.save(ArgumentMatchers.any(User.class))).thenReturn(user);

        UserResponse result = userService.updateUser(1L, request);

        verify(passwordEncoder).encode("newPass");
        verify(userRepository).save(user);
    }
}
