package com.bajriX.bajriX.config;

import com.bajriX.bajriX.dto.TokenClaims;
import com.bajriX.bajriX.exception.AuthenticationException;
import com.bajriX.bajriX.exception.ForbiddenException;
import com.bajriX.bajriX.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SellerAuthInterceptor implements HandlerInterceptor {

    private final TokenService tokenService;

    public SellerAuthInterceptor(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || authHeader.isBlank()) {
            throw new AuthenticationException("Authentication required. Please log in.");
        }

        TokenClaims claims = tokenService.parseToken(authHeader);

        if (!"SELLER".equalsIgnoreCase(claims.role())) {
            throw new ForbiddenException("Access restricted: You are logged in as a Buyer. Only sellers can access this portal.");
        }

        if (claims.sellerId() == null) {
            throw new ForbiddenException("Access restricted: No seller profile linked to this account.");
        }

        String xSellerId = request.getHeader("X-Seller-Id");
        if (xSellerId != null && !xSellerId.isBlank()) {
            try {
                Long requestedSellerId = Long.parseLong(xSellerId.trim());
                if (!claims.sellerId().equals(requestedSellerId)) {
                    throw new ForbiddenException("Access denied: You cannot operate on another seller's inventory.");
                }
            } catch (NumberFormatException e) {
                throw new AuthenticationException("Invalid X-Seller-Id header format");
            }
        }

        request.setAttribute("authenticatedUser", claims);
        return true;
    }
}
