package com.demo.rbac.config.security.filters;


import com.demo.rbac.services.auth.JwtUtilsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

import static com.demo.rbac.services.auth.JwtUtilsService.BEARER;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JwtUtilsService jwtUtilsService;

    private static boolean isBearerToken(String authorizationToken) {
        return StringUtils.startsWith(authorizationToken, BEARER);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest req, HttpServletResponse res, FilterChain filterChain
    ) throws ServletException, IOException {
        String authorizationHeader = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isBlank(authorizationHeader)) {
            // If authorization header is not present let the filter chain handle
            // Possible happy scenario: endpoints which does not require authentication
            filterChain.doFilter(req, res);
            return;
        }
        if (isBearerToken(authorizationHeader)) {
            String bearerToken = jwtUtilsService.extractBearerToken(authorizationHeader);
            validateBearerTokenNSetAuthenticatedPrincipal(bearerToken, res);
        } else {
            log.info("Not a bearer token!");
            res.setStatus(HttpStatus.UNAUTHORIZED.value());
        }
        if (res.getStatus() != HttpStatus.UNAUTHORIZED.value()) {
            filterChain.doFilter(req, res);
        }
    }

    private void validateBearerTokenNSetAuthenticatedPrincipal(String jwt, HttpServletResponse res) {
        try {
            Jws<Claims> parsedJwt = jwtUtilsService.parseToken(jwt);
            if (jwtUtilsService.isTokenValid(parsedJwt)) {
                Claims claims = parsedJwt.getPayload();
                String username = claims.getSubject();
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        username, StringUtils.EMPTY, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                res.setStatus(HttpStatus.UNAUTHORIZED.value());
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Exception while validating bearer token: {}", e.toString());
            res.setStatus(HttpStatus.UNAUTHORIZED.value());
        }
    }
}
