package com.revshop_backend;

import com.revshop_backend.dto.LoginResponse;
import com.revshop_backend.dto.RegisterRequest;
import com.revshop_backend.exception.InvalidCredentialsException;
import com.revshop_backend.model.Role;
import com.revshop_backend.model.User;
import com.revshop_backend.repository.AddressRepository;
import com.revshop_backend.repository.PasswordOtpRepository;
import com.revshop_backend.repository.UserRepository;
import com.revshop_backend.security.JwtUtil;
import com.revshop_backend.services.implementations.AuthServiceImpl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private PasswordOtpRepository passwordOtpRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;

    @Before
    public void setup() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@gmail.com");
        user.setPassword("encodedPassword");
        user.setRole(Role.BUYER);
    }

    @Test
    public void testRegisterSuccess() {

        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@gmail.com");
        request.setPassword("123456");
        request.setRole("BUYER");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        String result = authService.register(request);

        assertEquals("User registered successfully!", result);
        verify(userRepository, times(1)).save(any(User.class));
    }
    @Test
    public void testRegisterEmailAlreadyExists() {

        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@gmail.com");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        String result = authService.register(request);

        assertEquals("Email already registered!", result);
    }
    @Test
    public void testLoginSuccess() {

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("123456", user.getPassword()))
                .thenReturn(true);

        when(jwtUtil.generateToken(user.getEmail(), user.getRole().name()))
                .thenReturn("mockedToken");

        LoginResponse response = authService.login("test@gmail.com", "123456");

        assertNotNull(response);
        assertEquals("mockedToken", response.getToken());
        assertEquals("BUYER", response.getRole());
    }

    @Test(expected = InvalidCredentialsException.class)
    public void testLoginUserNotFound() {

        when(userRepository.findByEmail("wrong@gmail.com"))
                .thenReturn(Optional.empty());

        authService.login("wrong@gmail.com", "123456");
    }
    @Test(expected = InvalidCredentialsException.class)
    public void testLoginInvalidPassword() {

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("wrong", user.getPassword()))
                .thenReturn(false);

        authService.login("test@gmail.com", "wrong");
    }
}