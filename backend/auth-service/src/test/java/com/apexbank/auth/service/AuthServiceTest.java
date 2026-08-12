package com.apexbank.auth.service;

import com.apexbank.auth.client.AccountClient;
import com.apexbank.auth.dto.request.LoginRequest;
import com.apexbank.auth.dto.response.LoginResponse;
import com.apexbank.auth.entity.User;
import com.apexbank.auth.exception.AccountLockedException;
import com.apexbank.auth.exception.InvalidCredentialsException;
import com.apexbank.auth.repository.UserRepository;
import com.apexbank.auth.security.CustomUserDetails;
import com.apexbank.auth.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private OtpService otpService;
    @Mock private AccountClient accountClient;

    @InjectMocks
    private AuthService authService;

    private User user;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "maxLoginAttempts", 3);

        user = User.builder()
                .id(1L)
                .userId("arjun.rao")
                .accountId(1L)
                .accountNumber("100000000001")
                .fullName("Arjun Rao")
                .loginPasswordHash("hashedPassword")
                .transactionPasswordHash("hashedTxnPassword")
                .role("CUSTOMER")
                .failedLoginAttempts(0)
                .accountLocked(false)
                .build();
    }

    @Test
    void login_withValidCredentials_returnsLoginResponse() {
        LoginRequest request = new LoginRequest();
        request.setUserId("arjun.rao");
        request.setPassword("Passw0rd!");

        when(userRepository.findByUserId("arjun.rao")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Passw0rd!", "hashedPassword")).thenReturn(true);

        CustomUserDetails principal = new CustomUserDetails(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("mock-jwt-token");
        when(jwtTokenProvider.getExpirationMs()).thenReturn(3600000L);

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("mock-jwt-token", response.getToken());
        assertEquals("arjun.rao", response.getUserId());
        verify(userRepository).save(argThat(u -> u.getFailedLoginAttempts() == 0));
    }

    @Test
    void login_withInvalidPassword_incrementsFailedAttemptsAndThrows() {
        LoginRequest request = new LoginRequest();
        request.setUserId("arjun.rao");
        request.setPassword("WrongPassword");

        when(userRepository.findByUserId("arjun.rao")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("WrongPassword", "hashedPassword")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));

        verify(userRepository).save(argThat(u -> u.getFailedLoginAttempts() == 1 && !u.getAccountLocked()));
    }

    @Test
    void login_afterThreeFailedAttempts_locksAccount() {
        user.setFailedLoginAttempts(2);

        LoginRequest request = new LoginRequest();
        request.setUserId("arjun.rao");
        request.setPassword("WrongPassword");

        when(userRepository.findByUserId("arjun.rao")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("WrongPassword", "hashedPassword")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));

        verify(userRepository).save(argThat(u -> u.getFailedLoginAttempts() == 3 && u.getAccountLocked()));
    }

    @Test
    void login_whenAccountAlreadyLocked_throwsAccountLockedException() {
        user.setAccountLocked(true);

        LoginRequest request = new LoginRequest();
        request.setUserId("arjun.rao");
        request.setPassword("Passw0rd!");

        when(userRepository.findByUserId("arjun.rao")).thenReturn(Optional.of(user));

        assertThrows(AccountLockedException.class, () -> authService.login(request));
        verify(passwordEncoder, never()).matches(any(), any());
    }
}
