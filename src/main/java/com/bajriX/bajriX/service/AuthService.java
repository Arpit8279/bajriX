package com.bajriX.bajriX.service;

import com.bajriX.bajriX.dto.TokenClaims;
import com.bajriX.bajriX.dto.request.LoginRequest;
import com.bajriX.bajriX.dto.response.AuthResponse;
import com.bajriX.bajriX.dto.response.UserResponse;
import com.bajriX.bajriX.entity.User;
import com.bajriX.bajriX.exception.AuthenticationException;
import com.bajriX.bajriX.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthService(UserRepository userRepository, TokenService tokenService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email().trim().toLowerCase())
                .orElseThrow(() -> new AuthenticationException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new AuthenticationException("Invalid email or password");
        }

        String token = tokenService.generateToken(user);
        Long sellerId = user.getSeller() != null ? user.getSeller().getId() : null;
        String businessName = user.getSeller() != null ? user.getSeller().getBusinessName() : null;

        return new AuthResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole().name(),
                sellerId,
                businessName,
                token
        );
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(String authHeader) {
        TokenClaims claims = tokenService.parseToken(authHeader);
        User user = userRepository.findById(claims.userId())
                .orElseThrow(() -> new AuthenticationException("User account not found"));

        Long sellerId = user.getSeller() != null ? user.getSeller().getId() : null;
        String businessName = user.getSeller() != null ? user.getSeller().getBusinessName() : null;

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole().name(),
                sellerId,
                businessName
        );
    }
}
