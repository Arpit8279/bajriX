package com.bajriX.bajriX.service;

import com.bajriX.bajriX.dto.TokenClaims;
import com.bajriX.bajriX.entity.User;
import com.bajriX.bajriX.exception.AuthenticationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;

@Service
public class TokenService {

    private final String secretKey;
    private final ObjectMapper objectMapper;
    private final long expirationSeconds = 7 * 24 * 60 * 60; // 7 days

    public TokenService(
            @Value("${app.jwt.secret:bajrix-super-secret-key-for-marketplace-authentication-2026-secure}") String secretKey) {
        this.secretKey = secretKey;
        this.objectMapper = new ObjectMapper();
    }

    public String generateToken(User user) {
        try {
            long exp = Instant.now().getEpochSecond() + expirationSeconds;
            Long sellerId = user.getSeller() != null ? user.getSeller().getId() : null;
            TokenClaims claims = new TokenClaims(user.getId(), user.getEmail(), user.getRole().name(), sellerId, exp);

            String headerJson = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
            String payloadJson = objectMapper.writeValueAsString(claims);

            String encodedHeader = Base64.getUrlEncoder().withoutPadding().encodeToString(headerJson.getBytes(StandardCharsets.UTF_8));
            String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));
            String dataToSign = encodedHeader + "." + encodedPayload;

            String signature = sign(dataToSign);
            return dataToSign + "." + signature;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate token", e);
        }
    }

    public TokenClaims parseToken(String token) {
        if (token == null || token.isBlank()) {
            throw new AuthenticationException("Missing authentication token");
        }

        if (token.startsWith("Bearer ") || token.startsWith("bearer ")) {
            token = token.substring(7).trim();
        }

        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new AuthenticationException("Malformed authentication token");
        }

        String dataToSign = parts[0] + "." + parts[1];
        String expectedSignature = sign(dataToSign);

        if (!MessageDigest.isEqual(expectedSignature.getBytes(StandardCharsets.UTF_8), parts[2].getBytes(StandardCharsets.UTF_8))) {
            throw new AuthenticationException("Invalid token signature");
        }

        try {
            byte[] payloadBytes = Base64.getUrlDecoder().decode(parts[1]);
            TokenClaims claims = objectMapper.readValue(payloadBytes, TokenClaims.class);

            if (Instant.now().getEpochSecond() > claims.exp()) {
                throw new AuthenticationException("Token has expired. Please log in again.");
            }

            return claims;
        } catch (AuthenticationException ae) {
            throw ae;
        } catch (Exception e) {
            throw new AuthenticationException("Failed to parse token claims");
        }
    }

    private String sign(String data) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmac.init(secretKeySpec);
            byte[] signatureBytes = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(signatureBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error calculating HMAC signature", e);
        }
    }
}
