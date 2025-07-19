package com.mss301.msbrand_se183225.externalsecurity;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
public class AuthenticationFilter extends OncePerRequestFilter {

    private final RestTemplate restTemplate;
    private final AntPathMatcher antPathMatcher;
    private final String authenticationUrl;
    private final HandlerExceptionResolver handlerExceptionResolver;

    public AuthenticationFilter(
            RestTemplate restTemplate,
            AntPathMatcher antPathMatcher,
            String authenticationUrl,
            ObjectMapper objectMapper,
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver handlerExceptionResolver
    ) {
        this.restTemplate = restTemplate;
        this.antPathMatcher = antPathMatcher;
        this.authenticationUrl = authenticationUrl;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    private final List<String> PUBLIC_PATHS = List.of(
            "/api/public/**"
    );

    @Builder
    record ValidationResponse(
            boolean valid,
            String username,
            String role
    ) {
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            // PUBLIC path checking
            final String authorizationHeader = request.getHeader(AUTHORIZATION);
            if (isPublicPath(request)) {
                filterChain.doFilter(request, response);
                return;
            }
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                throw new UnauthorizedException("Missing required token");
            }
            String token = authorizationHeader.substring(7);
            ValidationResponse validationResponse = restTemplate.postForObject(
                    authenticationUrl,
                    Map.of("token", token),
                    ValidationResponse.class
            );
            if (validationResponse == null || !validationResponse.valid()) {
                throw new UnauthorizedException("Invalid credentials");
            }
            CustomPrincipal principal = CustomPrincipal.builder()
                    .username(validationResponse.username())
                    .role(validationResponse.role())
                    .token(token)
                    .build();
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    principal,
                    null,
                    List.of(new SimpleGrantedAuthority(validationResponse.role()))
            );
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        } catch (Exception e) {
            handlerExceptionResolver.resolveException(request, response, null, e);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private boolean isPublicPath(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();

        String pathToMatch;
        if (contextPath != null && !contextPath.isEmpty() && requestURI.startsWith(contextPath)) {
            pathToMatch = requestURI.substring(contextPath.length());
        } else {
            pathToMatch = requestURI;
        }

        return PUBLIC_PATHS.stream().anyMatch(pattern -> antPathMatcher.match(pattern, pathToMatch));
    }
}
