package com.mss301.msbrand_se183225.externalsecurity;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
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

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@RequiredArgsConstructor
@SuppressWarnings("CallToPrintStackTrace")
public class AuthenticationFilter extends OncePerRequestFilter {

    private final RestTemplate restTemplate;
    private final AntPathMatcher antPathMatcher;
    private final String authenticationUrl;
    private final ObjectMapper objectMapper;

    private final List<String> PUBLIC_PATHS = List.of(
            "/v3/api-docs/**",
            "/swagger-ui/**",
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
            // SWAGGER redirection
            if (request.getServletPath().equals("/")) {
                response.sendRedirect(request.getContextPath() + "/swagger-ui/index.html");
                return;
            }
            // PUBLIC path checking
            final String authorizationHeader = request.getHeader(AUTHORIZATION);
            if (isPublicPath(request)) {
                filterChain.doFilter(request, response);
                return;
            }
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                throw new RuntimeException("Missing required token");
            }
            String token = authorizationHeader.substring(7);
            ValidationResponse validationResponse = restTemplate.postForObject(
                    authenticationUrl,
                    Map.of("token", token),
                    ValidationResponse.class
            );
            if (validationResponse == null || !validationResponse.valid()) {
                throw new RuntimeException("Invalid credentials");
            }
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    validationResponse.username(),
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + validationResponse.role()))
            );
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        } catch (Exception e) {
            e.printStackTrace();
            buildResponse(response, "Unauthorized: " + e.getMessage());
            return;
        }
        filterChain.doFilter(request, response);
    }

    private void buildResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> apiResponse = Map.of(
                "response", HttpStatus.UNAUTHORIZED.value(),
                "error", message
        );

        String jsonResponse = objectMapper.writeValueAsString(apiResponse);
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
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
